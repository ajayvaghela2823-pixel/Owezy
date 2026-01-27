package com.owezy.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {

    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    private static final SimpleDateFormat MONTH_YEAR_FORMAT = new SimpleDateFormat("MMM yyyy", Locale.US);
    private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("dd MMM", Locale.US);

    /**
     * Format timestamp to display format (e.g., "Jan 21, 2024")
     */
    public static String formatDate(long timestamp) {
        return DISPLAY_FORMAT.format(new Date(timestamp));
    }

    /**
     * Format timestamp to month-year (e.g., "Mar 2024")
     */
    public static String formatMonthYear(long timestamp) {
        return MONTH_YEAR_FORMAT.format(new Date(timestamp));
    }

    /**
     * Format timestamp to short date (e.g., "21 Jan")
     */
    public static String formatShortDate(long timestamp) {
        return SHORT_DATE_FORMAT.format(new Date(timestamp));
    }

    /**
     * Get current timestamp
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * Get start of month timestamp
     */
    public static long getStartOfMonth(long timestamp) {
        // Simplified version - return first day of current month
        return timestamp - (timestamp % (30L * 24 * 60 * 60 * 1000));
    }
}
