package com.boomi.flow.services.boomi.mdh.universes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

public class UniverseRepository {
    private final static Logger LOGGER = LoggerFactory.getLogger(UniverseRepository.class);

    private final OkHttpClient httpClient;
    private final XmlMapper xmlMapper;

    @Inject
    public UniverseRepository(OkHttpClient httpClient, XmlMapper xmlMapper) {
        this.httpClient = httpClient;
        this.xmlMapper = xmlMapper;
    }

    public List<Universe> findAll(String atomHostname, String atomUsername, String atomPassword) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(atomHostname)
                .addPathSegments("mdm/universes")
                .build();

        Request request = new Request.Builder()
                .addHeader("Authorization", Credentials.basic(atomUsername, atomPassword))
                .url(url)
                .build();

        Response response;
        try {
            response = httpClient.newCall(request)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException("Unable to fetch the list of universes", e);
        }

        if (response.body() == null) {
            throw new RuntimeException("No response body was given when fetching the list of universes");
        }

        try {
            List<Universe> universes = xmlMapper.readValue(response.body().byteStream(), new TypeReference<List<Universe>>() {});

            return universes;
        } catch (IOException e) {
            throw new RuntimeException("Unable to deserialize the list of universes", e);
        }
    }
}
