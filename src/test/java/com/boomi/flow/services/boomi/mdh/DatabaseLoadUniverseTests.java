package com.boomi.flow.services.boomi.mdh;

import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.manywho.sdk.services.servers.EmbeddedServer;
import com.manywho.sdk.services.servers.undertow.UndertowServer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.mock.MockInterceptor;
import okhttp3.mock.Rule;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

public class DatabaseLoadUniverseTests {
    private EmbeddedServer server = new UndertowServer();

    private MockInterceptor interceptor = new MockInterceptor();

    private MediaType mediaType = MediaType.parse("application/json");

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
                                .put("developerName", "Hub Hostname")
                                .put("contentValue", "atom.example.com")
                        )
                        .put(new JSONObject()
                                .put("developerName", "Hub Username")
                                .put("contentValue", "username")
                        )
                        .put(new JSONObject()
                                .put("developerName", "Hub Token")
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
                .body("culture", nullValue())
                .body("hasMoreResults", equalTo(false))
                .body("objectData", hasSize(2))
                .body("objectData[0].developerName", equalTo("Universe"))
                .body("objectData[0].externalId", equalTo("04ea04cc-9df8-4004-bc00-a21b7dede1bb"))
                .body("objectData[0].internalId", nullValue())
                .body("objectData[0].isSelected", equalTo(false))
                .body("objectData[0].order", equalTo(0))
                .body("objectData[0].properties", hasSize(3))
                .body("objectData[0].properties[0].contentFormat", nullValue())
                .body("objectData[0].properties[0].contentType", equalTo("ContentString"))
                .body("objectData[0].properties[0].contentValue", equalTo("04ea04cc-9df8-4004-bc00-a21b7dede1bb"))
                .body("objectData[0].properties[0].developerName", equalTo("ID"))
                .body("objectData[0].properties[0].objectData", hasSize(0))
                .body("objectData[0].properties[0].typeElementPropertyId", nullValue())
                .body("objectData[0].properties[1].contentFormat", nullValue())
                .body("objectData[0].properties[1].contentType", equalTo("ContentString"))
                .body("objectData[0].properties[1].contentValue", equalTo("accounts"))
                .body("objectData[0].properties[1].developerName", equalTo("Name"))
                .body("objectData[0].properties[1].objectData", hasSize(0))
                .body("objectData[0].properties[1].typeElementPropertyId", nullValue())
                .body("objectData[0].properties[2].contentFormat", nullValue())
                .body("objectData[0].properties[2].contentType", equalTo("ContentString"))
                .body("objectData[0].properties[2].contentValue", equalTo("bb072194-2279-42b3-84c1-2f1b2a38c71e"))
                .body("objectData[0].properties[2].developerName", equalTo("Version"))
                .body("objectData[0].properties[2].objectData", hasSize(0))
                .body("objectData[0].properties[2].typeElementPropertyId", nullValue())
                .body("objectData[0].typeElementId", nullValue())
                .body("objectData[1].developerName", equalTo("Universe"))
                .body("objectData[1].externalId", equalTo("1d969a54-49e5-4f75-974a-85869280873d"))
                .body("objectData[1].internalId", nullValue())
                .body("objectData[1].isSelected", equalTo(false))
                .body("objectData[1].order", equalTo(0))
                .body("objectData[1].properties", hasSize(3))
                .body("objectData[1].properties[0].contentFormat", nullValue())
                .body("objectData[1].properties[0].contentType", equalTo("ContentString"))
                .body("objectData[1].properties[0].contentValue", equalTo("1d969a54-49e5-4f75-974a-85869280873d"))
                .body("objectData[1].properties[0].developerName", equalTo("ID"))
                .body("objectData[1].properties[0].objectData", hasSize(0))
                .body("objectData[1].properties[0].typeElementPropertyId", nullValue())
                .body("objectData[1].properties[1].contentFormat", nullValue())
                .body("objectData[1].properties[1].contentType", equalTo("ContentString"))
                .body("objectData[1].properties[1].contentValue", equalTo("employees"))
                .body("objectData[1].properties[1].developerName", equalTo("Name"))
                .body("objectData[1].properties[1].objectData", hasSize(0))
                .body("objectData[1].properties[1].typeElementPropertyId", nullValue())
                .body("objectData[1].properties[2].contentFormat", nullValue())
                .body("objectData[1].properties[2].contentType", equalTo("ContentString"))
                .body("objectData[1].properties[2].contentValue", equalTo("836e6d2d-47c7-457b-94f1-54ae07492d2f"))
                .body("objectData[1].properties[2].developerName", equalTo("Version"))
                .body("objectData[1].properties[2].objectData", hasSize(0))
                .body("objectData[1].properties[2].typeElementPropertyId", nullValue())
                .body("objectData[1].typeElementId", nullValue());
    }

    @Test
    public void testLoadSingleUniverse() {
        interceptor.addRule(new Rule.Builder()
                .respond(getClass().getClassLoader().getResourceAsStream("mocks/universe.xml")));

        var request = new JSONObject()
                .put("configurationValues", new JSONArray()
                        .put(new JSONObject()
                                .put("developerName", "Hub Hostname")
                                .put("contentValue", "atom.example.com")
                        )
                        .put(new JSONObject()
                                .put("developerName", "Hub Username")
                                .put("contentValue", "username")
                        )
                        .put(new JSONObject()
                                .put("developerName", "Hub Token")
                                .put("contentValue", "password")
                        )
                )
                .put("listFilter", new JSONObject()
                        .put("id", "12fa46f9-e14d-4042-878e-30b273b61731")
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
                .body("objectData", hasSize(1))
                .body("objectData[0].developerName", equalTo("Universe"))
                .body("objectData[0].externalId", equalTo("12fa46f9-e14d-4042-878e-30b273b61731"))
                .body("objectData[0].properties", hasSize(3))
                .body("objectData[0].properties[0].contentType", equalTo("ContentString"))
                .body("objectData[0].properties[0].contentValue", equalTo("12fa46f9-e14d-4042-878e-30b273b61731"))
                .body("objectData[0].properties[0].developerName", equalTo("ID"))
                .body("objectData[0].properties[0].objectData", hasSize(0))
                .body("objectData[0].properties[1].contentType", equalTo("ContentString"))
                .body("objectData[0].properties[1].contentValue", equalTo("person"))
                .body("objectData[0].properties[1].developerName", equalTo("Name"))
                .body("objectData[0].properties[1].objectData", hasSize(0))
                .body("objectData[0].properties[2].contentType", equalTo("ContentString"))
                .body("objectData[0].properties[2].contentValue", equalTo("9196c0f1-cf26-4768-91a1-9291ca04630b"))
                .body("objectData[0].properties[2].developerName", equalTo("Version"))
                .body("objectData[0].properties[2].objectData", hasSize(0));
    }

    @Test
    public void testLoadSingleUniverseWithBadRequest() throws IOException {
        var responseBody = ResponseBody.create(mediaType, Resources.toByteArray(getClass().getClassLoader().getResource("mocks/universe-bad-request.xml")));

        interceptor.addRule(new Rule.Builder()
                .respond(400, responseBody));

        var request = new JSONObject()
                .put("configurationValues", new JSONArray()
                        .put(new JSONObject()
                                .put("developerName", "Hub Hostname")
                                .put("contentValue", "atom.example.com")
                        )
                        .put(new JSONObject()
                                .put("developerName", "Hub Username")
                                .put("contentValue", "username")
                        )
                        .put(new JSONObject()
                                .put("developerName", "Hub Token")
                                .put("contentValue", "password")
                        )
                )
                .put("listFilter", new JSONObject()
                        .put("id", "12fa46f9-e14d-4042-878e-30b273b61731")
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
                .statusCode(400)
                .body("kind", equalTo("service"))
                .body("message", equalTo("The given universe id is blank."))
                .body("statusCode", equalTo(400))
                .body("uri", equalTo("/data"));
    }
}
