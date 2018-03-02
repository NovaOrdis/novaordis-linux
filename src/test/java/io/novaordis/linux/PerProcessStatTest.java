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

package io.novaordis.linux;

import java.io.File;
import java.nio.file.Files;

import org.junit.Test;

import io.novaordis.utilities.parsing.ParsingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/9/17
 */
public class PerProcessStatTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Tests -----------------------------------------------------------------------------------------------------------

    @Test
    public void reference() throws Exception {

        File referenceFile = new File(System.getProperty("basedir"), "src/test/resources/data/proc/pid972-stat-reference");
        assertTrue(referenceFile.isFile());

        byte[] content = Files.readAllBytes(referenceFile.toPath());

        PerProcessStat ps = new PerProcessStat(972, content);

        assertEquals(972, ps.getPid());

        assertEquals("docker-containe", ps.getExecutableFileName());

        assertEquals(808L, ps.getUtime());
        assertEquals(296L, ps.getStime());
        assertEquals(16L, ps.getCutime());
        assertEquals(17L, ps.getCstime());
        assertEquals(1820L, ps.getStarttime());
        assertEquals(43L, ps.getGuesttime());
    }

    // constructors ----------------------------------------------------------------------------------------------------

    @Test
    public void constructor_NullContent() throws Exception {

        try {

            new PerProcessStat(1, null);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("null content"));
        }
    }

    @Test
    public void constructor_PidArgumentDiffersFromContentPid() throws Exception {

        String content = "777 (docker-containe) S 901 972 972 0 -1 1077944576 2723 0 2 0 808 296 0 0 20 0 11 0" +
                " 1820 441688064 2267 18446744073709551615 4194304 11049596 140727040242048 140727040241432 4602915" +
                " 0 2079995941 0 2143420159 18446744073709551615 0 0 17 1 0 0 0 0 0 13147640 13322176 25554944" +
                " 140727040249523 140727040249749 140727040249749 140727040249821 0";

        try {

            new PerProcessStat(888, content.getBytes());
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("pid passed as argument"));
            assertTrue(msg.contains("777"));
            assertTrue(msg.contains("does not match pid extracted from content"));
            assertTrue(msg.contains("888"));
        }
    }

    @Test
    public void constructor_NotEnoughTokens() throws Exception {

        String content = "777 (docker-containe)";

        try {

            new PerProcessStat(777, content.getBytes());
            fail("should have thrown exception");
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("field 3 missing"));
        }
    }

    @Test
    public void constructor_InvalidPid() throws Exception {

        String content = "blah (docker-containe) S 901 972 972 0 -1 1077944576 2723 0 2 0 808 296 0 0 20 0 11 0" +
                " 1820 441688064 2267 18446744073709551615 4194304 11049596 140727040242048 140727040241432 4602915" +
                " 0 2079995941 0 2143420159 18446744073709551615 0 0 17 1 0 0 0 0 0 13147640 13322176 25554944" +
                " 140727040249523 140727040249749 140727040249749 140727040249821 0";

        try {

            new PerProcessStat(777, content.getBytes());
            fail("should have thrown exception");
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid field 1 (pid) value"));
            assertTrue(msg.contains("blah"));
        }
    }

    @Test
    public void constructor_InvalidUtime() throws Exception {

        String content = "777 (docker-containe) S 901 972 972 0 -1 1077944576 2723 0 2 0 blah 296 0 0 20 0 11 0" +
                " 1820 441688064 2267 18446744073709551615 4194304 11049596 140727040242048 140727040241432 4602915" +
                " 0 2079995941 0 2143420159 18446744073709551615 0 0 17 1 0 0 0 0 0 13147640 13322176 25554944" +
                " 140727040249523 140727040249749 140727040249749 140727040249821 0";

        try {

            new PerProcessStat(777, content.getBytes());
            fail("should have thrown exception");
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid field 14 (utime) value"));
            assertTrue(msg.contains("blah"));
        }
    }

    @Test
    public void constructor_InvalidStime() throws Exception {

        String content = "777 (docker-containe) S 901 972 972 0 -1 1077944576 2723 0 2 0 777 blah 0 0 20 0 11 0" +
                " 1820 441688064 2267 18446744073709551615 4194304 11049596 140727040242048 140727040241432 4602915" +
                " 0 2079995941 0 2143420159 18446744073709551615 0 0 17 1 0 0 0 0 0 13147640 13322176 25554944" +
                " 140727040249523 140727040249749 140727040249749 140727040249821 0";

        try {

            new PerProcessStat(777, content.getBytes());
            fail("should have thrown exception");
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid field 15 (stime) value"));
            assertTrue(msg.contains("blah"));
        }
    }

    @Test
    public void constructor_InvalidCutime() throws Exception {

        String content = "777 (docker-containe) S 901 972 972 0 -1 1077944576 2723 0 2 0 111 296 blah 0 20 0 11 0" +
                " 1820 441688064 2267 18446744073709551615 4194304 11049596 140727040242048 140727040241432 4602915" +
                " 0 2079995941 0 2143420159 18446744073709551615 0 0 17 1 0 0 0 0 0 13147640 13322176 25554944" +
                " 140727040249523 140727040249749 140727040249749 140727040249821 0";

        try {

            new PerProcessStat(777, content.getBytes());
            fail("should have thrown exception");
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid field 16 (cutime) value"));
            assertTrue(msg.contains("blah"));
        }
    }

    @Test
    public void constructor_InvalidCstime() throws Exception {

        String content = "777 (docker-containe) S 901 972 972 0 -1 1077944576 2723 0 2 0 777 111 0 blah 20 0 11 0" +
                " 1820 441688064 2267 18446744073709551615 4194304 11049596 140727040242048 140727040241432 4602915" +
                " 0 2079995941 0 2143420159 18446744073709551615 0 0 17 1 0 0 0 0 0 13147640 13322176 25554944" +
                " 140727040249523 140727040249749 140727040249749 140727040249821 0";

        try {

            new PerProcessStat(777, content.getBytes());
            fail("should have thrown exception");
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid field 17 (cstime) value"));
            assertTrue(msg.contains("blah"));
        }
    }

    @Test
    public void constructor_InvalidStarttime() throws Exception {

        String content = "777 (docker-containe) S 901 972 972 0 -1 1077944576 2723 0 2 0 777 111 0 1 20 0 11 0" +
                " blah 441688064 2267 18446744073709551615 4194304 11049596 140727040242048 140727040241432 4602915" +
                " 0 2079995941 0 2143420159 18446744073709551615 0 0 17 1 0 0 0 0 0 13147640 13322176 25554944" +
                " 140727040249523 140727040249749 140727040249749 140727040249821 0";

        try {

            new PerProcessStat(777, content.getBytes());
            fail("should have thrown exception");
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid field 22 (starttime) value"));
            assertTrue(msg.contains("blah"));
        }
    }

    @Test
    public void constructor_InvalidGuesttime() throws Exception {

        String content = "777 (docker-containe) S 901 972 972 0 -1 1077944576 2723 0 2 0 777 111 0 1 20 0 11 0" +
                " 1 441688064 2267 18446744073709551615 4194304 11049596 140727040242048 140727040241432 4602915" +
                " 0 2079995941 0 2143420159 18446744073709551615 0 0 17 1 0 0 0 blah 0 13147640 13322176 25554944" +
                " 140727040249523 140727040249749 140727040249749 140727040249821 0";

        try {

            new PerProcessStat(777, content.getBytes());
            fail("should have thrown exception");
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            assertTrue(msg.contains("invalid field 43 (guest_time) value"));
            assertTrue(msg.contains("blah"));
        }
    }


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
