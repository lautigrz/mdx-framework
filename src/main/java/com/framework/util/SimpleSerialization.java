package com.framework.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;

public class SimpleSerialization {

    private final ObjectMapper mapper;

    public SimpleSerialization() {
        this.mapper = new ObjectMapper();

    }

    public String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }


    public <T> T fromJson(String json, Type type) throws Exception {

        try {
            JavaType javaType = mapper.getTypeFactory().constructType(type);

            return mapper.readValue(json, javaType);

        }catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JSON to object", e);
        }

    }
}
