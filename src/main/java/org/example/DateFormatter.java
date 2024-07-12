package org.example;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
    public static void formateDate(String date) {
        // Example date-time string in ISO 8601 format "2021-01-01T00:00:00Z";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, formatter);
        System.out.println("Parsed date and time: " + zonedDateTime);
        String formattedDateTimeString = zonedDateTime.format(formatter);
        System.out.println("Formatted date and time: " + formattedDateTimeString);
    }
}
