package com.moonlight.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class DateOrDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
    );

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String dateString = parser.getText();

        try {
            if (dateString.matches("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}")) {
                return LocalDateTime.parse(dateString, DATE_FORMATTERS.get(0));
            } else if (dateString.matches("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}")) {
                return LocalDateTime.parse(dateString, DATE_FORMATTERS.get(2));
            } else {
                return LocalDateTime.parse(dateString + " 19:00:00", DATE_FORMATTERS.get(0));
            }
        } catch (DateTimeParseException e) {
            throw new IOException("Unable to deserialize date: " + dateString, e);
        }
    }
}
