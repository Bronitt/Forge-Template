package com.example.modid.util;

import com.example.modid.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getSimpleName());
    }

    public static Logger getLogger(Class<?> clazz, String name) {
        return getLogger(clazz.getSimpleName(), name);
    }

    public static Logger getLogger(Class<?> clazz, Class<?> clazz2) {
        return getLogger(clazz.getSimpleName(), clazz2.getSimpleName());
    }

    public static Logger getLogger(String name, String name2) {
        return getLogger(name + "/" +  name2);
    }

    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(Reference.MOD_NAME + "/" + name);
    }

    public static Logger getModLogger() {
        return LoggerFactory.getLogger(Reference.MOD_NAME);
    }


}
