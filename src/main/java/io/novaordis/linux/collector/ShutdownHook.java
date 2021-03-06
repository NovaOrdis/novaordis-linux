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

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 3/1/18
 */
public class ShutdownHook extends Thread {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Collector collector;

    // Constructors ----------------------------------------------------------------------------------------------------

    // Runnable override -----------------------------------------------------------------------------------------------

    @Override
    public void run() {

        collector.stop();

        collector.flush();

        collector.close();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public ShutdownHook(Collector c) {

        this.collector = c;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
