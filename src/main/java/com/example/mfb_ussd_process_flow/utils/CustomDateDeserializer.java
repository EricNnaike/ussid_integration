package com.example.mfb_ussd_process_flow.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class CustomDateDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateValue = p.getValueAsString();
        // Remove leading and trailing whitespaces
        return dateValue != null ? dateValue.trim() : null;
    }
}
