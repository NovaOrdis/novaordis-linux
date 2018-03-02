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

import java.text.DecimalFormat;
import java.text.Format;
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

    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("MM/dd/yy hh:mm:ss.SSS");

    private static final Format PERCENTAGE_FORMAT = new DecimalFormat("0.00");

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Reading previousReading;
    private long time;
    private ProcStat ps;
    private PerProcessStat pps;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param ps may not be null.
     * @param pps may be null.
     * @param  previousReading the previous reading instance, which allows us to calculate utilization percentages.
     *                         May be null, in which case percentages won't be calculated.
     */
    Reading(long readingTime, ProcStat ps, PerProcessStat pps, Reading previousReading) {

        if (ps == null) {

            throw new IllegalArgumentException("null ProcStat instance");
        }

        this.time = readingTime;
        this.ps = ps;
        this.pps = pps;
        this.previousReading = previousReading;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String toCsvHeader() {

        return "# time, user (ct), system (ct), idle (ct), pid, process-utime (ct), process-stime (ct), process-cutime (ct), process-cstime (ct), process-cpu-utilization (%)";
    }

    public String toCsv() {

        StringBuilder line = new StringBuilder();

        line.append(TIMESTAMP_FORMAT.format(time)).append(", ");

        CPUStats cpus = ps.getCumulativeCPUStatistics();

        line.append(cpus.getUserTime()).append(", ");
        line.append(cpus.getSystemTime()).append(", ");
        line.append(cpus.getIdleTime()).append(", ");

        if (pps == null) {

            line.append(", , , , , ");
        }
        else {

            line.append(pps.getPid()).append(", ");
            line.append(pps.getUtime()).append(", ");
            line.append(pps.getStime()).append(", ");
            line.append(pps.getCutime()).append(", ");
            line.append(pps.getCstime()).append(", ");

            Double cpuUtilization = computeCpuUtilization();

            if (cpuUtilization != null) {

                line.append(PERCENTAGE_FORMAT.format(100 * cpuUtilization));
            }
        }

        return line.toString();
    }

    public long getTime() {

        return time;
    }

    public ProcStat getProcStat() {

        return ps;
    }

    public PerProcessStat getPerProcessStat() {

        return pps;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    /**
     * Severs the connection to the previous reading, thus avoiding accumulation of a long GC uncollectable linked
     * list in memory.
     */
    void clear() {

        this.previousReading = null;
    }

    /**
     * Computes CPU utilization percentage (a value between 0 and 1) relative to the previous Reading. Returns null if
     * there's no previous reading, or the previous reading did not have a per-process statistics, or if this reading
     * does not have a per-process statistics.
     */
    Double computeCpuUtilization() {

        if (pps == null || previousReading == null || previousReading.getPerProcessStat() == null) {

            return null;
        }

        //
        // total clock ticks counted by all processors (user + system + idle + ...) since previous reading
        //

        CPUStats previousCpuStats = previousReading.getProcStat().getCumulativeCPUStatistics();
        CPUStats crtCpuStats = ps.getCumulativeCPUStatistics();
        long ct = crtCpuStats.getTotalTime() - previousCpuStats.getTotalTime();

        if (ct < 0) {

            throw new IllegalStateException("current cumulative CPU time less than previous cumulative CPU time");
        }
        else if (ct == 0) {

            //
            // nothing happened on that processor, it may be we're sampling too fast
            //

            return null;
        }

        //
        // clock ticks used by this process in user space and kernel space
        //

        long thisProcCt = pps.getTotalTime() - previousReading.getPerProcessStat().getTotalTime();

        return ((double)thisProcCt)/ct;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
