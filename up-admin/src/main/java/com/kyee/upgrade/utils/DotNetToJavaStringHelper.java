package com.kyee.upgrade.utils;

public class DotNetToJavaStringHelper {
    public static boolean isNullOrEmpty(String string)
    {
        return string == null || string.equals("") || string.equalsIgnoreCase("null");
    }
}
