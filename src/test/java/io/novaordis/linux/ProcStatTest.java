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

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/9/17
 */
public class ProcStatTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void reference() throws Exception {

        File referenceFile = new File(System.getProperty("basedir"), "src/test/resources/data/proc/stat-reference");
        assertTrue(referenceFile.isFile());

        byte[] content = Files.readAllBytes(referenceFile.toPath());

        ProcStat ps = new ProcStat(content);

        CPUStats ccpu = ps.getCumulativeCPUStatistics();

        assertNull(ccpu.getCPUID());
        assertTrue(ccpu.isCumulative());
        assertEquals(51867872L, ccpu.getUserTime());
        assertEquals(12504L, ccpu.getNiceTime());
        assertEquals(17293071L, ccpu.getSystemTime());
        assertEquals(1007053375L, ccpu.getIdleTime());
        assertEquals(43884956L, ccpu.getIowaitTime());
        assertEquals(0L, ccpu.getIrqTime());
        assertEquals(734997L, ccpu.getSoftirqTime());
        assertEquals(28476L, ccpu.getStealTime());
        assertEquals(0L, ccpu.getGuestTime());
        assertEquals(0L, ccpu.getGuestNiceTime());

        assertEquals(2, ps.getCPUCount());

        CPUStats c0cpu = ps.getCPUStatistics(0);

        assertEquals(0, c0cpu.getCPUID().intValue());
        assertFalse(c0cpu.isCumulative());
        assertEquals(26424972L, c0cpu.getUserTime());
        assertEquals(5655L, c0cpu.getNiceTime());
        assertEquals(8522768L, c0cpu.getSystemTime());
        assertEquals(507855710L, c0cpu.getIdleTime());
        assertEquals(17916771L, c0cpu.getIowaitTime());
        assertEquals(0L, c0cpu.getIrqTime());
        assertEquals(160989L, c0cpu.getSoftirqTime());
        assertEquals(12217L, c0cpu.getStealTime());
        assertEquals(0L, c0cpu.getGuestTime());
        assertEquals(0L, c0cpu.getGuestNiceTime());

        CPUStats c1cpu = ps.getCPUStatistics(1);

        assertEquals(1, c1cpu.getCPUID().intValue());
        assertFalse(c1cpu.isCumulative());
        assertEquals(25442899L, c1cpu.getUserTime());
        assertEquals(6848L, c1cpu.getNiceTime());
        assertEquals(8770302L, c1cpu.getSystemTime());
        assertEquals(499197664L, c1cpu.getIdleTime());
        assertEquals(25968185L, c1cpu.getIowaitTime());
        assertEquals(0L, c1cpu.getIrqTime());
        assertEquals(574008L, c1cpu.getSoftirqTime());
        assertEquals(16259L, c1cpu.getStealTime());
        assertEquals(0L, c1cpu.getGuestTime());
        assertEquals(0L, c1cpu.getGuestNiceTime());

        try {

            ps.getCPUStatistics(2);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("no such cpu"));
            assertTrue(msg.contains("2"));
        }
    }

    // constructors ----------------------------------------------------------------------------------------------------

    @Test
    public void constructor_NullContent() throws Exception {

        try {

            new ProcStat(null);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("null content"));
        }
    }

    @Test
    public void constructor_MustHaveAtLeastOneCPULine() throws Exception {

        try {

            new ProcStat("something that does not make any sense".getBytes());
            fail("should have thrown exception");
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("missing cpu data"));
        }
    }

    @Test
    public void constructor_MustHaveAtLeastOneCPULine2() throws Exception {

        try {

            new ProcStat("something that does not make any sense\non two lines".getBytes());
            fail("should have thrown exception");
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("missing cpu data"));
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
