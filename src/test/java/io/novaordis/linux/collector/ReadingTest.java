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

import org.junit.Test;

import io.novaordis.linux.PerProcessStat;
import io.novaordis.linux.ProcStat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 3/2/18
 */
public class ReadingTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    // computeCpuUtilization() -----------------------------------------------------------------------------------------

    @Test
    public void computeCpuUtilization_NoPreviousReading() throws Exception {

        Reading r = new Reading(1L, new ProcStat("cpu 1000 0 0 0 0 0 0 0 0 0".getBytes()), null, null);

        assertNull(r.computeCpuUtilization());
    }

    @Test
    public void computeCpuUtilization() throws Exception {

        // 'cpu' user nice system idle iowait irq softirq steal guest guest_nice
        ProcStat ps = new ProcStat("cpu 1000 0 0 0 0 0 0 0 0 0".getBytes());

        PerProcessStat pps = new PerProcessStat(1,
                "1 0 0 0 0 0 0 0 0 0 0 0 0 1000 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0".getBytes());

        Reading r = new Reading(1L, ps, pps, null);

        ProcStat ps2 = new ProcStat("cpu 2000 0 0 0 0 0 0 0 0 0".getBytes());

        PerProcessStat pps2 = new PerProcessStat(1,
                "1 0 0 0 0 0 0 0 0 0 0 0 0 2000 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0".getBytes());

        Reading r2 = new Reading(2L, ps2, pps2, r);

        Double d = r2.computeCpuUtilization();

        assertEquals(1.0d, d, 0.0001);
    }

    @Test
    public void computeCpuUtilization2() throws Exception {

        // 'cpu' user nice system idle iowait irq softirq steal guest guest_nice
        ProcStat ps = new ProcStat("cpu 1000 0 0 0 0 0 0 0 0 0".getBytes());

        PerProcessStat pps = new PerProcessStat(1,
                "1 0 0 0 0 0 0 0 0 0 0 0 0 1000 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0".getBytes());

        Reading r = new Reading(1L, ps, pps, null);

        ProcStat ps2 = new ProcStat("cpu 2000 0 0 0 0 0 0 0 0 0".getBytes());

        PerProcessStat pps2 = new PerProcessStat(1,
                "1 0 0 0 0 0 0 0 0 0 0 0 0 1000 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0".getBytes());

        Reading r2 = new Reading(2L, ps2, pps2, r);

        Double d = r2.computeCpuUtilization();

        assertEquals(0.0d, d, 0.0001);
    }

    @Test
    public void computeCpuUtilization3() throws Exception {

        // 'cpu' user nice system idle iowait irq softirq steal guest guest_nice
        ProcStat ps = new ProcStat("cpu 1000 0 0 0 0 0 0 0 0 0".getBytes());

        PerProcessStat pps = new PerProcessStat(1,
                "1 0 0 0 0 0 0 0 0 0 0 0 0 1000 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0".getBytes());

        Reading r = new Reading(1L, ps, pps, null);

        ProcStat ps2 = new ProcStat("cpu 2000 0 0 0 0 0 0 0 0 0".getBytes());

        PerProcessStat pps2 = new PerProcessStat(1,
                "1 0 0 0 0 0 0 0 0 0 0 0 0 1500 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0".getBytes());

        Reading r2 = new Reading(2L, ps2, pps2, r);

        Double d = r2.computeCpuUtilization();

        assertEquals(0.5d, d, 0.0001);
    }

    @Test
    public void computeCpuUtilization_CumulativeTimeMismatch() throws Exception {

        // 'cpu' user nice system idle iowait irq softirq steal guest guest_nice
        ProcStat ps = new ProcStat("cpu 1000 0 0 0 0 0 0 0 0 0".getBytes());

        PerProcessStat pps = new PerProcessStat(1,
                "1 0 0 0 0 0 0 0 0 0 0 0 0 1000 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0".getBytes());

        Reading r = new Reading(1L, ps, pps, null);

        ProcStat ps2 = new ProcStat("cpu 999 0 0 0 0 0 0 0 0 0".getBytes());

        PerProcessStat pps2 = new PerProcessStat(1,
                "1 0 0 0 0 0 0 0 0 0 0 0 0 2000 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0".getBytes());

        Reading r2 = new Reading(2L, ps2, pps2, r);

        try {

            r2.computeCpuUtilization();
            fail("should have thrown exception");
        }
        catch(IllegalStateException e) {

            String msg = e.getMessage();
            assertEquals("current cumulative CPU time less than previous cumulative CPU time", msg);
        }
    }

    @Test
    public void computeCpuUtilization_NothingHappenedOnProcessorBetweenReadings() throws Exception {

        // 'cpu' user nice system idle iowait irq softirq steal guest guest_nice
        ProcStat ps = new ProcStat("cpu 1000 0 0 0 0 0 0 0 0 0".getBytes());

        PerProcessStat pps = new PerProcessStat(1,
                "1 0 0 0 0 0 0 0 0 0 0 0 0 1000 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0".getBytes());

        Reading r = new Reading(1L, ps, pps, null);

        ProcStat ps2 = new ProcStat("cpu 1000 0 0 0 0 0 0 0 0 0".getBytes());

        PerProcessStat pps2 = new PerProcessStat(1,
                "1 0 0 0 0 0 0 0 0 0 0 0 0 2000 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0".getBytes());

        Reading r2 = new Reading(2L, ps2, pps2, r);

        assertNull(r2.computeCpuUtilization());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
