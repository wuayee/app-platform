/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package modelengine.fit.waterflow.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;

import modelengine.fitframework.util.ObjectUtils;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提供Map中的byte数组序列化和反序列化能力
 *
 * @author 夏斐
 * @since 2024/2/2
 */
public class ByteArraySerialiseUtilV1 {
    private static final SerializeConfig SERIALIZE_CONFIG = new SerializeConfig();

    private static final String PREFIX = "BYTE_BASE64:";

    static {
        SERIALIZE_CONFIG.put(byte[].class, new ByteArrayToHexStringSerializer());
    }

    /**
     * getSerializeConfig
     *
     * @return SerializeConfig
     */
    public static SerializeConfig getSerializeConfig() {
        return SERIALIZE_CONFIG;
    }

    /**
     * getMapParserConfig
     *
     * @return ParserConfig
     */
    public static ParserConfig getMapParserConfig() {
        ParserConfig parserConfig = ParserConfig.getGlobalInstance();
        parserConfig.putDeserializer(Map.class, new CustomMapDeserializer());
        parserConfig.putDeserializer(JSONObject.class, new CustomMapDeserializer());
        return parserConfig;
    }

    /**
     * deserializeBytesValue
     *
     * @param jsonObject jsonObject
     * @return Object
     */
    public static Object deserializeBytesValue(Object jsonObject) {
        return tryDeserializeBytesValue(jsonObject);
    }

    /**
     * tryDeserializeBytesValue
     *
     * @param map map
     * @return Object
     */
    public static Object tryDeserializeBytesValue(Map<String, Object> map) {
        map.entrySet().stream().forEach(entry -> {
            Object src = entry.getValue();
            Object dst = tryDeserializeBytesValue(src);
            if (!(dst == src)) {
                entry.setValue(dst);
            }
        });
        return map;
    }

    /**
     * tryDeserializeBytesValue
     *
     * @param list list
     * @return Object
     */
    public static Object tryDeserializeBytesValue(List<Object> list) {
        for (int i = 0; i < list.size(); ++i) {
            Object src = list.get(i);
            Object dst = ByteArraySerialiseUtilV1.tryDeserializeBytesValue(src);
            if (!(dst == src)) {
                list.set(i, dst);
            }
        }
        return list;
    }

    /**
     * tryDeserializeBytesValue
     *
     * @param value value
     * @return Object
     */
    public static Object tryDeserializeBytesValue(String value) {
        if (value.startsWith(PREFIX)) {
            String base64 = value.substring(PREFIX.length());
            return Base64.getDecoder().decode(base64);
        }
        return value;
    }

    private static Object tryDeserializeBytesValue(Object object) {
        if (object instanceof String) {
            return tryDeserializeBytesValue((String) object);
        } else if (object instanceof List) {
            return tryDeserializeBytesValue((List) object);
        } else if (object instanceof Map) {
            return tryDeserializeBytesValue((Map) object);
        } else {
            return object;
        }
    }

    private static class ByteArrayToHexStringSerializer implements ObjectSerializer {
        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
            byte[] bytes = (byte[]) object;
            SerializeWriter out = serializer.out;

            if (bytes == null) {
                out.writeNull();
                return;
            }

            String hexStr = Base64.getEncoder().encodeToString(bytes);
            out.writeString(PREFIX + hexStr);
        }
    }

    /**
     * 自定义Map反序列化。注意：这里只能拦截到顶层的反序列化，即：JSONObject.parseObject(str, Map/JSONObject.class, parserConfig)
     *
     * @author 宋永坦
     * @since 2024/2/26
     */
    private static class CustomMapDeserializer implements ObjectDeserializer {
        @Override
        public JSONObject deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            Map<String, Object> map = new HashMap<>();
            parser.parseObject(map);
            return new JSONObject(ObjectUtils.cast(ByteArraySerialiseUtilV1.deserializeBytesValue(map)));
        }

        @Override
        public int getFastMatchToken() {
            return 0;
        }
    }
}
