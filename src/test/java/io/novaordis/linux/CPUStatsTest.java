/*
 * Copyright (c) 2017 Nova Ordis LLC
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

package io.novaordis.linux;

import io.novaordis.utilities.parsing.ParsingException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/9/17
 */
public class CPUStatsTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // constructors ----------------------------------------------------------------------------------------------------

    @Test
    public void constructor_NullLine() throws Exception {

        try {

            new CPUStats(1L, null);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("null line"));
        }
    }

    @Test
    public void constructor_NotACPUStatisticsLine() throws Exception {

        try {

            new CPUStats(7L, "something that has nothing to do with CPU");
            fail("should have thrown exception");
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("not a cpu statistics line"));

            assertEquals(7L, e.getLineNumber().longValue());
        }
    }

    @Test
    public void constructor_InvalidCPUID() throws Exception {

        try {

            new CPUStats(7L, "cpublah blah blah");
            fail("should have thrown exception");
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid cpu ID"));
            assertTrue(msg.contains("blah"));

            assertEquals(7L, e.getLineNumber().longValue());
        }
    }

    // Processor ID and isCumulative() ---------------------------------------------------------------------------------

    @Test
    public void getCPUID_Cumulative() throws Exception {

        String line = "cpu  51867872 12504 17293071 1007053375 43884956 0 734997 28476 0 0";

        CPUStats s = new CPUStats(1L, line);

        assertTrue(s.isCumulative());

        assertNull(s.getCPUID());
    }

    @Test
    public void getCPUID_NonCumulative() throws Exception {

        String line = "cpu0 26424972 5655 8522768 507855710 17916771 0 160989 12217 0 0";

        CPUStats s = new CPUStats(1L, line);

        assertFalse(s.isCumulative());

        assertEquals(0, s.getCPUID().intValue());
    }

    // counters --------------------------------------------------------------------------------------------------------

    @Test
    public void counters() throws Exception {

        String line = "cpu 1 2 3 4 5 6 7 8 9 10";

        CPUStats s = new CPUStats(1L, line);

        assertEquals(1L, s.getUserTime());
        assertEquals(2L, s.getNiceTime());
        assertEquals(3L, s.getSystemTime());
        assertEquals(4L, s.getIdleTime());
        assertEquals(5L, s.getIowaitTime());
        assertEquals(6L, s.getIrqTime());
        assertEquals(7L, s.getSoftirqTime());
        assertEquals(8L, s.getStealTime());
        assertEquals(9L, s.getGuestTime());
        assertEquals(10L, s.getGuestNiceTime());
    }

    @Test
    public void counters2() throws Exception {

        String line = "cpu0 1 2 3 4 5 6 7 8 9 10";

        CPUStats s = new CPUStats(1L, line);

        assertEquals(1L, s.getUserTime());
        assertEquals(2L, s.getNiceTime());
        assertEquals(3L, s.getSystemTime());
        assertEquals(4L, s.getIdleTime());
        assertEquals(5L, s.getIowaitTime());
        assertEquals(6L, s.getIrqTime());
        assertEquals(7L, s.getSoftirqTime());
        assertEquals(8L, s.getStealTime());
        assertEquals(9L, s.getGuestTime());
        assertEquals(10L, s.getGuestNiceTime());
    }

    @Test
    public void invalidUserTime() throws Exception {

        String line = "cpu blah 5655 8522768 507855710 17916771 0 160989 12217 0 0";

        try {

            new CPUStats(7L, line);
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid user time value"));
            assertTrue(msg.contains("blah"));

            assertEquals(7L, e.getLineNumber().longValue());
        }
    }

    @Test
    public void invalidNiceTime() throws Exception {

        String line = "cpu 1 blah 8522768 507855710 17916771 0 160989 12217 0 0";

        try {

            new CPUStats(7L, line);
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid nice time value"));
            assertTrue(msg.contains("blah"));

            assertEquals(7L, e.getLineNumber().longValue());
        }
    }

    @Test
    public void invalidSystemTime() throws Exception {

        String line = "cpu 1 2 blah 507855710 17916771 0 160989 12217 0 0";

        try {

            new CPUStats(7L, line);
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid system time value"));
            assertTrue(msg.contains("blah"));

            assertEquals(7L, e.getLineNumber().longValue());
        }
    }

    @Test
    public void invalidIdleTime() throws Exception {

        String line = "cpu 1 2 3 blah 17916771 0 160989 12217 0 0";

        try {

            new CPUStats(7L, line);
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid idle time value"));
            assertTrue(msg.contains("blah"));

            assertEquals(7L, e.getLineNumber().longValue());
        }
    }

    @Test
    public void invalidIowaitTime() throws Exception {

        String line = "cpu 1 2 3 4 blah 0 160989 12217 0 0";

        try {

            new CPUStats(7L, line);
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid iowait time value"));
            assertTrue(msg.contains("blah"));

            assertEquals(7L, e.getLineNumber().longValue());
        }
    }

    @Test
    public void invalidIrqTime() throws Exception {

        String line = "cpu 1 2 3 4 5 blah 160989 12217 0 0";

        try {

            new CPUStats(7L, line);
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid irq time value"));
            assertTrue(msg.contains("blah"));

            assertEquals(7L, e.getLineNumber().longValue());
        }
    }

    @Test
    public void invalidSoftirqTime() throws Exception {

        String line = "cpu 1 2 3 4 5 6 blah 12217 0 0";

        try {

            new CPUStats(7L, line);
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid softirq time value"));
            assertTrue(msg.contains("blah"));

            assertEquals(7L, e.getLineNumber().longValue());
        }
    }

    @Test
    public void invalidStealTime() throws Exception {

        String line = "cpu 1 2 3 4 5 6 7 blah 0 0";

        try {

            new CPUStats(7L, line);
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid steal time value"));
            assertTrue(msg.contains("blah"));

            assertEquals(7L, e.getLineNumber().longValue());
        }
    }

    @Test
    public void invalidGuestTime() throws Exception {

        String line = "cpu 1 2 3 4 5 6 7 8 blah 0";

        try {

            new CPUStats(7L, line);
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid guest time value"));
            assertTrue(msg.contains("blah"));

            assertEquals(7L, e.getLineNumber().longValue());
        }
    }

    @Test
    public void invalidGuestNiceTime() throws Exception {

        String line = "cpu 1 2 3 4 5 6 7 8 9 blah";

        try {

            new CPUStats(7L, line);
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid guest_nice time value"));
            assertTrue(msg.contains("blah"));

            assertEquals(7L, e.getLineNumber().longValue());
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
