package com.boomi.flow.services.boomi.mdh.utilities;

import com.manywho.sdk.api.run.elements.type.ListFilter;

import java.util.Base64;

public class OffsetTokenGenerator {
    public static String generate(ListFilter listFilter) {
        if (listFilter != null && listFilter.getOffset() != null) {
            return Base64.getEncoder().encodeToString(listFilter.getOffset().toString().getBytes());
        }

        return null;
    }
}
