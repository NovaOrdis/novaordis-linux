/*
 * Copyright (c) 2018 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.linux.collector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicReference;

import io.novaordis.linux.PerProcessStat;
import io.novaordis.linux.ProcStat;
import io.novaordis.linux.collector.command.Command;
import io.novaordis.utilities.UserErrorException;
import io.novaordis.utilities.parsing.ParsingException;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 3/1/18
 */
public class Collector {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Configuration conf;

    private volatile boolean doRun;

    private File outputFile;

    private OutputStream outputStream;

    private volatile boolean firstCollectionRun;

    private long samplingIntervalMs;

    private ProcessFinder processFinder;

    //
    // the pid of the process to monitor - will be asynchronously updated, and it may transition from null to
    // non-null and back
    //
    private AtomicReference<Integer> pid;

    private Reading previousReading;

    // Constructors ----------------------------------------------------------------------------------------------------

    Collector(Configuration conf) throws UserErrorException {

        this.conf = conf;
        this.doRun = true;
        this.outputFile = conf.getOutputFile();
        this.samplingIntervalMs = conf.getSamplingIntervalMs();
        this.pid = new AtomicReference<>();

        try {

            if (outputFile == null) {

                outputStream = System.out;
            }
            else {

                outputStream = new FileOutputStream(outputFile);
            }
        }
        catch(IOException e) {

            throw new UserErrorException("failed to open " + outputFile + " for writing", e);
        }

        this.firstCollectionRun = true;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void run() throws UserErrorException {

        String processRegex = conf.getProcessRegex();
        Command command = conf.getCommand();

        if (command != null) {

            command.execute();
        }
        else {

            //
            // default behavior, collect data
            //

            if (processRegex != null) {

                //
                // start another thread to look for the process specified by the regular expression, and do it continuously
                // and asynchronously while we're collecting samples on the main thread
                //
                //

                startProcessFinder(processRegex);
            }

            //
            // do the sample collection on the main thread, unless a specific command is provided
            //

            while (doRun) {

                long t0 = System.currentTimeMillis();

                try {

                    sampleCollectionRun();
                }
                catch (TransientUserException e) {

                    System.err.println("[warn]: " + e.getMessage());
                }
                finally {

                    long t1 = System.currentTimeMillis();

                    long collectionDuration = t1 - t0;

                    //
                    // if the collection finished quicker than the sampling interval, sleep ...
                    //

                    long timeToSleep = samplingIntervalMs - collectionDuration;

                    if (timeToSleep > 0) {

                        try {

                            Thread.sleep(timeToSleep);
                        } catch (InterruptedException e) {

                            //
                            // no reason to be interrupted, but if we are, warn and collect faster than the
                            // sampling interval
                            //

                            System.err.println("[warn]: collection thread interrupted");
                        }
                    } else if (timeToSleep < 0) {

                        //
                        // otherwise warn we're configured too tight ...
                        //

                        System.err.println("[warn]: cannot complete collections in " + samplingIntervalMs + " ms, consider increasing the interval ...");
                    }
                }
            }
        }
    }

    public void stop() {

        if (processFinder != null) {

            processFinder.stop();
        }

        this.doRun = false;
    }

    public void flush() {

        //
        // noop for the time being, we're not buffered
        //
    }

    public void close() {

        if (outputFile != null) {

            try {

                outputStream.close();
            }
            catch (Exception e) {

                System.err.println("failed to close output stream for " + outputFile);
            }
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void sampleCollectionRun() throws UserErrorException, TransientUserException {

        long t0 = System.currentTimeMillis();

        ProcStat s = collectProcStat();

        Integer pidValue = pid.get();

        PerProcessStat s2 = collectPerProcessStat(pidValue);

        long t1 = System.currentTimeMillis();

        Reading r = new Reading(t0 + (t1 - t0)/2, s, s2, previousReading);

        write(r);

        previousReading = r;
    }

    private ProcStat collectProcStat() throws UserErrorException {

        File file = new File("/proc/stat");

        if (!file.isFile()) {

            throw new UserErrorException("file " + file + " not found");
        }

        try {

            byte[] content = Files.readAllBytes(file.toPath());

            return new ProcStat(content);

        }
        catch (ParsingException e) {

            throw new UserErrorException("failed to parse file " + file + ": " + e.getMessage());
        }
        catch (IOException e) {

            throw new UserErrorException("failed to read " + file, e);
        }
    }

    /**
     * @param pid the pid of the process to investigate. May be null, in which case we're a noop.
     * @return per-process statistics or null if the pid was null.
     */
    private PerProcessStat collectPerProcessStat(Integer pid) throws TransientUserException {

        if (pid == null) {

            //
            // no such process
            //

            return null;
        }

        File stat = new File("/proc", pid.toString() + "/stat");

        if (!stat.isFile()) {

            throw new TransientUserException("no " + stat + " file found");
        }

        try {

            byte[] content = Files.readAllBytes(stat.toPath());

            return new PerProcessStat(pid, content);

        }
        catch (Exception e) {

            throw new TransientUserException(e);
        }
    }

    private void write(Reading r) throws UserErrorException {

        try {

            if (firstCollectionRun) {

                firstCollectionRun = false;

                String csvHeader = r.toCsvHeader();

                outputStream.write((csvHeader + "\n").getBytes());
            }

            String csvLine = r.toCsv();

            outputStream.write((csvLine + "\n").getBytes());
        }
        catch(IOException e) {

            //
            // this will break the main loop as it does not make sense to keep collecting data if we cannot write it
            //

            throw new UserErrorException("failed to write data into " + outputFile, e);
        }
        finally {

            // severs the relationship with the previous reading
            r.clear();
        }
    }

    private void startProcessFinder(String processRegex) {

        this.processFinder = new ProcessFinder(processRegex, pid);

        new Thread(processFinder, "Process Finder Thread").start();
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
