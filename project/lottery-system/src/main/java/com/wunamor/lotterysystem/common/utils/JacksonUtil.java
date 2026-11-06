package com.wunamor.lotterysystem.common.utils;

import org.springframework.boot.json.JsonParseException;
import org.springframework.util.ReflectionUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.Callable;

public class JacksonUtil {
    private JacksonUtil() {}

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
    }

    public static ObjectMapper getObjectMapper() { // 如果是懒汉模式，需要考虑到线程安全问题
        return OBJECT_MAPPER;
    }

    private static <T> T tryParse(Callable<T> parser, Class<? extends Exception> check) {
        try {
            return parser.call();
        }
        catch (Exception ex) {
            if (check.isInstance(ex)) {
                throw new JsonParseException(ex);
            }
            throw new IllegalStateException(ex);
        }
    }

    private static <T> T tryParse(Callable<T> parser) {
        return tryParse(parser, JacksonException.class);
    }

    public static String writeObjectAsString(Object object) {
        return tryParse(() -> {
            return getObjectMapper().writeValueAsString(object);
        });
    }

    public static <T> T readValue(String json, Class<T> type) {
        return tryParse(() -> {
            return getObjectMapper().readValue(json, type);
        });
    }

    public static <T> T readListValue(String json, Class<T> type) {
        JavaType javaType = getObjectMapper().getTypeFactory().constructParametricType(List.class, type);
        return tryParse(() -> {
            return getObjectMapper().readValue(json, javaType);
        });
    }
}
