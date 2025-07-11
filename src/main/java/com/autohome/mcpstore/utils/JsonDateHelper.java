package com.autohome.mcpstore.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

public class JsonDateHelper {
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private static ThreadLocalDateFormat fmtThreadLocal = new ThreadLocalDateFormat();

    /**
     * Date format pattern used to parse HTTP date headers in RFC 1123 format.
     */
    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

    /**
     * Date format pattern used to parse HTTP date headers in RFC 1036 format.
     */
    public static final String PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz";

    /**
     * Date format pattern used to parse HTTP date headers in ANSI C
     * <code>asctime()</code> format.
     */
    public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";

    public static final Collection<String> DEFAULT_HTTP_CLIENT_PATTERNS = Collections.unmodifiableList(Arrays.asList(
            PATTERN_ASCTIME, PATTERN_RFC1036, PATTERN_RFC1123));

    private static final Date DEFAULT_TWO_DIGIT_YEAR_START;

    static {
        Calendar calendar = Calendar.getInstance(UTC, Locale.ROOT);
        calendar.set(1970, Calendar.JANUARY, 1, 0, 0);
        DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
    }

    private static class ThreadLocalDateFormat extends ThreadLocal<DateFormat> {
        DateFormat proto;

        public ThreadLocalDateFormat() {
            super();
            //2007-04-26T08:05:04Z
            SimpleDateFormat tmp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ROOT);
            tmp.setTimeZone(UTC);
            proto = tmp;
        }

        @Override
        protected DateFormat initialValue() {
            return (DateFormat) proto.clone();
        }
    }

    /**
     * Returns a formatter that can be use by the current thread if needed to
     * convert Date objects to the Internal representation.
     *
     * @return The {@link DateFormat} for the current thread
     */
    public static DateFormat getThreadLocalDateFormat() {
        return fmtThreadLocal.get();
    }


    public static Date parseDate(String d, Collection<String> fmts) throws ParseException {
        if (d.endsWith("Z") && d.length() > 20) {
            return getThreadLocalDateFormat().parse(d);
        }
        return parseDate(d, fmts, null);
    }

    /**
     * Slightly modified from org.apache.commons.httpclient.util.DateUtil.parseDate
     * <p>
     * Parses the date value using the given date formats.
     *
     * @param dateValue   the date value to parse
     * @param dateFormats the date formats to use
     * @param startDate   During parsing, two digit years will be placed in the range
     *                    <code>startDate</code> to <code>startDate + 100 years</code>. This value may
     *                    be <code>null</code>. When <code>null</code> is given as a parameter, year
     *                    <code>2000</code> will be used.
     * @return the parsed date
     * @throws ParseException if none of the dataFormats could parse the dateValue
     */
    public static Date parseDate(String dateValue, Collection<String> dateFormats, Date startDate) throws ParseException {
        if (dateValue == null) {
            throw new IllegalArgumentException("dateValue is null");
        }

        if (startDate == null) {
            startDate = DEFAULT_TWO_DIGIT_YEAR_START;
        }
        // trim single quotes around date if present
        // see issue #5279
        if (dateValue.length() > 1 && dateValue.startsWith("'") && dateValue.endsWith("'")) {
            dateValue = dateValue.substring(1, dateValue.length() - 1);
        }

        SimpleDateFormat dateParser = null;
        Iterator formatIter = dateFormats.iterator();

        while (formatIter.hasNext()) {
            String format = (String) formatIter.next();
            if (dateParser == null) {
                dateParser = new SimpleDateFormat(format, Locale.getDefault());
                dateParser.setTimeZone(TimeZone.getDefault());
                dateParser.set2DigitYearStart(startDate);
            } else {
                dateParser.applyPattern(format);
            }
            try {
                return dateParser.parse(dateValue);
            } catch (ParseException pe) {
                // ignore this exception, we will try the next format
            }
        }

        // we were unable to parse the date
        throw new ParseException("Unable to parse the date " + dateValue, 0);
    }
}