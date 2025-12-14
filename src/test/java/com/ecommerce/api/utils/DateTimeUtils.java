package com.ecommerce.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility class for date and time operations
 * Provides methods for timestamp handling, formatting, parsing, and validation
 */
public class DateTimeUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtils.class);
    
    // Common date/time formats
    public static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String ISO_DATE_TIME_WITH_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public static final String ISO_DATE_TIME_WITH_MILLIS_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String ISO_DATE_TIME_WITH_MILLIS_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
    public static final String ISO_TIME_FORMAT = "HH:mm:ss";
    public static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmss";
    public static final String READABLE_DATE_TIME_FORMAT = "MMM dd, yyyy HH:mm:ss";
    public static final String READABLE_DATE_FORMAT = "MMM dd, yyyy";
    public static final String LOG_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    
    // Common formatters
    public static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(ISO_DATE_TIME_FORMAT);
    public static final DateTimeFormatter ISO_DATE_TIME_WITH_ZONE_FORMATTER = DateTimeFormatter.ofPattern(ISO_DATE_TIME_WITH_ZONE_FORMAT);
    public static final DateTimeFormatter ISO_DATE_TIME_WITH_MILLIS_FORMATTER = DateTimeFormatter.ofPattern(ISO_DATE_TIME_WITH_MILLIS_FORMAT);
    public static final DateTimeFormatter ISO_DATE_TIME_WITH_MILLIS_ZONE_FORMATTER = DateTimeFormatter.ofPattern(ISO_DATE_TIME_WITH_MILLIS_ZONE_FORMAT);
    public static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ofPattern(ISO_DATE_FORMAT);
    public static final DateTimeFormatter ISO_TIME_FORMATTER = DateTimeFormatter.ofPattern(ISO_TIME_FORMAT);
    public static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT);
    public static final DateTimeFormatter READABLE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(READABLE_DATE_TIME_FORMAT);
    public static final DateTimeFormatter READABLE_DATE_FORMATTER = DateTimeFormatter.ofPattern(READABLE_DATE_FORMAT);
    public static final DateTimeFormatter LOG_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern(LOG_TIMESTAMP_FORMAT);
    
    /**
     * Get current timestamp in ISO format
     * @return Current timestamp as ISO string
     */
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(ISO_DATE_TIME_FORMATTER);
    }
    
    /**
     * Get current timestamp in ISO format with timezone
     * @return Current timestamp as ISO string with timezone
     */
    public static String getCurrentTimestampWithZone() {
        return ZonedDateTime.now().format(ISO_DATE_TIME_WITH_ZONE_FORMATTER);
    }
    
    /**
     * Get current timestamp in ISO format with milliseconds
     * @return Current timestamp as ISO string with milliseconds
     */
    public static String getCurrentTimestampWithMillis() {
        return LocalDateTime.now().format(ISO_DATE_TIME_WITH_MILLIS_FORMATTER);
    }
    
    /**
     * Get current timestamp in ISO format with milliseconds and timezone
     * @return Current timestamp as ISO string with milliseconds and timezone
     */
    public static String getCurrentTimestampWithMillisAndZone() {
        return ZonedDateTime.now().format(ISO_DATE_TIME_WITH_MILLIS_ZONE_FORMATTER);
    }
    
    /**
     * Get current date in ISO format
     * @return Current date as ISO string
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(ISO_DATE_FORMATTER);
    }
    
    /**
     * Get current time in ISO format
     * @return Current time as ISO string
     */
    public static String getCurrentTime() {
        return LocalTime.now().format(ISO_TIME_FORMATTER);
    }
    
    /**
     * Get current timestamp for logging
     * @return Current timestamp formatted for logging
     */
    public static String getCurrentLogTimestamp() {
        return LocalDateTime.now().format(LOG_TIMESTAMP_FORMATTER);
    }
    
    /**
     * Get current epoch timestamp in milliseconds
     * @return Current epoch timestamp
     */
    public static long getCurrentEpochMillis() {
        return System.currentTimeMillis();
    }
    
    /**
     * Get current epoch timestamp in seconds
     * @return Current epoch timestamp in seconds
     */
    public static long getCurrentEpochSeconds() {
        return Instant.now().getEpochSecond();
    }
    
    /**
     * Format LocalDateTime to string using specified pattern
     * @param dateTime LocalDateTime to format
     * @param pattern Format pattern
     * @return Formatted date/time string
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        try {
            if (dateTime == null) {
                return null;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return dateTime.format(formatter);
        } catch (Exception e) {
            logger.error("Failed to format datetime {} with pattern {}: {}", dateTime, pattern, e.getMessage());
            return null;
        }
    }
    
    /**
     * Format ZonedDateTime to string using specified pattern
     * @param dateTime ZonedDateTime to format
     * @param pattern Format pattern
     * @return Formatted date/time string
     */
    public static String formatZonedDateTime(ZonedDateTime dateTime, String pattern) {
        try {
            if (dateTime == null) {
                return null;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return dateTime.format(formatter);
        } catch (Exception e) {
            logger.error("Failed to format zoned datetime {} with pattern {}: {}", dateTime, pattern, e.getMessage());
            return null;
        }
    }
    
    /**
     * Parse date/time string to LocalDateTime
     * @param dateTimeString Date/time string to parse
     * @param pattern Format pattern
     * @return Parsed LocalDateTime, or null if parsing fails
     */
    public static LocalDateTime parseDateTime(String dateTimeString, String pattern) {
        try {
            if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
                return null;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDateTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            logger.warn("Failed to parse datetime '{}' with pattern '{}': {}", dateTimeString, pattern, e.getMessage());
            return null;
        }
    }
    
    /**
     * Parse date/time string to ZonedDateTime
     * @param dateTimeString Date/time string to parse
     * @param pattern Format pattern
     * @return Parsed ZonedDateTime, or null if parsing fails
     */
    public static ZonedDateTime parseZonedDateTime(String dateTimeString, String pattern) {
        try {
            if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
                return null;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return ZonedDateTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            logger.warn("Failed to parse zoned datetime '{}' with pattern '{}': {}", dateTimeString, pattern, e.getMessage());
            return null;
        }
    }
    
    /**
     * Parse ISO date/time string to LocalDateTime
     * @param isoDateTimeString ISO date/time string
     * @return Parsed LocalDateTime, or null if parsing fails
     */
    public static LocalDateTime parseIsoDateTime(String isoDateTimeString) {
        return parseDateTime(isoDateTimeString, ISO_DATE_TIME_FORMAT);
    }
    
    /**
     * Parse ISO date/time string with zone to ZonedDateTime
     * @param isoDateTimeString ISO date/time string with zone
     * @return Parsed ZonedDateTime, or null if parsing fails
     */
    public static ZonedDateTime parseIsoDateTimeWithZone(String isoDateTimeString) {
        return parseZonedDateTime(isoDateTimeString, ISO_DATE_TIME_WITH_ZONE_FORMAT);
    }
    
    /**
     * Parse date string to LocalDate
     * @param dateString Date string to parse
     * @param pattern Format pattern
     * @return Parsed LocalDate, or null if parsing fails
     */
    public static LocalDate parseDate(String dateString, String pattern) {
        try {
            if (dateString == null || dateString.trim().isEmpty()) {
                return null;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            logger.warn("Failed to parse date '{}' with pattern '{}': {}", dateString, pattern, e.getMessage());
            return null;
        }
    }
    
    /**
     * Parse ISO date string to LocalDate
     * @param isoDateString ISO date string
     * @return Parsed LocalDate, or null if parsing fails
     */
    public static LocalDate parseIsoDate(String isoDateString) {
        return parseDate(isoDateString, ISO_DATE_FORMAT);
    }
    
    /**
     * Convert epoch milliseconds to LocalDateTime
     * @param epochMillis Epoch milliseconds
     * @return LocalDateTime representation
     */
    public static LocalDateTime fromEpochMillis(long epochMillis) {
        try {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
        } catch (Exception e) {
            logger.error("Failed to convert epoch millis {} to LocalDateTime: {}", epochMillis, e.getMessage());
            return null;
        }
    }
    
    /**
     * Convert epoch seconds to LocalDateTime
     * @param epochSeconds Epoch seconds
     * @return LocalDateTime representation
     */
    public static LocalDateTime fromEpochSeconds(long epochSeconds) {
        try {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault());
        } catch (Exception e) {
            logger.error("Failed to convert epoch seconds {} to LocalDateTime: {}", epochSeconds, e.getMessage());
            return null;
        }
    }
    
    /**
     * Convert LocalDateTime to epoch milliseconds
     * @param dateTime LocalDateTime to convert
     * @return Epoch milliseconds
     */
    public static long toEpochMillis(LocalDateTime dateTime) {
        try {
            if (dateTime == null) {
                return 0;
            }
            return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (Exception e) {
            logger.error("Failed to convert LocalDateTime {} to epoch millis: {}", dateTime, e.getMessage());
            return 0;
        }
    }
    
    /**
     * Convert LocalDateTime to epoch seconds
     * @param dateTime LocalDateTime to convert
     * @return Epoch seconds
     */
    public static long toEpochSeconds(LocalDateTime dateTime) {
        try {
            if (dateTime == null) {
                return 0;
            }
            return dateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        } catch (Exception e) {
            logger.error("Failed to convert LocalDateTime {} to epoch seconds: {}", dateTime, e.getMessage());
            return 0;
        }
    }
    
    /**
     * Add specified amount of time to LocalDateTime
     * @param dateTime Base LocalDateTime
     * @param amount Amount to add
     * @param unit Time unit
     * @return Modified LocalDateTime
     */
    public static LocalDateTime addTime(LocalDateTime dateTime, long amount, ChronoUnit unit) {
        try {
            if (dateTime == null) {
                return null;
            }
            return dateTime.plus(amount, unit);
        } catch (Exception e) {
            logger.error("Failed to add {} {} to {}: {}", amount, unit, dateTime, e.getMessage());
            return dateTime;
        }
    }
    
    /**
     * Subtract specified amount of time from LocalDateTime
     * @param dateTime Base LocalDateTime
     * @param amount Amount to subtract
     * @param unit Time unit
     * @return Modified LocalDateTime
     */
    public static LocalDateTime subtractTime(LocalDateTime dateTime, long amount, ChronoUnit unit) {
        try {
            if (dateTime == null) {
                return null;
            }
            return dateTime.minus(amount, unit);
        } catch (Exception e) {
            logger.error("Failed to subtract {} {} from {}: {}", amount, unit, dateTime, e.getMessage());
            return dateTime;
        }
    }
    
    /**
     * Calculate difference between two LocalDateTime objects
     * @param startDateTime Start date/time
     * @param endDateTime End date/time
     * @param unit Time unit for the result
     * @return Difference in specified unit
     */
    public static long calculateDifference(LocalDateTime startDateTime, LocalDateTime endDateTime, ChronoUnit unit) {
        try {
            if (startDateTime == null || endDateTime == null) {
                return 0;
            }
            return unit.between(startDateTime, endDateTime);
        } catch (Exception e) {
            logger.error("Failed to calculate difference between {} and {} in {}: {}", 
                        startDateTime, endDateTime, unit, e.getMessage());
            return 0;
        }
    }
    
    /**
     * Check if a date/time string is valid for a given pattern
     * @param dateTimeString Date/time string to validate
     * @param pattern Format pattern
     * @return true if valid, false otherwise
     */
    public static boolean isValidDateTime(String dateTimeString, String pattern) {
        try {
            if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
                return false;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime.parse(dateTimeString, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Check if a date/time string is valid ISO format
     * @param isoDateTimeString ISO date/time string
     * @return true if valid, false otherwise
     */
    public static boolean isValidIsoDateTime(String isoDateTimeString) {
        return isValidDateTime(isoDateTimeString, ISO_DATE_TIME_FORMAT);
    }
    
    /**
     * Check if a date string is valid ISO format
     * @param isoDateString ISO date string
     * @return true if valid, false otherwise
     */
    public static boolean isValidIsoDate(String isoDateString) {
        try {
            if (isoDateString == null || isoDateString.trim().isEmpty()) {
                return false;
            }
            LocalDate.parse(isoDateString, ISO_DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Get start of day for a given LocalDateTime
     * @param dateTime LocalDateTime
     * @return LocalDateTime at start of day (00:00:00)
     */
    public static LocalDateTime getStartOfDay(LocalDateTime dateTime) {
        try {
            if (dateTime == null) {
                return null;
            }
            return dateTime.toLocalDate().atStartOfDay();
        } catch (Exception e) {
            logger.error("Failed to get start of day for {}: {}", dateTime, e.getMessage());
            return dateTime;
        }
    }
    
    /**
     * Get end of day for a given LocalDateTime
     * @param dateTime LocalDateTime
     * @return LocalDateTime at end of day (23:59:59.999999999)
     */
    public static LocalDateTime getEndOfDay(LocalDateTime dateTime) {
        try {
            if (dateTime == null) {
                return null;
            }
            return dateTime.toLocalDate().atTime(LocalTime.MAX);
        } catch (Exception e) {
            logger.error("Failed to get end of day for {}: {}", dateTime, e.getMessage());
            return dateTime;
        }
    }
    
    /**
     * Check if a LocalDateTime is between two other LocalDateTime objects
     * @param dateTime DateTime to check
     * @param startDateTime Start of range (inclusive)
     * @param endDateTime End of range (inclusive)
     * @return true if dateTime is within range, false otherwise
     */
    public static boolean isBetween(LocalDateTime dateTime, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        try {
            if (dateTime == null || startDateTime == null || endDateTime == null) {
                return false;
            }
            return !dateTime.isBefore(startDateTime) && !dateTime.isAfter(endDateTime);
        } catch (Exception e) {
            logger.error("Failed to check if {} is between {} and {}: {}", 
                        dateTime, startDateTime, endDateTime, e.getMessage());
            return false;
        }
    }
    
    /**
     * Get timezone offset for current system timezone
     * @return Timezone offset string (e.g., "+05:30", "-08:00")
     */
    public static String getSystemTimezoneOffset() {
        try {
            ZoneOffset offset = ZonedDateTime.now().getOffset();
            return offset.toString();
        } catch (Exception e) {
            logger.error("Failed to get system timezone offset: {}", e.getMessage());
            return "+00:00";
        }
    }
    
    /**
     * Convert LocalDateTime to different timezone
     * @param dateTime LocalDateTime to convert
     * @param fromZone Source timezone
     * @param toZone Target timezone
     * @return Converted LocalDateTime
     */
    public static LocalDateTime convertTimezone(LocalDateTime dateTime, ZoneId fromZone, ZoneId toZone) {
        try {
            if (dateTime == null || fromZone == null || toZone == null) {
                return dateTime;
            }
            ZonedDateTime zonedDateTime = dateTime.atZone(fromZone);
            ZonedDateTime convertedDateTime = zonedDateTime.withZoneSameInstant(toZone);
            return convertedDateTime.toLocalDateTime();
        } catch (Exception e) {
            logger.error("Failed to convert {} from {} to {}: {}", dateTime, fromZone, toZone, e.getMessage());
            return dateTime;
        }
    }
    
    /**
     * Generate a unique timestamp-based identifier
     * @return Unique timestamp-based identifier
     */
    public static String generateTimestampId() {
        return getCurrentTimestampWithMillis().replaceAll("[^0-9]", "") + "_" + 
               String.format("%04d", (int)(Math.random() * 10000));
    }
    
    /**
     * Get age in years from birth date
     * @param birthDate Birth date
     * @return Age in years
     */
    public static int calculateAge(LocalDate birthDate) {
        try {
            if (birthDate == null) {
                return 0;
            }
            return Period.between(birthDate, LocalDate.now()).getYears();
        } catch (Exception e) {
            logger.error("Failed to calculate age from birth date {}: {}", birthDate, e.getMessage());
            return 0;
        }
    }
    
    /**
     * Check if a year is a leap year
     * @param year Year to check
     * @return true if leap year, false otherwise
     */
    public static boolean isLeapYear(int year) {
        try {
            return Year.isLeap(year);
        } catch (Exception e) {
            logger.error("Failed to check if {} is leap year: {}", year, e.getMessage());
            return false;
        }
    }
}