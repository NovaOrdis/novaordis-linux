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
import io.novaordis.utilities.parsing.PreParsedContent;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an immutable /proc/stat "reading" - the state of the file at a certain moment in time.
 *
 * https://kb.novaordis.com/index.php//proc/stat#Contents
 *
 * TODO: support for "page", "swap", "intr", "ctxt", etc. not yet implemented.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/9/17
 */
public class ProcStat implements PreParsedContent {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String CPU_LINE_PREFIX = "cpu";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Long instanceCreationTime;

    private CPUStats cumulativeCPUStatistics;

    //
    // indexed per CPU id, as read from the file
    //

    private List<CPUStats> perCPUStatistics;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * "Reads" the file information and initializes internal structures.
     *
     * @param content the content of the file.
     */
    public ProcStat(byte[] content) throws ParsingException {

        if (content == null) {

            throw new IllegalArgumentException("null content");
        }

        instanceCreationTime = System.currentTimeMillis();

        perCPUStatistics = new ArrayList<>();

        String contentAsString = new String(content);

        parse(contentAsString);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return the cumulative CPU statistics as read from the "cpu" line.
     */
    public CPUStats getCumulativeCPUStatistics() {

        return cumulativeCPUStatistics;
    }

    /**
     * @return the number of CPUs the file contains statistics for. The CPU indexes are 0-based.
     */
    public int getCPUCount() {

        return perCPUStatistics.size();
    }

    /**
     * @return the CPU statistics for the corresponding CPU. The CPU indexes are 0-based.
     *
     * @exception IllegalArgumentException if no such CPU exists.
     */
    public CPUStats getCPUStatistics(int cpuIndex) {

        try {

            return perCPUStatistics.get(cpuIndex);
        }
        catch(IndexOutOfBoundsException e) {

            throw new IllegalArgumentException("no such cpu: " + cpuIndex, e);
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    @Override
    public String toString() {

        String s = "/proc/stat[";

        s += instanceCreationTime == null ? "UNSTAMPED" : Constants.TIMESTAMP_FORMAT.format(instanceCreationTime);

        s += "]";

        return s;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void parse(String content) throws ParsingException {

        BufferedReader br = new BufferedReader(new StringReader(content));

        String line;

        long lineNumber = 0;

        try {

            while((line = br.readLine()) != null) {

                lineNumber ++;

                if (line.startsWith(CPU_LINE_PREFIX)) {

                    CPUStats cpuStats = new CPUStats(lineNumber, instanceCreationTime, line);

                    if (cpuStats.isCumulative()) {

                        this.cumulativeCPUStatistics = cpuStats;
                    }
                    else {

                        perCPUStatistics.add(cpuStats.getCPUID(), cpuStats);
                    }
                }
            }

            br.close();
        }
        catch(Exception e) {

            //
            // should not happen
            //

            throw new IllegalStateException(e);
        }

        //
        // consistency checks
        //

        if (cumulativeCPUStatistics == null) {

            throw new ParsingException("missing cpu data");
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
