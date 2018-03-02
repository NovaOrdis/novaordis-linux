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

package io.novaordis.linux.collector.command;

import java.util.concurrent.atomic.AtomicReference;

import io.novaordis.linux.collector.Configuration;
import io.novaordis.linux.collector.ProcessFinder;
import io.novaordis.utilities.UserErrorException;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 3/2/18
 */
public class Find implements Command {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String LITERAL = "find";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Configuration conf;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Find(Configuration conf) {

        this.conf = conf;
    }

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public void execute() throws UserErrorException {

        String processRegex = conf.getProcessRegex();

        if (processRegex == null) {

            throw new UserErrorException("no process regular expression specified, use --process-regex='...'");
        }

        AtomicReference<Integer> pid = new AtomicReference<>();

        ProcessFinder processFinder = new ProcessFinder(processRegex, pid);
        processFinder.stop(); // this will make it run once and exit
        processFinder.run();

        Integer i = pid.get();

        if (i != null) {

            System.out.println(i);
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
