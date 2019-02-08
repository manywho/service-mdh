package com.boomi.flow.services.boomi.mdh.client;

import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineQueryRequest;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineQueryResponse;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordQueryRequest;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordQueryResponse;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.boomi.flow.services.boomi.mdh.universes.UniversesResponse;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class MdhClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MdhClient.class);
    private static final MediaType XML = MediaType.parse("application/xml; charset=utf-8");

    private final OkHttpClient httpClient;

    @Inject
    public MdhClient(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public List<Universe> findAllUniverses(String hostname, String username, String password) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(hostname)
                .addPathSegments("mdm/universes")
                .build();

        Request request = new Request.Builder()
                .addHeader("Authorization", Credentials.basic(username, password))
                .url(url)
                .build();

        Response response;
        try {
            response = httpClient.newCall(request)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException("Unable to fetch the list of universes", e);
        }

        var body = response.body();
        if (body == null) {
            throw new RuntimeException("No response body was given when fetching the list of universes");
        }

        try {
            var result = JAXB.unmarshal(body.byteStream(), UniversesResponse.class);
            if (result == null) {
                return new ArrayList<>();
            }

            return result.getUniverses();
        } catch (RuntimeException e) {
            throw new RuntimeException("Unable to deserialize the list of universes", e);
        }
    }

    public Universe findUniverse(String hostname, String username, String password, String id) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(hostname)
                .addPathSegments("mdm/universes")
                .addPathSegment(id)
                .build();

        Request request = new Request.Builder()
                .addHeader("Authorization", Credentials.basic(username, password))
                .url(url)
                .build();

        Response response;
        try {
            response = httpClient.newCall(request)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException("Unable to fetch the universe " + id, e);
        }

        var body = response.body();
        if (body == null) {
            throw new RuntimeException("No response body was given when fetching the universe " + id);
        }

        try {
            return JAXB.unmarshal(body.byteStream(), Universe.class);
        } catch (RuntimeException e) {
            throw new RuntimeException("Unable to deserialize the universes " + id, e);
        }
    }

    public QuarantineQueryResponse queryQuarantineEntries(String hostname, String username, String password, String universe, QuarantineQueryRequest query) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(hostname)
                .addPathSegments("mdm/universes")
                .addPathSegment(universe)
                .addPathSegments("quarantine/query")
                .build();

        return sendRequest(username, password, url, query, QuarantineQueryResponse.class, "quarantine entry");
    }

    public GoldenRecordQueryResponse queryGoldenRecords(String hostname, String username, String password, String universe, GoldenRecordQueryRequest query) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(hostname)
                .addPathSegments("mdm/universes")
                .addPathSegment(universe)
                .addPathSegments("records/query")
                .build();

        return sendRequest(username, password, url, query, GoldenRecordQueryResponse.class, "golden record");
    }

    private <T> T sendRequest(String username, String password, HttpUrl url, Object query, Class<T> aClass, String type) {
        var bodyContent = new StringWriter();

        try {
            JAXB.marshal(query, bodyContent);
        } catch (RuntimeException e) {
            throw new RuntimeException("Something went wrong creating the request", e);
        }

        var body = RequestBody.create(XML, bodyContent.toString());

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
            LOGGER.error("Unable to query for {}", type, e);

            throw new RuntimeException("Unable to query for " + type + ": " + e.getMessage(), e);
        }

        if (response.body() == null) {
            throw new RuntimeException("No response body was given while querying for " + type + " objects");
        }

        String responseBody;
        try {
            responseBody = response.body().string();
        } catch (IOException e) {
            LOGGER.error("Unable to fetch the response body", e);

            throw new RuntimeException("Unable to fetch the response body", e);
        }

        if (response.isSuccessful() == false) {
            LOGGER.error("Unable to query {}: {}", type, responseBody);

            throw new RuntimeException("Something went wrong loading the " + type + " objects: " + responseBody);
        }

        try {
            return JAXB.unmarshal(responseBody, aClass);
        } catch (RuntimeException e) {
            LOGGER.error("Unable to deserialize the response", e);

            throw new RuntimeException("Unable to deserialize the " + type + " query response", e);
        }
    }
}
