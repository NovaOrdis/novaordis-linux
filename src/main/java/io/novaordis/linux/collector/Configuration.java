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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.novaordis.linux.collector.command.Command;
import io.novaordis.linux.collector.command.Find;
import io.novaordis.utilities.UserErrorException;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 3/1/18
 */
public class Configuration {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final int DEFAULT_SAMPLING_INTERVAL_MS = 10;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private File outputFile;
    private String processRegex;
    private int samplingIntervalMs;
    private Command command;

    // Constructors ----------------------------------------------------------------------------------------------------

    Configuration(String[] args) throws UserErrorException {

        samplingIntervalMs = DEFAULT_SAMPLING_INTERVAL_MS;

        //noinspection ForLoopReplaceableByForEach
        for(int i = 0; i < args.length; i ++) {

            String crt = args[i];

            if (crt.startsWith("--sampling-interval-ms=")) {

                crt = crt.substring("--sampling-interval-ms=".length());

                try {

                    samplingIntervalMs = Integer.parseInt(crt);
                }
                catch (Exception e) {

                    throw new UserErrorException("invalid sampling interval value " + crt);
                }
            }
            else if (crt.startsWith("--process-regex=")) {

                processRegex = crt.substring("--process-regex=".length());
            }
            else if (crt.startsWith("--output-file=")) {

                outputFile = new File(crt.substring("--output-file=".length()));
            }
            else if (crt.equalsIgnoreCase("--help") || crt.equalsIgnoreCase("help")) {

                displayHelp();

                System.exit(0);
            }
            else if (Find.LITERAL.equals(crt)) {

                this.command = new Find(this);
            }
            else {

                throw new UserErrorException("unknown argument: " + crt);
            }
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * May return null, which means we won't collect statistics for any specific process.
     */
    public String getProcessRegex() {

        return processRegex;
    }

    /**
     * May return null, which means to write to stdout.
     */
    public File getOutputFile() {

        return outputFile;
    }

    public int getSamplingIntervalMs() {

        return samplingIntervalMs;
    }

    /**
     * May return null, which means default behavior - collect statistics.
     */
    public Command getCommand() {

        return command;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void displayHelp() {

        InputStream is = Configuration.class.getClassLoader().getResourceAsStream("collector-help.txt");

        if (is == null) {

            System.err.println("no in-line help file found in classpath");
        }
        else {

            BufferedReader br = null;

            try {

                br = new BufferedReader(new InputStreamReader(is));

                String line;

                while ((line = br.readLine()) != null) {

                    System.out.println(line);
                }
            }
            catch(Exception e) {

                System.err.println("I/O error while reading help file");
            }
            finally {

                if (br != null) {

                    try {

                        br.close();
                    }
                    catch(Exception e) {

                        //
                        // ok to ignore
                        //
                    }
                }
            }
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
