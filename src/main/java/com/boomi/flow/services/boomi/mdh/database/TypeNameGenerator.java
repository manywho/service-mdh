package com.boomi.flow.services.boomi.mdh.database;

public class TypeNameGenerator {
    public static String createModelName(String universeName) {
        return String.format("%s", universeName);
    }
}
