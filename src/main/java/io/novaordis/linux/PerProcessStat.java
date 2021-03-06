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

import io.novaordis.utilities.parsing.ParsingException;
import io.novaordis.utilities.parsing.PreParsedContent;

/**
 * Represents an immutable /proc/<pid>/stat "reading" - the state of the file at a certain moment in time.
 *
 * https://kb.novaordis.com/index.php//proc/pid/stat#Contents
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/9/17
 */
public class PerProcessStat implements PreParsedContent {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final int FIELD_1_PID = 0;
    private static final int FIELD_2_EXECUTABLE_FILE_NAME = 1;
    private static final int FIELD_3_STATE = 2;
    private static final int FIELD_4_PPID = 3;
    private static final int FIELD_5_PGRP = 4;
    private static final int FIELD_6_SESSION = 5;
    private static final int FIELD_7_TTY_NR = 6;
    private static final int FIELD_8_TPGID = 7;
    private static final int FIELD_9_FLAGS = 8;
    private static final int FIELD_10_MINFLT = 9;
    private static final int FIELD_11_CMINFLT = 10;
    private static final int FIELD_12_MAJFLT = 11;
    private static final int FIELD_13_CMAJFLT = 12;
    private static final int FIELD_14_UTIME = 13;
    private static final int FIELD_15_STIME = 14;
    private static final int FIELD_16_CUTIME = 15;
    private static final int FIELD_17_CSTIME = 16;
    private static final int FIELD_18_PRIORITY = 17;
    private static final int FIELD_19_NICE = 18;
    private static final int FIELD_20_NUM_THREADS = 19;
    private static final int FIELD_21_ITREALVALUE = 20;
    private static final int FIELD_22_STARTTIME = 21;
    private static final int FIELD_23_VSIZE = 22;
    private static final int FIELD_24_RSS = 23;
    private static final int FIELD_25_RSSLIM = 24;
    private static final int FIELD_26_STARTCODE = 25;
    private static final int FIELD_27_ENDCODE = 26;
    private static final int FIELD_28_STARTSTACK = 27;
    private static final int FIELD_29_KSTKESP = 28;
    private static final int FIELD_30_KSTKEIP = 29;
    private static final int FIELD_31_SIGNAL = 30;
    private static final int FIELD_32_BLOCKED = 31;
    private static final int FIELD_33_SIGIGNORE = 32;
    private static final int FIELD_34_SIGCATCH = 33;
    private static final int FIELD_35_WCHAN = 34;
    private static final int FIELD_36_NSWAP = 35;
    private static final int FIELD_37_CNSWAP = 36;
    private static final int FIELD_38_EXIT_SIGNAL = 37;
    private static final int FIELD_39_PROCESSOR = 38;
    private static final int FIELD_40_RT_PRIORITY = 39;
    private static final int FIELD_41_POLICY = 40;
    private static final int FIELD_42_DELAYACCT_BLKIO_TICKS = 41;
    private static final int FIELD_43_GUEST_TIME = 42;
    private static final int FIELD_44_CGUEST_TIME = 43;
    private static final int FIELD_45_START_DATA = 44;
    private static final int FIELD_46_END_DATA = 45;
    private static final int FIELD_47_START_BRK = 46;
    private static final int FIELD_48_ARG_START = 47;
    private static final int FIELD_49_ARG_END = 48;
    private static final int FIELD_50_ENV_START = 49;
    private static final int FIELD_51_ENV_END = 50;
    private static final int FIELD_52_EXIT_CODE = 51;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private int pid;

    private Long instanceCreationTime;

    private String executableFileName;

    private long utime;
    private long stime;
    private long cutime;
    private long cstime;
    private long starttime;
    private long guesttime;

    // Constructors ----------------------------------------------------------------------------------------------------

    private PerProcessStat() {

        instanceCreationTime = System.currentTimeMillis();
    }

    /**
     * "Reads" the file information and initializes internal structures.
     *
     * @param pid - the pid of the process the content corresponds to.
     *
     * @param content the content of the file.
     *
     * @exception IllegalArgumentException if the pid value provided as argument of the constructor does not match the
     * pid value extracted from content.
     */
    public PerProcessStat(int pid, byte[] content) throws ParsingException, IllegalArgumentException {

        this();

        if (content == null) {

            throw new IllegalArgumentException("null content");
        }

        this.pid = pid;

        String contentAsString = new String(content);

        parse(contentAsString);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public int getPid() {

        return pid;
    }

    public String getExecutableFileName() {

        return executableFileName;
    }

    public long getUtime() {

        return utime;
    }

    public long getStime() {

        return stime;
    }

    public long getCutime() {

        return cutime;
    }

    public long getCstime() {

        return cstime;
    }

    public long getStarttime() {

        return starttime;
    }

    public long getGuesttime() {

        return guesttime;
    }

    public long getTotalTime() {

        return utime + stime + cutime + cstime;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        String s = "/proc/" + pid + "/stat[";

        s += instanceCreationTime == null ? "UNSTAMPED" : Constants.TIMESTAMP_FORMAT.format(instanceCreationTime);

        s += "]";

        return s;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void parse(String content) throws ParsingException {

        String[] tokens = content.split(" ");

        int index = FIELD_1_PID;
        String s;

        try {

            s = tokens[index];

            int i;

            try {

                i = Integer.parseInt(s);
            }
            catch(Exception e) {

                throw new ParsingException("invalid field 1 (pid) value: " + s);
            }

            if (pid != i) {

                throw new IllegalArgumentException(
                        "pid passed as argument (" + this.pid + ") does not match pid extracted from content (" + i + ")");
            }

            index = FIELD_2_EXECUTABLE_FILE_NAME;

            this.executableFileName = tokens[index];

            if (executableFileName.startsWith("(")) {

                executableFileName = executableFileName.substring(1);
            }

            if (executableFileName.endsWith(")")) {

                executableFileName = executableFileName.substring(0, executableFileName.length() - 1);
            }

            index = FIELD_3_STATE;

            s = tokens[index];

            // TODO process field

            index = FIELD_4_PPID;

            s = tokens[index];

            // TODO process field

            index = FIELD_5_PGRP;

            s = tokens[index];

            // TODO process field

            index = FIELD_6_SESSION;

            s = tokens[index];

            // TODO process field

            index = FIELD_7_TTY_NR;

            s = tokens[index];

            // TODO process field

            index = FIELD_8_TPGID;

            s = tokens[index];

            // TODO process field

            index = FIELD_9_FLAGS;

            s = tokens[index];

            // TODO process field

            index = FIELD_10_MINFLT;

            s = tokens[index];

            // TODO process field

            index = FIELD_11_CMINFLT;

            s = tokens[index];

            // TODO process field

            index = FIELD_12_MAJFLT;

            s = tokens[index];

            // TODO process field

            index = FIELD_13_CMAJFLT;

            s = tokens[index];

            // TODO process field

            index = FIELD_14_UTIME;

            s = tokens[index];

            try {

                this.utime = Long.parseLong(s);
            }
            catch(Exception e) {

                throw new ParsingException("invalid field 14 (utime) value: " + s);
            }

            index = FIELD_15_STIME;

            s = tokens[index];

            try {

                this.stime = Long.parseLong(s);
            }
            catch(Exception e) {

                throw new ParsingException("invalid field 15 (stime) value: " + s);
            }

            index = FIELD_16_CUTIME;

            s = tokens[index];

            try {

                this.cutime = Long.parseLong(s);
            }
            catch(Exception e) {

                throw new ParsingException("invalid field 16 (cutime) value: " + s);
            }

            index = FIELD_17_CSTIME;

            s = tokens[index];

            try {

                this.cstime = Long.parseLong(s);
            }
            catch(Exception e) {

                throw new ParsingException("invalid field 17 (cstime) value: " + s);
            }

            index = FIELD_18_PRIORITY;

            s = tokens[index];

            // TODO process field

            index = FIELD_19_NICE;

            s = tokens[index];

            // TODO process field

            index = FIELD_20_NUM_THREADS;

            s = tokens[index];

            // TODO process field

            index = FIELD_21_ITREALVALUE;

            s = tokens[index];

            // TODO process field

            index = FIELD_22_STARTTIME;

            s = tokens[index];

            try {

                this.starttime = Long.parseLong(s);
            }
            catch(Exception e) {

                throw new ParsingException("invalid field 22 (starttime) value: " + s);
            }

            index = FIELD_23_VSIZE;

            s = tokens[index];

            // TODO process field

            index = FIELD_24_RSS;

            s = tokens[index];

            // TODO process field

            index = FIELD_25_RSSLIM;

            s = tokens[index];

            // TODO process field

            index = FIELD_26_STARTCODE;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_27_ENDCODE;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_28_STARTSTACK;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_29_KSTKESP;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_30_KSTKEIP;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_31_SIGNAL;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_32_BLOCKED;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_33_SIGIGNORE;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_34_SIGCATCH;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_35_WCHAN;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_36_NSWAP;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_37_CNSWAP;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_38_EXIT_SIGNAL;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_39_PROCESSOR;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_40_RT_PRIORITY;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_41_POLICY;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_42_DELAYACCT_BLKIO_TICKS;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_43_GUEST_TIME;

            s = tokens[index];

            try {

                this.guesttime = Long.parseLong(s);
            }
            catch(Exception e) {

                throw new ParsingException("invalid field 43 (guest_time) value: " + s);
            }

            index = FIELD_44_CGUEST_TIME;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_45_START_DATA;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_46_END_DATA;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_47_START_BRK;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_48_ARG_START;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_49_ARG_END;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_50_ENV_START;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_51_ENV_END;

            s = tokens[index];

            // TODO process the rest of the fields

            index = FIELD_52_EXIT_CODE;

            s = tokens[index];

            // TODO process the rest of the fields
        }
        catch(IndexOutOfBoundsException e) {

            throw new ParsingException("field " + (index + 1) + " missing");
        }

//        long lineNumber = 0;
//
//        try {
//
//            while((line = br.readLine()) != null) {
//
//                lineNumber ++;
//
//                if (line.startsWith(CPU_LINE_PREFIX)) {
//
//                    CPUStats cpuStats = new CPUStats(lineNumber, instanceCreationTime, line);
//
//                    if (cpuStats.isCumulative()) {
//
//                        this.cumulativeCPUStatistics = cpuStats;
//                    }
//                    else {
//
//                        perCPUStatistics.add(cpuStats.getCPUID(), cpuStats);
//                    }
//                }
//            }
//
//            br.close();
//        }
//        catch(Exception e) {
//
//            //
//            // should not happen
//            //
//
//            throw new IllegalStateException(e);
//        }
//
//        //
//        // consistency checks
//        //
//
//        if (cumulativeCPUStatistics == null) {
//
//            throw new ParsingException("missing cpu data");
//        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
