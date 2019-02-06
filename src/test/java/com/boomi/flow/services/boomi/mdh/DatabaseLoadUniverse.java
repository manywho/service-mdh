package com.boomi.flow.services.boomi.mdh;

import com.google.inject.AbstractModule;
import com.manywho.sdk.services.servers.EmbeddedServer;
import com.manywho.sdk.services.servers.undertow.UndertowServer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import okhttp3.OkHttpClient;
import okhttp3.mock.MockInterceptor;
import okhttp3.mock.Rule;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

public class DatabaseLoadUniverse {
    private EmbeddedServer server = new UndertowServer();

    private MockInterceptor interceptor = new MockInterceptor();

    @Before
    public void before() throws Exception {
        RestAssured.port = 10001;

        server.addModule(new ApplicationModule());
        server.addModule(new AbstractModule() {
            @Override
            protected void configure() {
                bind(OkHttpClient.class).toInstance(new OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                        .build());
            }
        });
        server.setApplication(Application.class);
        server.start("/", 10001);
    }

    @After
    public void after() {
        server.stop();
    }

    @Test
    public void testLoadAllUniverses() {
        interceptor.addRule(new Rule.Builder()
                .respond(getClass().getClassLoader().getResourceAsStream("mocks/universes.xml")));

        var request = new JSONObject()
                .put("configurationValues", new JSONArray()
                        .put(new JSONObject()
                                .put("developerName", "Atom Hostname")
                                .put("contentValue", "atom.example.com")
                        )
                        .put(new JSONObject()
                                .put("developerName", "Atom Username")
                                .put("contentValue", "username")
                        )
                        .put(new JSONObject()
                                .put("developerName", "Atom Password")
                                .put("contentValue", "password")
                        )
                )
                .put("objectDataType", new JSONObject()
                        .put("developerName", "Universe")
                );

        given()
                .contentType(ContentType.JSON)
                .body(request.toString())
                .when()
                .post("/data")
                .then()
                .assertThat()
                .statusCode(200)
                .body("hasMoreResults", equalTo(false))
                .body("objectData", hasSize(2))
                .body("objectData[0].developerName", equalTo("Universe"))
                .body("objectData[0].externalId", equalTo("2df80af3-1035-4732-ba12-6c84b0d9f568"))
                .body("objectData[0].properties", hasSize(3))
                .body("objectData[0].properties[0].contentType", equalTo("ContentString"))
                .body("objectData[0].properties[0].contentValue", equalTo("2df80af3-1035-4732-ba12-6c84b0d9f568"))
                .body("objectData[0].properties[0].developerName", equalTo("ID"))
                .body("objectData[0].properties[0].objectData", hasSize(0))
                .body("objectData[0].properties[1].contentType", equalTo("ContentString"))
                .body("objectData[0].properties[1].contentValue", equalTo("account"))
                .body("objectData[0].properties[1].developerName", equalTo("Name"))
                .body("objectData[0].properties[1].objectData", hasSize(0))
                .body("objectData[0].properties[2].contentType", equalTo("ContentString"))
                .body("objectData[0].properties[2].contentValue", equalTo("021a2910-cdc4-441d-83c1-ee9c5cc7f257"))
                .body("objectData[0].properties[2].developerName", equalTo("Version"))
                .body("objectData[0].properties[2].objectData", hasSize(0))

                .body("objectData[1].developerName", equalTo("Universe"))
                .body("objectData[1].externalId", equalTo("12fa46f9-e14d-4042-878e-30b273b61731"))
                .body("objectData[1].properties", hasSize(3))
                .body("objectData[1].properties[0].contentType", equalTo("ContentString"))
                .body("objectData[1].properties[0].contentValue", equalTo("12fa46f9-e14d-4042-878e-30b273b61731"))
                .body("objectData[1].properties[0].developerName", equalTo("ID"))
                .body("objectData[1].properties[0].objectData", hasSize(0))
                .body("objectData[1].properties[1].contentType", equalTo("ContentString"))
                .body("objectData[1].properties[1].contentValue", equalTo("person"))
                .body("objectData[1].properties[1].developerName", equalTo("Name"))
                .body("objectData[1].properties[1].objectData", hasSize(0))
                .body("objectData[1].properties[2].contentType", equalTo("ContentString"))
                .body("objectData[1].properties[2].contentValue", equalTo("9196c0f1-cf26-4768-91a1-9291ca04630b"))
                .body("objectData[1].properties[2].developerName", equalTo("Version"))
                .body("objectData[1].properties[2].objectData", hasSize(0));
    }
}
