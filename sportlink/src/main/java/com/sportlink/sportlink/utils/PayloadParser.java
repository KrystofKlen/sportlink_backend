package com.sportlink.sportlink.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class PayloadParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Method to parse JSON string to Java object
    public static <T> T parseJsonToObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            System.err.println("Error parsing JSON to object: " + e.getMessage());
            return null;
        }
    }

    public static Map<String, Object> parseJsonToMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            System.err.println("Error parsing JSON to Map: " + e.getMessage());
            return Collections.emptyMap();
        }
    }


    // Method to serialize Java object to JSON string
    public static String parseObjectToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing object to JSON: " + e.getMessage());
            return null;
        }
    }
}
