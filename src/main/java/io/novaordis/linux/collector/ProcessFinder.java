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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.novaordis.utilities.os.NativeExecutionResult;
import io.novaordis.utilities.os.OS;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 3/1/18
 */
public class ProcessFinder implements Runnable {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final int DEFAULT_POLL_INTERVAL_MS = 500;

    public static final String PS_COMMAND = "ps -ef";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String processRegex;

    private volatile boolean findProcess;

    private Pattern pattern;

    private long pollIntervalMs;

    private AtomicReference<Integer> pid;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param pid the atomic reference to update with the "found" pid or with null if the process goes away.
     */
    public ProcessFinder(String processRegex, AtomicReference<Integer> pid) {

        if (processRegex == null) {

            throw new IllegalArgumentException("null process regex");
        }

        if (pid == null) {

            throw new IllegalArgumentException("null pid atomic reference");
        }

        this.pid = pid;

        this.findProcess = true;

        this.processRegex = processRegex;

        this.pattern = Pattern.compile(processRegex);

        this.pollIntervalMs = DEFAULT_POLL_INTERVAL_MS;
    }

    // Runnable implementation -----------------------------------------------------------------------------------------

    public void run() {

        while(findProcess) {

            try {

                long t0 = System.currentTimeMillis();

                NativeExecutionResult r = OS.getInstance().execute(PS_COMMAND);

                if (r.isSuccess()) {

                    List<Integer> pids = findProcesses(r.getStdout());

                    if (pids.isEmpty()) {

                        //
                        // the process possibly went away, update the atomic reference pid
                        //

                        pid.set(null);

                    }
                    else {

                        int processCount = pids.size();

                        if (processCount > 1) {

                            System.err.println("[warn]: more than one process matches regex '" + processRegex + "': " + pids);
                        }
                        else {

                            //
                            // one pid, update the atomic reference
                            //

                            int p = pids.get(0);
                            pid.set(p);
                        }
                    }
                }
                else {

                    System.err.println(
                            "[warn]: '" + PS_COMMAND + "' command failed:\n" + r.getStderr() + "\n" + r.getStdout());
                }

                long sleepTime = pollIntervalMs - System.currentTimeMillis() + t0;

                if (sleepTime > 0) {

                    Thread.sleep(sleepTime);
                }
            }
            catch(Exception e) {

                System.err.println("[error]: "  + e.getMessage());
                e.printStackTrace(System.err);
            }
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void stop() {

        this.findProcess = false;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    /**
     * @return a list of PIDs corresponding to processes matching the regular expression. May return an empty list if
     * no process matches.
     */
    private List<Integer> findProcesses(String psOutput) {

        List<Integer> pids = new ArrayList<>();

        for(StringTokenizer st = new StringTokenizer(psOutput, "\n"); st.hasMoreTokens(); ) {

            String line = st.nextToken();

            if (line.contains(" --process-regex=" + processRegex)) {

                //
                // ignore our own process
                //

                continue;
            }

            Matcher m = pattern.matcher(line);

            if (m.find()) {

                StringTokenizer st2 = new StringTokenizer(line, " ");
                st2.nextToken();
                String spid = st2.nextToken();
                int pid = Integer.parseInt(spid);
                pids.add(pid);
            }
        }

        return pids;
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
