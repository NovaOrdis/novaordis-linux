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

import io.novaordis.utilities.UserErrorException;

/**
 * Configure it with a process regular expression and keep it running. It will collect statistics and write them
 * into the output file. To stop collection, Ctrl-C.
 *
 * collect [--output-file=/tmp/cpu.csv] [--process-regex='<regex>'] [--sampling-interval-ms=10]
 *
 * If no output file is specified, the process will write to stdout.
 *
 * For more usage details, see src/main/resources/collector-help.txt
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 3/1/18
 */
public class Main {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    public static void main(String[] args) throws Exception {

        try {

            Configuration conf = new Configuration(args);

            Collector c = new Collector(conf);

            Runtime.getRuntime().addShutdownHook(new ShutdownHook(c));

            c.run();
        }
        catch (UserErrorException e) {

            System.out.println("[error]: " + e.getMessage());
        }

    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
