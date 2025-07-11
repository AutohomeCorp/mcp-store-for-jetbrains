package com.autohome.mcpstore.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;


public class JacksonEnhancedDateDeserializer extends DateDeserializers.DateDeserializer {

    private static final Pattern PATTERN_TIMESTAMP = Pattern.compile("/Date\\((\\d+)(\\+\\d{4})?\\)/");

    private static Collection<String> APPEND_DATE_FORMATS = new ArrayList<>();

    static {
        APPEND_DATE_FORMATS.add("yyyy-MM-dd'T'HH:mm:ss'Z'");
        APPEND_DATE_FORMATS.add("yyyy-MM-dd'T'HH:mm:ss");
        APPEND_DATE_FORMATS.add("yyyy-MM-dd HH:mm:ss.SSS");
        APPEND_DATE_FORMATS.add("yyyy-MM-dd HH:mm:ss");
        APPEND_DATE_FORMATS.add("yyyy-MM-dd HH:mm");
        APPEND_DATE_FORMATS.add("EEE MMM d HH:mm:ss z yyyy");
        APPEND_DATE_FORMATS.addAll(JsonDateHelper.DEFAULT_HTTP_CLIENT_PATTERNS);
    }

    public JacksonEnhancedDateDeserializer() {

    }

    public JacksonEnhancedDateDeserializer(DateDeserializers.DateDeserializer base, DateFormat df, String formatString) {
        super(base, df, formatString);
    }

    @Override
    public Date deserialize(final JsonParser jsonparser, final DeserializationContext context) throws IOException {
        if (jsonparser == null) {
            throw new IllegalArgumentException("deserialize null date field ");
        }
        try {
            return super.deserialize(jsonparser, context);
        } catch (Exception e) {
            return appendParse(jsonparser.getText());
        }
    }

    protected Date appendParse(String dateString) {
        try {
            Matcher matcher = PATTERN_TIMESTAMP.matcher(dateString);
            if (matcher.matches()) {
                String dateStr = matcher.group(1);
                return new Date(Long.parseLong(dateStr));
            }
            return JsonDateHelper.parseDate(dateString, APPEND_DATE_FORMATS);
        } catch (ParseException e) {
            throw new IllegalArgumentException(MessageFormat.format("Unable to parse {0}", dateString));
        }
    }

    @Override
    public boolean isCachable() {
        return true;
    }

    @Override
    protected JacksonEnhancedDateDeserializer withDateFormat(DateFormat df, String formatString) {
        return new JacksonEnhancedDateDeserializer(this, df, formatString);
    }
}