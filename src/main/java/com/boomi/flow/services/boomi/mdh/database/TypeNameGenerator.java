package com.boomi.flow.services.boomi.mdh.database;

public class TypeNameGenerator {
    public static String createModelName(String universeName) {
        return String.format("%s", universeName);
    }

    static String createChildTypeName(String fieldName, String parentName) {
        //return String.format("%s - %s", parentName, fieldName);
        return String.format("%s", fieldName);
    }

    static String createQuarentineEntityName(String universeName) {
        return String.format("%s Quarantine Entry", universeName);
    }

    public static String createGoldenRecordName(String universeName) {
        return String.format("%s Golden Record", universeName);
    }
}
