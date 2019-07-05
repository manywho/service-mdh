package com.boomi.flow.services.boomi.mdh.database;

public class TypeNameGenerator {
    static String createModelName(String universeName) {
        return String.format("%s Model", universeName);
    }

    static String createChildTypeName(String fieldName, String parentName) {
        return String.format("%s - %s", parentName, fieldName);
    }

    static String createQuarentineEntityName(String universeName) {
        return String.format("%s Quarantine Entry", universeName);
    }

    static String createGoldenRecordName(String universeName) {
        return String.format("%s Golden Record", universeName);
    }
}
