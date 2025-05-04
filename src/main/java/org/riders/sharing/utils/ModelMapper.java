package org.riders.sharing.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.MappingException;

import java.io.IOException;
import java.io.InputStream;

public final class ModelMapper {
    private static final Logger LOGGER;

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper(new YAMLFactory()).registerModules(new JavaTimeModule());
        LOGGER = LogManager.getLogger(ModelMapper.class);
    }

    private ModelMapper() {
    }

    public static String toJsonString(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.error("Couldn't map object to JSON", e);
            throw new MappingException("Couldn't map object to JSON", e);
        }
    }

    public static <T> T parse(String json, Class<T> valueType) {
        try {
            return OBJECT_MAPPER.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            LOGGER.error("Couldn't map JSON to Object", e);
            throw new MappingException("Couldn't map JSON to Object", e);
        }
    }

    public static <T> T parse(String value, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(value, typeReference);
        } catch (JsonProcessingException e) {
            LOGGER.error("Couldn't map JSON to Object", e);
            throw new MappingException("Couldn't map JSON to Object", e);
        }
    }

    public static <T> T parse(InputStream inputStream, Class<T> valueType) {
        try {
            return OBJECT_MAPPER.readValue(inputStream, valueType);
        } catch (IOException e) {
            LOGGER.error("Couldn't map JSON to Object", e);
            throw new MappingException("Couldn't map JSON to Object", e);
        }
    }
}
