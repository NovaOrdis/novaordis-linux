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

import java.text.SimpleDateFormat;

import io.novaordis.linux.CPUStats;
import io.novaordis.linux.PerProcessStat;
import io.novaordis.linux.ProcStat;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 3/1/18
 */
public class Reading {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("MM/dd/yy hh:mm:ss,SSS");

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private long time;
    private ProcStat ps;
    private PerProcessStat pps;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param ps
     * @param pps may be null.
     */
    public Reading(long readingTime, ProcStat ps, PerProcessStat pps) {

        this.time = readingTime;
        this.ps = ps;
        this.pps = pps;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String toCsvHeader() {

        return "# time, user, system, idle, pid, process-utime, process-stime, process-cutime, process-cstime";
    }

    public String toCsv() {

        StringBuilder line = new StringBuilder();

        line.append(TIMESTAMP_FORMAT.format(time)).append(", ");

        CPUStats cpus = ps.getCumulativeCPUStatistics();

        line.append(cpus.getUserTime()).append(", ");
        line.append(cpus.getSystemTime()).append(", ");
        line.append(cpus.getIdleTime()).append(", ");

        if (pps == null) {

            line.append(", , ");
        }
        else {

            line.append(pps.getPid()).append(", ");
            line.append(pps.getUtime()).append(", ");
            line.append(pps.getStime()).append(", ");
            line.append(pps.getCutime()).append(", ");
            line.append(pps.getCstime());
        }

        return line.toString();
    }

    public long getTime() {

        return time;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
