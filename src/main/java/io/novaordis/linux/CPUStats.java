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

/**
 * The immutable representation of a "cpu" line.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 9/9/17
 */
public class CPUStats {

    // Constants -------------------------------------------------------------------------------------------------------

    public static byte USER_TIME_TOKEN_INDEX = 0;
    public static byte NICE_TIME_TOKEN_INDEX = 1;
    public static byte SYSTEM_TIME_TOKEN_INDEX = 2;
    public static byte IDLE_TIME_TOKEN_INDEX = 3;
    public static byte IOWAIT_TIME_TOKEN_INDEX = 4;
    public static byte IRQ_TIME_TOKEN_INDEX = 5;
    public static byte SOFTIRQ_TIME_TOKEN_INDEX = 6;
    public static byte STEAL_TIME_TOKEN_INDEX = 7;
    public static byte GUEST_TIME_TOKEN_INDEX = 8;
    public static byte GUEST_NICE_TIME_TOKEN_INDEX = 9;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    //
    // a 0-based CPU ID index. If missing (null), this instance contains cumulative statistics for all processors in
    // the system
    //
    private Short cpuId;

    private long userTime;
    private long niceTime;
    private long systemTime;
    private long idleTime;
    private long iowaitTime;
    private long irqTime;
    private long softirqTime;
    private long stealTime;
    private long guestTime;
    private long guestNiceTime;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @throws ParsingException
     * @throws IllegalArgumentException on null line
     */
    public CPUStats(Long lineNumber, String line) throws ParsingException {

        parse(lineNumber, line);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return the CPU ID or null if the statistics are cumulative.
     *
     * @see CPUStats#isCumulative()
     */
    public Short getCPUID() {

        return cpuId;
    }

    /**
     * @return true if the statistics are cumulative, representing all processors in the system - the stats were read
     * from a "cpu" line, false otherwise (the stats were read from a cpu<id> line.
     *
     * @see CPUStats#getCPUID()
     */
    public boolean isCumulative() {

        return cpuId == null;
    }

    /**
     *  Time (in USER_HZ units) spent in user mode.
     */
    public long getUserTime() {

        return userTime;
    }

    /**
     * @param previousReading may be null.
     *
     * @return the percentage of the total CPU time (calculated as the sum of the user, nice, system, idle, iowait, irq,
     * softirq, steal, guest and guest_nice readings) spent in user mode. If a previous reading is provided, the
     * percentage is calculated using the user time and total time deltas, otherwise it is calculated based on this
     * current reading only, which contains aggregated values since the last boot. Closer the readings are, more precise
     * the result is.
     *
     * @exception IllegalArgumentException if the current state does not differ from the previous state (total time
     * is the same).
     */
    public float getUserTimePercentage(CPUStats previousReading) {

        if (previousReading == null) {

            return (float)(((double)getUserTime())/getTotalTime());
        }
        else {

            insureStateChanged(previousReading);

            return (float)
                    (((double)(getUserTime() - previousReading.getUserTime()))/
                            (getTotalTime() - previousReading.getTotalTime()));
        }
    }

    /**
     *  Time (in USER_HZ units) spent in user mode with low priority (nice).
     */
    public long getNiceTime() {

        return niceTime;
    }

    /**
     * @param previousReading may be null.
     *
     * @return the percentage of the total CPU time (calculated as the sum of the user, nice, system, idle, iowait, irq,
     * softirq, steal, guest and guest_nice readings) spent in nice user mode. If a previous reading is provided, the
     * percentage is calculated using the nice time and total time deltas, otherwise it is calculated based on this
     * current reading only, which contains aggregated values since the last boot. Closer the readings are, more precise
     * the result is.
     *
     * @exception IllegalArgumentException if the current state does not differ from the previous state (total time
     * is the same).
     */
    public float getNiceTimePercentage(CPUStats previousReading) {

        if (previousReading == null) {

            return (float)(((double)getNiceTime())/getTotalTime());
        }
        else {

            insureStateChanged(previousReading);

            return (float)
                    (((double)(getNiceTime() - previousReading.getNiceTime()))/
                            (getTotalTime() - previousReading.getTotalTime()));
        }
    }

    /**
     *  Time (in USER_HZ units) spent in system mode: kernel executing system calls on behalf of processes.
     */
    public long getSystemTime() {

        return systemTime;
    }

    /**
     * @param previousReading may be null.
     *
     * @return the percentage of the total CPU time (calculated as the sum of the user, nice, system, idle, iowait, irq,
     * softirq, steal, guest and guest_nice readings) spent in system mode. If a previous reading is provided, the
     * percentage is calculated using the system time and total time deltas, otherwise it is calculated based on this
     * current reading only, which contains aggregated values since the last boot. Closer the readings are, more precise
     * the result is.
     *
     * @exception IllegalArgumentException if the current state does not differ from the previous state (total time
     * is the same).
     */
    public float getSystemTimePercentage(CPUStats previousReading) {

        if (previousReading == null) {

            return (float)(((double)getSystemTime())/getTotalTime());
        }
        else {

            insureStateChanged(previousReading);

            return (float)
                    (((double)(getSystemTime() - previousReading.getSystemTime()))/
                            (getTotalTime() - previousReading.getTotalTime()));
        }
    }

    /**
     *  Time (in USER_HZ units) spent in idle mode.
     */
    public long getIdleTime() {

        return idleTime;
    }

    /**
     * @param previousReading may be null.
     *
     * @return the percentage of the total CPU time (calculated as the sum of the user, nice, system, idle, iowait, irq,
     * softirq, steal, guest and guest_nice readings) spent in idle mode. If a previous reading is provided, the
     * percentage is calculated using the idle time and total time deltas, otherwise it is calculated based on this
     * current reading only, which contains aggregated values since the last boot. Closer the readings are, more precise
     * the result is.
     *
     * @exception IllegalArgumentException if the current state does not differ from the previous state (total time
     * is the same).
     */
    public float getIdleTimePercentage(CPUStats previousReading) {

        if (previousReading == null) {

            return (float)(((double)getIdleTime())/getTotalTime());
        }
        else {

            insureStateChanged(previousReading);

            return (float)
                    (((double)(getIdleTime() - previousReading.getIdleTime()))/
                            (getTotalTime() - previousReading.getTotalTime()));
        }
    }

    /**
     *  Time in USER_HZ units processes are waiting for I/O to complete
     */
    public long getIowaitTime() {

        return iowaitTime;
    }

    /**
     * @param previousReading may be null.
     *
     * @return the percentage of the total CPU time (calculated as the sum of the user, nice, system, idle, iowait, irq,
     * softirq, steal, guest and guest_nice readings) spent waiting for I/O to complete. If a previous reading is
     * provided, the percentage is calculated using the iowait time and total time deltas, otherwise it is calculated
     * based on this current reading only, which contains aggregated values since the last boot. Closer the readings
     * are, more precise the result is.
     *
     * @exception IllegalArgumentException if the current state does not differ from the previous state (total time
     * is the same).
     */
    public float getIowaitTimePercentage(CPUStats previousReading) {

        if (previousReading == null) {

            return (float)(((double)getIowaitTime())/getTotalTime());
        }
        else {

            insureStateChanged(previousReading);

            return (float)
                    (((double)(getIowaitTime() - previousReading.getIowaitTime()))/
                            (getTotalTime() - previousReading.getTotalTime()));
        }
    }

    /**
     *  Time in USER_HZ units spent servicing interrupts.
     */
    public long getIrqTime() {

        return irqTime;
    }

    /**
     * @param previousReading may be null.
     *
     * @return the percentage of the total CPU time (calculated as the sum of the user, nice, system, idle, iowait, irq,
     * softirq, steal, guest and guest_nice readings) spent servicing hardware interrupts. If a previous reading is
     * provided, the percentage is calculated using the irq time and total time deltas, otherwise it is calculated based
     * on this current reading only, which contains aggregated values since the last boot. Closer the readings are, more
     * precise the result is.
     *
     * @exception IllegalArgumentException if the current state does not differ from the previous state (total time
     * is the same).
     */
    public float getIrqTimePercentage(CPUStats previousReading) {

        if (previousReading == null) {

            return (float)(((double)getIrqTime())/getTotalTime());
        }
        else {

            insureStateChanged(previousReading);

            return (float)
                    (((double)(getIrqTime() - previousReading.getIrqTime()))/
                            (getTotalTime() - previousReading.getTotalTime()));
        }
    }

    /**
     *  Time in USER_HZ units spent servicing softirqs.
     */
    public long getSoftirqTime() {

        return softirqTime;
    }

    /**
     * @param previousReading may be null.
     *
     * @return the percentage of the total CPU time (calculated as the sum of the user, nice, system, idle, iowait, irq,
     * softirq, steal, guest and guest_nice readings) spent servicing software interrupts. If a previous reading is
     * provided, the percentage is calculated using the softirq time and total time deltas, otherwise it is calculated
     * based on this current reading only, which contains aggregated values since the last boot. Closer the readings
     * are, more precise the result is.
     *
     * @exception IllegalArgumentException if the current state does not differ from the previous state (total time
     * is the same).
     */
    public float getSoftirqTimePercentage(CPUStats previousReading) {

        if (previousReading == null) {

            return (float)(((double)getSoftirqTime())/getTotalTime());
        }
        else {

            insureStateChanged(previousReading);

            return (float)
                    (((double)(getSoftirqTime() - previousReading.getSoftirqTime()))/
                            (getTotalTime() - previousReading.getTotalTime()));
        }
    }

    /**
     *  Time in USER_HZ units spent in other operating systems when running in a virtualized environment.
     */
    public long getStealTime() {

        return stealTime;
    }

    /**
     * @param previousReading may be null.
     *
     * @return the percentage of the total CPU time (calculated as the sum of the user, nice, system, idle, iowait, irq,
     * softirq, steal, guest and guest_nice readings) spent in other operating systems. If a previous reading is
     * provided, the percentage is calculated using the steal time and total time deltas, otherwise it is calculated
     * based on this current reading only, which contains aggregated values since the last boot. Closer the readings
     * are, more precise the result is.
     *
     * @exception IllegalArgumentException if the current state does not differ from the previous state (total time
     * is the same).
     */
    public float getStealTimePercentage(CPUStats previousReading) {

        if (previousReading == null) {

            return (float)(((double)getStealTime())/getTotalTime());
        }
        else {

            insureStateChanged(previousReading);

            return (float)
                    (((double)(getStealTime() - previousReading.getStealTime()))/
                            (getTotalTime() - previousReading.getTotalTime()));
        }
    }

    /**
     *  Time in USER_HZ units spent running a virtual CPU for guest operating systems under the control of the Linux
     *  kernel.
     */
    public long getGuestTime() {

        return guestTime;
    }

    /**
     * @param previousReading may be null.
     *
     * @return the percentage of the total CPU time (calculated as the sum of the user, nice, system, idle, iowait, irq,
     * softirq, steal, guest and guest_nice readings) spent running a virtual CPU for guest operating systems. If a
     * previous reading is provided, the percentage is calculated using the guest time and total time deltas, otherwise
     * it is calculated based on this current reading only, which contains aggregated values since the last boot.
     * Closer the readings are, more precise the result is.
     *
     * @exception IllegalArgumentException if the current state does not differ from the previous state (total time
     * is the same).
     */
    public float getGuestTimePercentage(CPUStats previousReading) {

        if (previousReading == null) {

            return (float)(((double)getGuestTime())/getTotalTime());
        }
        else {

            insureStateChanged(previousReading);

            return (float)
                    (((double)(getGuestTime() - previousReading.getGuestTime()))/
                            (getTotalTime() - previousReading.getTotalTime()));
        }
    }

    /**
     *  Time in USER_HZ units spent running a niced guest (virtual CPU for guest operating systems under the control of
     *  the Linux kernel).
     */
    public long getGuestNiceTime() {

        return guestNiceTime;
    }

    /**
     * @param previousReading may be null.
     *
     * @return the percentage of the total CPU time (calculated as the sum of the user, nice, system, idle, iowait, irq,
     * softirq, steal, guest and guest_nice readings) spent running a niced guest. If a previous reading is provided,
     * the percentage is calculated using the user guest time and total time deltas, otherwise it is calculated based on
     * this current reading only, which contains aggregated values since the last boot. Closer the readings are, more
     * precise the result is.
     *
     * @exception IllegalArgumentException if the current state does not differ from the previous state (total time
     * is the same).
     */
    public float getGuestNiceTimePercentage(CPUStats previousReading) {

        if (previousReading == null) {

            return (float)(((double)getGuestNiceTime())/getTotalTime());
        }
        else {

            insureStateChanged(previousReading);

            return (float)
                    (((double)(getGuestNiceTime() - previousReading.getGuestNiceTime()))/
                            (getTotalTime() - previousReading.getTotalTime()));
        }
    }

    /**
     *  Total CPU time in USER_HZ units. Represents the sum of user, nice, system, idle, iowait, irq, softirq, steal,
     *  guest and guest_nice readings.
     */
    public long getTotalTime() {

        return
                userTime +
                        niceTime +
                        systemTime +
                        idleTime +
                        iowaitTime +
                        irqTime +
                        softirqTime +
                        stealTime +
                        guestTime +
                        guestNiceTime;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void parse(Long lineNumber, String line) throws ParsingException {

        if (line == null) {

            throw new IllegalArgumentException("null line");
        }

        String s = line.trim();

        if (!s.startsWith(ProcStat.CPU_LINE_PREFIX)) {

            throw new ParsingException(lineNumber, "not a cpu statistics line: " + line);
        }

        s = s.substring(ProcStat.CPU_LINE_PREFIX.length());

        if (!s.startsWith(" ") && !s.startsWith("\t")) {

            //
            // individual CPU line (otherwise is a cumulative line and procId stays null
            //

            int i = 0;

            for(; i < s.length(); i ++) {

                if (s.charAt(i) == ' ' || s.charAt(i) == '\t') {

                    break;
                }
            }

            String cpuId = s.substring(0, i);

            try {

                this.cpuId = Short.parseShort(cpuId);
            }
            catch(Exception e) {

                throw new ParsingException(lineNumber, "invalid cpu ID: " + cpuId, e);
            }

            s = s.substring(i);
        }

        int i = 0;

        for(; i < s.length(); i ++) {

            if (s.charAt(i) != ' ' && s.charAt(i) != '\t') {

                break;
            }
        }

        s = s.substring(i);

        String[] tokens = s.split("[ \t]");

        String token = tokens[USER_TIME_TOKEN_INDEX];

        try {

            userTime = Long.parseLong(token);
        }
        catch(Exception e) {

            throw new ParsingException(lineNumber, "invalid user time value: " + token, e);
        }

        token = tokens[NICE_TIME_TOKEN_INDEX];

        try {

            niceTime = Long.parseLong(token);
        }
        catch(Exception e) {

            throw new ParsingException(lineNumber, "invalid nice time value: " + token, e);
        }

        token = tokens[SYSTEM_TIME_TOKEN_INDEX];

        try {

            systemTime = Long.parseLong(token);
        }
        catch(Exception e) {

            throw new ParsingException(lineNumber, "invalid system time value: " + token, e);
        }

        token = tokens[IDLE_TIME_TOKEN_INDEX];

        try {

            idleTime = Long.parseLong(token);
        }
        catch(Exception e) {

            throw new ParsingException(lineNumber, "invalid idle time value: " + token, e);
        }

        token = tokens[IOWAIT_TIME_TOKEN_INDEX];

        try {

            iowaitTime = Long.parseLong(token);
        }
        catch(Exception e) {

            throw new ParsingException(lineNumber, "invalid iowait time value: " + token, e);
        }

        token = tokens[IRQ_TIME_TOKEN_INDEX];

        try {

            irqTime = Long.parseLong(token);
        }
        catch(Exception e) {

            throw new ParsingException(lineNumber, "invalid irq time value: " + token, e);
        }

        token = tokens[SOFTIRQ_TIME_TOKEN_INDEX];

        try {

            softirqTime = Long.parseLong(token);
        }
        catch(Exception e) {

            throw new ParsingException(lineNumber, "invalid softirq time value: " + token, e);
        }

        token = tokens[STEAL_TIME_TOKEN_INDEX];

        try {

            stealTime = Long.parseLong(token);
        }
        catch(Exception e) {

            throw new ParsingException(lineNumber, "invalid steal time value: " + token, e);
        }

        token = tokens[GUEST_TIME_TOKEN_INDEX];

        try {

            guestTime = Long.parseLong(token);
        }
        catch(Exception e) {

            throw new ParsingException(lineNumber, "invalid guest time value: " + token, e);
        }

        token = tokens[GUEST_NICE_TIME_TOKEN_INDEX];

        try {

            guestNiceTime = Long.parseLong(token);
        }
        catch(Exception e) {

            throw new ParsingException(lineNumber, "invalid guest_nice time value: " + token, e);
        }
    }

    private void insureStateChanged(CPUStats previousReading) {

        if (previousReading.getTotalTime() == getTotalTime()) {

            throw new IllegalArgumentException("cpu statistics did not change since the last reading");
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
