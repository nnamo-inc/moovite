package com.nnamo.utils;

import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

public class UserDataUtils {
    private final static String dataDir = AppDirsFactory
            .getInstance()
            .getUserDataDir("moovite", null, "nnamo");

    private final static String sessionPath = dataDir + "/session.txt";
    private final static String mapCachePath = dataDir + "/.cache";

    public static String getDataDir() {
        return dataDir;
    }

    public static String getSessionPath() {
        return sessionPath;
    }

    public static String getMapCachePath() {
        return mapCachePath;
    }
}
