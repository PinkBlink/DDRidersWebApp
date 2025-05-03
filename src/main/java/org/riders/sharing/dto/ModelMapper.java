package org.riders.sharing.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.ModelMapperException;

import java.io.IOException;
import java.io.InputStream;

public enum ModelMapper {
    INSTANCE;

    private static final Logger logger = LogManager.getLogger(ModelMapper.class);

    private ObjectMapper objectMapper;

    ModelMapper() {
        init();
    }


    private void init() {
        objectMapper = new ObjectMapper(new YAMLFactory()).registerModules(new JavaTimeModule());
    }

    public String getAsJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Couldn't map object to JSON", e);
            throw new ModelMapperException("Couldn't map object to JSON", e);
        }
    }

    public <T> T getAsObject(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            logger.error("Couldn't map JSON to Object", e);
            throw new ModelMapperException("Couldn't map JSON to Object", e);
        }
    }

    public <T> T getAsObject(String value, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(value, typeReference);
        } catch (JsonProcessingException e) {
            logger.error("Couldn't map JSON to Object", e);
            throw new ModelMapperException("Couldn't map JSON to Object", e);
        }
    }

    public <T> T getAsObject(InputStream inputStream, Class<T> valueType) {
        try {
            return objectMapper.readValue(inputStream, valueType);
        } catch (IOException e) {
            logger.error("Couldn't map JSON to Object", e);
            throw new ModelMapperException("Couldn't map JSON to Object", e);
        }
    }
}
