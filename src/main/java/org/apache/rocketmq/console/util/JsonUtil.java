package org.apache.rocketmq.console.util;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Created by tangjie
 * 2016/11/21
 * styletang.me@gmail.com
 */
@SuppressWarnings("unchecked")
public class JsonUtil {

    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    private JsonUtil() {
    }

    static {
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationConfig.Feature.WRITE_ENUMS_USING_TO_STRING, true);
        objectMapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public static void writeValue(Writer writer, Object obj) {
        try {
            objectMapper.writeValue(writer, obj);
        } catch (IOException e) {
            Throwables.propagateIfPossible(e);
        }
    }

    /**
     * Object => String
     *
     * @param src
     * @return
     */
    public static <T> String obj2String(T src) {
        if (src == null) {
            return null;
        }

        try {
            return src instanceof String ? (String) src : objectMapper.writeValueAsString(src);
        } catch (Exception e) {
            logger.error("Parse Object to String error src="+src, e);
            return null;
        }
    }


    /**
     * Object => byte[]
     *
     * @param src
     * @return
     */
    public static <T> byte[] obj2Byte(T src) {
        if (src == null) {
            return null;
        }

        try {
            return src instanceof byte[] ? (byte[]) src : objectMapper.writeValueAsBytes(src);
        } catch (Exception e) {
            logger.error("Parse Object to byte[] error", e);
            return null;
        }
    }

    /**
     * String => Object
     *
     * @param str
     * @param clazz
     * @return
     */
    public static <T> T string2Obj(String str, Class<T> clazz) {
        if (Strings.isNullOrEmpty(str) || clazz == null) {
            return null;
        }
        str = escapesSpecialChar(str);
        try {
            return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
        } catch (Exception e) {
            logger.error("Parse String to Object error\nString: {}\nClass<T>: {}\nError: {}", str, clazz.getName(), e);
            return null;
        }
    }

    /**
     * byte[] => Object
     *
     * @param bytes
     * @param clazz
     * @return
     */
    public static <T> T byte2Obj(byte[] bytes, Class<T> clazz) {
        if (bytes == null || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(byte[].class) ? (T) bytes : objectMapper.readValue(bytes, clazz);
        } catch (Exception e) {
            logger.error("Parse byte[] to Object error\nbyte[]: {}\nClass<T>: {}\nError: {}", bytes, clazz.getName(), e);
            return null;
        }
    }

    /**
     * String => Object
     *
     * @param str
     * @param typeReference
     * @return
     */
    public static <T> T string2Obj(String str, TypeReference<T> typeReference) {
        if (Strings.isNullOrEmpty(str)|| typeReference == null) {
            return null;
        }
        str = escapesSpecialChar(str);
        try {
            return (T) (typeReference.getType().equals(String.class) ? str : objectMapper.readValue(str, typeReference));
        } catch (Exception e) {
            logger.error("Parse String to Object error\nString: {}\nTypeReference<T>: {}\nError: {}", str,
                    typeReference.getType(), e);
            return null;
        }
    }

    /**
     * byte[] => Object
     *
     * @param bytes
     * @param typeReference
     * @return
     */
    public static <T> T byte2Obj(byte[] bytes, TypeReference<T> typeReference) {
        if (bytes == null || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(byte[].class) ? bytes : objectMapper.readValue(bytes,
                    typeReference));
        } catch (Exception e) {
            logger.error("Parse byte[] to Object error\nbyte[]: {}\nTypeReference<T>: {}\nError: {}", bytes,
                    typeReference.getType(), e);
            return null;
        }
    }

    /**
     *
     * @param map
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T map2Obj(Map<String,String> map, Class<T> clazz) {
        String str = obj2String(map);
        return string2Obj(str,clazz);
    }



    /**
     * Escapes Special Character
     *
     * @param str
     * @return
     */
    private static String escapesSpecialChar(String str) {
        return str.replace("\n", "\\n").replace("\r", "\\r");
    }
}
