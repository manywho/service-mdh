package com.boomi.flow.services.boomi.mdh.client;

import com.boomi.flow.services.boomi.mdh.match.MatchEntityResponse;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineQueryRequest;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineQueryResponse;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordQueryRequest;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordQueryResponse;
import com.boomi.flow.services.boomi.mdh.common.BatchUpdateRequest;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.boomi.flow.services.boomi.mdh.universes.UniversesResponse;
import com.manywho.sdk.api.run.ServiceProblemException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.StringReader;
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
            LOGGER.error("Unable to fetch the a list of universes", e);

            throw new ServiceProblemException(500, "Unable to fetch the list of universes due to an unexpected error");
        }

        var body = response.body();
        if (body == null) {
            throw new ServiceProblemException(500, "No response body was given when fetching the list of universes");
        }

        try {
            var result = JAXB.unmarshal(body.byteStream(), UniversesResponse.class);
            if (result == null) {
                return new ArrayList<>();
            }

            return result.getUniverses();
        } catch (RuntimeException e) {
            LOGGER.error("Unable to deserialize a list of universes", e);

            throw new ServiceProblemException(500, "Unable to deserialize the list of universes due to an unexpected error");
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
            LOGGER.error("Unable to fetch a universe", e);

            throw new ServiceProblemException(500, "Unable to fetch the universe " + id + " due to an unexpected error");
        }

        var body = response.body();
        if (body == null) {
            throw new ServiceProblemException(500, "No response body was given when fetching the universe " + id);
        }

        if (response.isSuccessful()) {
            try {
                return JAXB.unmarshal(body.byteStream(), Universe.class);
            } catch (RuntimeException e) {
                LOGGER.error("Unable to deserialize a universe", e);

                throw new ServiceProblemException(500, "Unable to deserialize the universe " + id + " due to an unexpected error");
            }
        }

        if (response.code() == 400) {
            var error = JAXB.unmarshal(body.byteStream(), MdhError.class);
            if (error != null) {
                throw new ServiceProblemException(400, error.getMessage());
            }
        }

        if (response.code() == 404) {
            throw new ServiceProblemException(404, "No universe could be found with the ID " + id);
        }

        throw new ServiceProblemException(500, "An unexpected error occurred while finding a universe");
    }

    public QuarantineQueryResponse queryQuarantineEntries(String hostname, String username, String password, String universe, QuarantineQueryRequest query) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(hostname)
                .addPathSegments("mdm/universes")
                .addPathSegment(universe)
                .addPathSegments("quarantine/query")
                .build();

        return sendRequestExpectingResponse(username, password, url, query, QuarantineQueryResponse.class, "quarantine entry");
    }

    public void updateGoldenRecords(String hostname, String username, String password, String universe, BatchUpdateRequest request) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(hostname)
                .addPathSegments("mdm/universes")
                .addPathSegment(universe)
                .addPathSegment("records")
                .build();

        var response = sendRequest(username, password, url, request, "golden record");

        if (response.isSuccessful()) {
            return;
        }

        var body = response.body();
        if (body == null) {
            throw new ServiceProblemException(response.code(), "An unknown error occurred while updating golden records");
        }

        var error = JAXB.unmarshal(body.byteStream(), MdhError.class);
        if (error != null) {
            throw new ServiceProblemException(response.code(), error.getMessage());
        }

        throw new ServiceProblemException(response.code(), "An unknown error occurred while updating golden records");
    }

    public GoldenRecordQueryResponse queryGoldenRecords(String hostname, String username, String password, String universe, GoldenRecordQueryRequest query) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(hostname)
                .addPathSegments("mdm/universes")
                .addPathSegment(universe)
                .addPathSegments("records/query")
                .build();

        return sendRequestExpectingResponse(username, password, url, query, GoldenRecordQueryResponse.class, "golden record");
    }

    public MatchEntityResponse  queryMatchEntity(String hostname, String username, String password, String universe, BatchUpdateRequest query) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(hostname)
                .addPathSegments("mdm/universes")
                .addPathSegments(universe)
                .addPathSegments("match")
                .build();
        return sendRequestExpectingResponse(username, password, url, query, MatchEntityResponse.class, "match");
    }

    private Response sendRequest(String username, String password, HttpUrl url, Object query, String type) {
        var bodyContent = new StringWriter();

        try {
            JAXB.marshal(query, bodyContent);
        } catch (RuntimeException e) {
            LOGGER.error("Unable to serialize the request", e);

            throw new ServiceProblemException(500, "An unexpected error occurred while creating the request");
        }

        var body = RequestBody.create(XML, bodyContent.toString());

        Request request = new Request.Builder()
                .addHeader("Authorization", Credentials.basic(username, password))
                .url(url)
                .post(body)
                .build();

        try {
            return httpClient
                    .newCall(request)
                    .execute();
        } catch (IOException e) {
            LOGGER.error("Unable to query for {}", type, e);

            throw new ServiceProblemException(500, "Unable to query for " + type + ": " + e.getMessage());
        }
    }

    private <T> T sendRequestExpectingResponse(String username, String password, HttpUrl url, Object query, Class<T> aClass, String type) {
        var response = sendRequest(username, password, url, query, type);
        if (response.body() == null) {
            throw new ServiceProblemException(500, "No response body was given while querying for " + type + " objects");
        }

        String responseBody;
        try {
            responseBody = response.body().string();
        } catch (IOException e) {
            LOGGER.error("Unable to fetch the response body", e);

            throw new ServiceProblemException(500, "Unable to fetch the response body due to an unexpected error");
        }

        if (response.isSuccessful() == false) {
            LOGGER.error("Unable to query {}: {}", type, responseBody);

            throw new ServiceProblemException(response.code(), "Something went wrong loading the " + type + " objects: " + responseBody);
        }

        try {
            return JAXB.unmarshal(new StringReader(responseBody), aClass);
        } catch (RuntimeException e) {
            LOGGER.error("Unable to deserialize the response", e);

            throw new ServiceProblemException(500, "Unable to deserialize the " + type + " query response");
        }
    }
}
