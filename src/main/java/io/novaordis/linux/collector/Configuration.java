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

import io.novaordis.utilities.UserErrorException;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 3/1/18
 */
public class Configuration {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final int DEFAULT_SAMPLING_INTERVAL_MS = 10;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private File outputFile;
    private String processRegex;
    private int samplingIntervalMs;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Configuration(String[] args) throws UserErrorException {

        samplingIntervalMs = DEFAULT_SAMPLING_INTERVAL_MS;

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

                System.out.println("process regex: " + processRegex);
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

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
