package com.brajagopal.rmend.utils;

import com.google.gson.JsonObject;

/**
 * @author <bxr4261>
 */
public class SerDeUtils {

    public static String getValue(JsonObject jsonObject, String key, String defaultValue) {

        if (jsonObject.has(key)) {
            return jsonObject.get(key).getAsString();
        }
        else {
            return defaultValue;
        }
    }

    public static Long getValue(JsonObject jsonObject, String key, Long defaultValue) {

        if (jsonObject.has(key)) {
            return jsonObject.get(key).getAsLong();
        }
        else {
            return defaultValue;
        }
    }

    public static Double getValue(JsonObject jsonObject, String key, Double defaultValue) {

        if (jsonObject.has(key)) {
            return jsonObject.get(key).getAsDouble();
        }
        else {
            return defaultValue;
        }
    }

    public static Integer getValue(JsonObject jsonObject, String key, Integer defaultValue) {

        if (jsonObject.has(key)) {
            return jsonObject.get(key).getAsInt();
        }
        else {
            return defaultValue;
        }
    }
}
