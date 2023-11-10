package com.kyee.upgrade.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class CommonUtils {

    public CommonUtils() {
    }

    public static class JsonUtil {
        private static Gson gson;

        public JsonUtil() {
        }

        public static Gson getGson() {
            return gson;
        }

        public static Map<String, Object> json2Map(String jsonSrc) {
            Map<String, Object> rs = (Map)gson.fromJson(jsonSrc, Map.class);
            return rs;
        }

        public static Map<String, Object> json2Map(Reader reader) {
            Map<String, Object> rs = (Map)gson.fromJson(reader, Map.class);
            return rs;
        }

        public static List json2List(String jsonSrc) {
            return (List)gson.fromJson(jsonSrc, List.class);
        }

        public static List json2List(Reader reader) {
            return (List)gson.fromJson(reader, List.class);
        }

        public static <T> T json2AnyType(Reader reader, Type type) {
            return gson.fromJson(reader, type);
        }

        public static <T> T json2AnyType(String jsonSrc, Type type) {
            return gson.fromJson(jsonSrc, type);
        }

        public static <T> T json2Object(String jsonSrc, Class<T> type) {
            return gson.fromJson(jsonSrc, type);
        }

        public static <T> T json2Object(Reader reader, Class<T> type) {
            return gson.fromJson(reader, type);
        }

        public static String object2Json(Object obj) {
            return gson.toJson(obj);
        }

        public static boolean validate(String jsonSrc) {
            try {
                gson.fromJson(jsonSrc, Object.class);
                return true;
            } catch (JsonSyntaxException var2) {
                return false;
            }
        }

        static {
            GsonBuilder builder = new GsonBuilder();
            builder.setDateFormat("yyyy/MM/dd HH:mm:ss");
            gson = builder.serializeNulls().create();
        }
    }
}
