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

            new CPUStats(1L, null, null);
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

            new CPUStats(7L, null, "something that has nothing to do with CPU");
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

            new CPUStats(7L, null, "cpublah blah blah");
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

        CPUStats s = new CPUStats(1L, null, line);

        assertTrue(s.isCumulative());

        assertNull(s.getCPUID());
    }

    @Test
    public void getCPUID_NonCumulative() throws Exception {

        String line = "cpu0 26424972 5655 8522768 507855710 17916771 0 160989 12217 0 0";

        CPUStats s = new CPUStats(1L, null, line);

        assertFalse(s.isCumulative());

        assertEquals(0, s.getCPUID().intValue());
    }

    // counters --------------------------------------------------------------------------------------------------------

    @Test
    public void counters() throws Exception {

        String line = "cpu 1 2 3 4 5 6 7 8 9 10";

        CPUStats s = new CPUStats(1L, null, line);

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
        assertEquals(1L + 2L + 3L + 4L + 5L + 6L + 7L + 8L + 9L + 10L, s.getTotalTime());
    }

    @Test
    public void counters2() throws Exception {

        String line = "cpu0 1 2 3 4 5 6 7 8 9 10";

        CPUStats s = new CPUStats(1L, null, line);

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
        assertEquals(1L + 2L + 3L + 4L + 5L + 6L + 7L + 8L + 9L + 10L, s.getTotalTime());
    }

    @Test
    public void invalidUserTime() throws Exception {

        String line = "cpu blah 5655 8522768 507855710 17916771 0 160989 12217 0 0";

        try {

            new CPUStats(7L, null, line);
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

            new CPUStats(7L, null, line);
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

            new CPUStats(7L, null, line);
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

            new CPUStats(7L, null, line);
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

            new CPUStats(7L, null, line);
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

            new CPUStats(7L, null, line);
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

            new CPUStats(7L, null, line);
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

            new CPUStats(7L, null, line);
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

            new CPUStats(7L, null, line);
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

            new CPUStats(7L, null, line);
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid guest_nice time value"));
            assertTrue(msg.contains("blah"));

            assertEquals(7L, e.getLineNumber().longValue());
        }
    }

    // percentage ------------------------------------------------------------------------------------------------------

    @Test
    public void getUserTimePercentage_NoPreviousReading() throws Exception {

        CPUStats s = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        float f = s.getUserTimePercentage(null);

        assertEquals(1f/55f, f, 0.00001);
    }

    @Test
    public void getUserTimePercentage() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 11 22 33 44 55 66 77 88 99 110");

        float f = s.getUserTimePercentage(pr);

        assertEquals(10f/550, f, 0.00001);
    }

    @Test
    public void getUserTimePercentage_NoChangeInTotalTime() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        try {

            s.getUserTimePercentage(pr);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("cpu statistics did not change since the last reading"));
        }
    }

    @Test
    public void getNiceTimePercentage_NoPreviousReading() throws Exception {

        CPUStats s = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        float f = s.getNiceTimePercentage(null);

        assertEquals(2f / 55f, f, 0.00001);
    }

    @Test
    public void getNiceTimePercentage() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 11 22 33 44 55 66 77 88 99 110");

        float f = s.getNiceTimePercentage(pr);

        assertEquals(20f / 550, f, 0.00001);
    }

    @Test
    public void getNiceTimePercentage_NoChangeInTotalTime() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        try {

            s.getNiceTimePercentage(pr);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("cpu statistics did not change since the last reading"));
        }
    }

    @Test
    public void getSystemTimePercentage_NoPreviousReading() throws Exception {

        CPUStats s = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        float f = s.getSystemTimePercentage(null);

        assertEquals(3f / 55f, f, 0.00001);
    }

    @Test
    public void getSystemTimePercentage() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 11 22 33 44 55 66 77 88 99 110");

        float f = s.getSystemTimePercentage(pr);

        assertEquals(30f / 550, f, 0.00001);
    }

    @Test
    public void getSystemTimePercentage_NoChangeInTotalTime() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        try {

            s.getSystemTimePercentage(pr);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("cpu statistics did not change since the last reading"));
        }
    }

    @Test
    public void getIdleTimePercentage_NoPreviousReading() throws Exception {

        CPUStats s = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        float f = s.getIdleTimePercentage(null);

        assertEquals(4f / 55f, f, 0.00001);
    }

    @Test
    public void getIdleTimePercentage() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 11 22 33 44 55 66 77 88 99 110");

        float f = s.getIdleTimePercentage(pr);

        assertEquals(40f / 550, f, 0.00001);
    }

    @Test
    public void getIdleTimePercentage_NoChangeInTotalTime() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        try {

            s.getIdleTimePercentage(pr);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("cpu statistics did not change since the last reading"));
        }
    }

    @Test
    public void getIowaitTimePercentage_NoPreviousReading() throws Exception {

        CPUStats s = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        float f = s.getIowaitTimePercentage(null);

        assertEquals(5f / 55f, f, 0.00001);
    }

    @Test
    public void getIowaitTimePercentage() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 11 22 33 44 55 66 77 88 99 110");

        float f = s.getIowaitTimePercentage(pr);

        assertEquals(50f / 550, f, 0.00001);
    }

    @Test
    public void getIowaitTimePercentage_NoChangeInTotalTime() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        try {

            s.getIowaitTimePercentage(pr);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("cpu statistics did not change since the last reading"));
        }
    }

    @Test
    public void getIrqTimePercentage_NoPreviousReading() throws Exception {

        CPUStats s = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        float f = s.getIrqTimePercentage(null);

        assertEquals(6f / 55f, f, 0.00001);
    }

    @Test
    public void getIrqTimePercentage() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 11 22 33 44 55 66 77 88 99 110");

        float f = s.getIrqTimePercentage(pr);

        assertEquals(60f / 550, f, 0.00001);
    }

    @Test
    public void getIrqTimePercentage_NoChangeInTotalTime() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        try {

            s.getIrqTimePercentage(pr);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("cpu statistics did not change since the last reading"));
        }
    }

    @Test
    public void getSoftirqTimePercentage_NoPreviousReading() throws Exception {

        CPUStats s = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        float f = s.getSoftirqTimePercentage(null);

        assertEquals(7f / 55f, f, 0.00001);
    }

    @Test
    public void getSoftirqTimePercentage() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 11 22 33 44 55 66 77 88 99 110");

        float f = s.getSoftirqTimePercentage(pr);

        assertEquals(70f / 550, f, 0.00001);
    }

    @Test
    public void getSoftirqTimePercentage_NoChangeInTotalTime() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        try {

            s.getSoftirqTimePercentage(pr);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("cpu statistics did not change since the last reading"));
        }
    }

    @Test
    public void getStealTimePercentage_NoPreviousReading() throws Exception {

        CPUStats s = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        float f = s.getStealTimePercentage(null);

        assertEquals(8f / 55f, f, 0.00001);
    }

    @Test
    public void getStealTimePercentage() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 11 22 33 44 55 66 77 88 99 110");

        float f = s.getStealTimePercentage(pr);

        assertEquals(80f / 550, f, 0.00001);
    }

    @Test
    public void getStealTimePercentage_NoChangeInTotalTime() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        try {

            s.getStealTimePercentage(pr);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("cpu statistics did not change since the last reading"));
        }
    }

    @Test
    public void getGuestTimePercentage_NoPreviousReading() throws Exception {

        CPUStats s = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        float f = s.getGuestTimePercentage(null);

        assertEquals(9f / 55f, f, 0.00001);
    }

    @Test
    public void getGuestTimePercentage() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 11 22 33 44 55 66 77 88 99 110");

        float f = s.getGuestTimePercentage(pr);

        assertEquals(90f / 550, f, 0.00001);
    }

    @Test
    public void getGuestTimePercentage_NoChangeInTotalTime() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        try {

            s.getGuestTimePercentage(pr);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("cpu statistics did not change since the last reading"));
        }
    }

    @Test
    public void getGuestNiceTimePercentage_NoPreviousReading() throws Exception {

        CPUStats s = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        float f = s.getGuestNiceTimePercentage(null);

        assertEquals(10f / 55f, f, 0.00001);
    }

    @Test
    public void getGuestNiceTimePercentage() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 11 22 33 44 55 66 77 88 99 110");

        float f = s.getGuestNiceTimePercentage(pr);

        assertEquals(100f / 550, f, 0.00001);
    }

    @Test
    public void getGuestNiceTimePercentage_NoChangeInTotalTime() throws Exception {

        CPUStats pr = new CPUStats(1L, null, "cpu 1 2 3 4 5 6 7 8 9 10");
        CPUStats s = new CPUStats(2L, null, "cpu 1 2 3 4 5 6 7 8 9 10");

        try {

            s.getGuestNiceTimePercentage(pr);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("cpu statistics did not change since the last reading"));
        }
    }

    // production data -------------------------------------------------------------------------------------------------

    @Test
    public void productionData() throws Exception {

        String r = "cpu 53802252 12981 17867171 1032327410 44915037 0 764142 29459 0 0";
        String r2 = "cpu 53802411 12981 17867222 1032329314 44915145 0 764143 29459 0 0";

        CPUStats s = new CPUStats(null, null, r);
        CPUStats s2 = new CPUStats(null, null, r2);

        float userPercentageSinceBeginningOfTime = s2.getUserTimePercentage(null);
        float userPercentageLastInterval = s2.getUserTimePercentage(s);

        assertEquals(0.046796072f, userPercentageSinceBeginningOfTime, 0.000000001);
        assertEquals(0.07152496f, userPercentageLastInterval, 0.000000001);

        float nicePercentageSinceBeginningOfTime = s2.getNiceTimePercentage(null);
        float nicePercentageLastInterval = s2.getNiceTimePercentage(s);

        assertEquals(1.1290568E-5, nicePercentageSinceBeginningOfTime, 0.000000001);
        assertEquals(0.0f, nicePercentageLastInterval, 0.000000001);

        float systemPercentageSinceBeginningOfTime = s2.getSystemTimePercentage(null);
        float systemPercentageLastInterval = s2.getSystemTimePercentage(s);

        assertEquals(0.015540489f, systemPercentageSinceBeginningOfTime, 0.000000001);
        assertEquals(0.022941971f, systemPercentageLastInterval, 0.000000001);

        float idlePercentageSinceBeginningOfTime = s2.getIdleTimePercentage(null);
        float idlePercentageLastInterval = s2.getIdleTimePercentage(s);

        assertEquals(0.89789575f, idlePercentageSinceBeginningOfTime, 0.000000001);
        assertEquals(0.8565002f, idlePercentageLastInterval, 0.000000001);

        float iowaitPercentageSinceBeginningOfTime = s2.getIowaitTimePercentage(null);
        float iowaitPercentageLastInterval = s2.getIowaitTimePercentage(s);

        assertEquals(0.039066136f, iowaitPercentageSinceBeginningOfTime, 0.000000001);
        assertEquals(0.048582997f, iowaitPercentageLastInterval, 0.000000001);

        float irqPercentageSinceBeginningOfTime = s2.getIrqTimePercentage(null);
        float irqPercentageLastInterval = s2.getIrqTimePercentage(s);

        assertEquals(0f, irqPercentageSinceBeginningOfTime, 0.000000001);
        assertEquals(0f, irqPercentageLastInterval, 0.000000001);

        float softirqPercentageSinceBeginningOfTime = s2.getSoftirqTimePercentage(null);
        float softirqPercentageLastInterval = s2.getSoftirqTimePercentage(s);

        assertEquals(6.646336E-4, softirqPercentageSinceBeginningOfTime, 0.000000001);
        assertEquals(4.4984257E-4, softirqPercentageLastInterval, 0.000000001);

        float stealPercentageSinceBeginningOfTime = s2.getStealTimePercentage(null);
        float stealPercentageLastInterval = s2.getStealTimePercentage(s);

        assertEquals(2.5622745E-5, stealPercentageSinceBeginningOfTime, 0.000000001);
        assertEquals(0f, stealPercentageLastInterval, 0.000000001);

        float guestPercentageSinceBeginningOfTime = s2.getGuestTimePercentage(null);
        float guestPercentageLastInterval = s2.getGuestTimePercentage(s);

        assertEquals(0f, guestPercentageSinceBeginningOfTime, 0.000000001);
        assertEquals(0f, guestPercentageLastInterval, 0.000000001);

        float guestNicePercentageSinceBeginningOfTime = s2.getGuestNiceTimePercentage(null);
        float guestNicePercentageLastInterval = s2.getGuestNiceTimePercentage(s);

        assertEquals(0f, guestNicePercentageSinceBeginningOfTime, 0.000000001);
        assertEquals(0f, guestNicePercentageLastInterval, 0.000000001);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
