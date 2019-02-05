package com.boomi.flow.services.boomi.mdh.client;

import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineQueryRequest;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineQueryResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;

public class MdhClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MdhClient.class);
    private static final MediaType XML = MediaType.parse("application/xml; charset=utf-8");

    private final OkHttpClient httpClient;
    private final XmlMapper xmlMapper;

    @Inject
    public MdhClient(OkHttpClient httpClient, XmlMapper xmlMapper) {
        this.httpClient = httpClient;
        this.xmlMapper = xmlMapper;
    }

    public QuarantineQueryResponse queryQuarantineEntries(String hostname, String username, String password, String universe, QuarantineQueryRequest query) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(hostname)
                .addPathSegments("mdm/universes")
                .addPathSegment(universe)
                .addPathSegments("quarantine/query")
                .build();

        RequestBody body;
        try {
            String t = xmlMapper.writeValueAsString(query);
            body = RequestBody.create(XML, t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Something went wrong creating the query request", e);
        }

        Request request = new Request.Builder()
                .addHeader("Authorization", Credentials.basic(username, password))
                .url(url)
                .post(body)
                .build();

        Response response;
        try {
            response = httpClient
                    .newCall(request)
                    .execute();
        } catch (IOException e) {
            LOGGER.error("Unable to query for quarantine entries", e);

            throw new RuntimeException("Unable to query for quarantine entries: " + e.getMessage(), e);
        }

        if (response.body() == null) {
            throw new RuntimeException("No response body was given while querying for quarantine entries");
        }

        String responseBody;
        try {
            responseBody = response.body().string();
        } catch (IOException e) {
            LOGGER.error("Unable to fetch the response body", e);

            throw new RuntimeException("Unable to fetch the response body", e);
        }

        if (response.isSuccessful() == false) {
            LOGGER.error("Unable to query quarantine entries: {}", responseBody);

            throw new RuntimeException("Something went wrong loading the quarantine entries: " + responseBody);
        }

        try {
            return xmlMapper.readValue(responseBody, QuarantineQueryResponse.class);
        } catch (IOException e) {
            LOGGER.error("Unable to deserialize the response", e);

            throw new RuntimeException("Unable to deserialize the quarantine entry query response", e);
        }
    }
}
