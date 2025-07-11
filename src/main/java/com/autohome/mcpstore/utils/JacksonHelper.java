package com.autohome.mcpstore.utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.StdDateFormat;


public final class JacksonHelper {
    private static final ObjectMapper COMMON_DEFINE_OBJECT_MAPPER;

    static {
        COMMON_DEFINE_OBJECT_MAPPER = getCommonObjectMapper();
    }

    private JacksonHelper() {
    }

    public static <T> String serialize(T object) {
        return serialize(object, COMMON_DEFINE_OBJECT_MAPPER);
    }

    public static <T> String serialize(T object, ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T deSerialize(String jsonString, Class<T> classT) {
        return deSerialize(jsonString, classT, COMMON_DEFINE_OBJECT_MAPPER);
    }

    public static <T> T deSerialize(String jsonString, Class<T> classT, ObjectMapper objectMapper) {
        if (jsonString == null || jsonString.equalsIgnoreCase("")) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonString, classT);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T deSerialize(String jsonString, TypeReference<T> typeReference) {
        return deSerialize(jsonString, typeReference, COMMON_DEFINE_OBJECT_MAPPER);
    }

    public static <T> T deSerialize(String jsonString, TypeReference<T> typeReference, ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(jsonString, typeReference);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T deSerialize(String jsonString, JavaType javaType) {
        return deSerialize(jsonString, javaType, COMMON_DEFINE_OBJECT_MAPPER);
    }

    public static <T> T deSerialize(String jsonString, JavaType javaType, ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(jsonString, javaType);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T deSerialize(String string, Type type) {
        JavaType javaType = TypeFactory.defaultInstance().constructType(type);
        return deSerialize(string, javaType);
    }

    public static <T> List<T> deSerializeList(String jsonString, Class<T> classT) {
        return deSerializeList(jsonString, classT, COMMON_DEFINE_OBJECT_MAPPER);
    }

    public static <T> List<T> deSerializeList(String jsonString, Class<T> classT, ObjectMapper objectMapper) {
        JavaType javaType = getCollectionType(objectMapper, ArrayList.class, classT);
        return (List<T>) deSerialize(jsonString, javaType, objectMapper);
    }

    public static JavaType getCollectionType(ObjectMapper objectMapper, Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static ObjectMapper getCommonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        return settingCommonObjectMapper(objectMapper);
    }

    public static ObjectMapper settingCommonObjectMapper(ObjectMapper objectMapper) {
        objectMapper.setDateFormat(new StdDateFormat());
        objectMapper.setTimeZone(TimeZone.getDefault());

        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_TRAILING_COMMA, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);


        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.configure(MapperFeature.INFER_PROPERTY_MUTATORS, false);
        objectMapper.configure(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS, false);

        SimpleModule enhancedDateModule = new SimpleModule().addDeserializer(Date.class, new JacksonEnhancedDateDeserializer());
        objectMapper.registerModule(enhancedDateModule);
        return objectMapper;
    }
}


