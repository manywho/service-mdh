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

public class DescribeTests {
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
    public void testDescribe() {
        interceptor.addRule(new Rule.Builder()
                .respond(getClass().getClassLoader().getResourceAsStream("mocks/universes.xml")));

        given()
                .contentType(ContentType.JSON)
                .body(new JSONObject())
                .when()
                .post("/metadata")
                .then()
                .assertThat()
                .statusCode(200)
                .body("actions", hasSize(0))
                .body("configurationValues", hasSize(3))
                .body("configurationValues[0].developerName", equalTo("Atom Hostname"))
                .body("configurationValues[0].contentType", equalTo("ContentString"))
                .body("configurationValues[0].required", equalTo(true))
                .body("configurationValues[1].developerName", equalTo("Atom Password"))
                .body("configurationValues[1].contentType", equalTo("ContentPassword"))
                .body("configurationValues[1].required", equalTo(true))
                .body("configurationValues[2].developerName", equalTo("Atom Username"))
                .body("configurationValues[2].contentType", equalTo("ContentString"))
                .body("configurationValues[2].required", equalTo(true))

                .body("install.typeElements", hasSize(1))
                .body("install.typeElements[0].developerName", equalTo("Universe"))
                .body("install.typeElements[0].developerSummary", not(isEmptyOrNullString()))
                .body("install.typeElements[0].properties", hasSize(3))
                .body("install.typeElements[0].properties[0].developerName", equalTo("ID"))
                .body("install.typeElements[0].properties[0].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[1].developerName", equalTo("Name"))
                .body("install.typeElements[0].properties[1].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[2].developerName", equalTo("Version"))
                .body("install.typeElements[0].properties[2].contentType", equalTo("ContentString"))

                .body("install.typeElements[0].bindings", hasSize(1))
                .body("install.typeElements[0].bindings[0].developerName", equalTo("Universe"))
                .body("install.typeElements[0].bindings[0].developerSummary", not(isEmptyOrNullString()))
                .body("install.typeElements[0].bindings[0].databaseTableName", equalTo("Universe"))
                .body("install.typeElements[0].bindings[0].propertyBindings", hasSize(3))
                .body("install.typeElements[0].bindings[0].propertyBindings[0].databaseFieldName", equalTo("ID"))
                .body("install.typeElements[0].bindings[0].propertyBindings[0].typeElementPropertyDeveloperName", equalTo("ID"))
                .body("install.typeElements[0].bindings[0].propertyBindings[1].databaseFieldName", equalTo("Name"))
                .body("install.typeElements[0].bindings[0].propertyBindings[1].typeElementPropertyDeveloperName", equalTo("Name"))
                .body("install.typeElements[0].bindings[0].propertyBindings[2].databaseFieldName", equalTo("Version"))
                .body("install.typeElements[0].bindings[0].propertyBindings[2].typeElementPropertyDeveloperName", equalTo("Version"))

                .body("providesAutoBinding", equalTo(false))
                .body("providesDatabase", equalTo(true))
                .body("providesFiles", equalTo(false))
                .body("providesIdentity", equalTo(false))
                .body("providesListening", equalTo(false))
                .body("providesLogic", equalTo(false))
                .body("providesNotifications", equalTo(false))
                .body("providesSmartSave", equalTo(false))
                .body("providesSocial", equalTo(false))
                .body("providesSharing", equalTo(false))
                .body("providesViews", equalTo(false))
                .body("providesVoting", equalTo(false));
    }


    @Test
    public void testInstall() {
        // TODO: currently all child types are deactivated

        interceptor.addRule(new Rule.Builder()
                .respond(getClass().getClassLoader().getResourceAsStream("mocks/universes-nested-elements.xml")));

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
                );

        var response = given()
                .contentType(ContentType.JSON)
                .body(request.toString())
                .when()
                .post("/metadata")
                .then()
                .assertThat()
                .statusCode(200);

        response
                .body("actions", hasSize(0))
                .body("configurationValues", hasSize(3))
                .body("configurationValues[0].contentType", equalTo("ContentString"))
                .body("configurationValues[0].contentValue", nullValue())
                .body("configurationValues[0].developerName", equalTo("Atom Hostname"))
                .body("configurationValues[0].ordinal", equalTo(0))
                .body("configurationValues[0].required", equalTo(true))
                .body("configurationValues[0].typeElementDeveloperName", nullValue())
                .body("configurationValues[1].contentType", equalTo("ContentPassword"))
                .body("configurationValues[1].contentValue", nullValue())
                .body("configurationValues[1].developerName", equalTo("Atom Password"))
                .body("configurationValues[1].ordinal", equalTo(0))
                .body("configurationValues[1].required", equalTo(true))
                .body("configurationValues[1].typeElementDeveloperName", nullValue())
                .body("configurationValues[2].contentType", equalTo("ContentString"))
                .body("configurationValues[2].contentValue", nullValue())
                .body("configurationValues[2].developerName", equalTo("Atom Username"))
                .body("configurationValues[2].ordinal", equalTo(0))
                .body("configurationValues[2].required", equalTo(true))
                .body("configurationValues[2].typeElementDeveloperName", nullValue())
                .body("culture", nullValue());
        response
                .body("install.typeElements", hasSize(3))
                .body("install.typeElements[0].bindings", hasSize(1))
                .body("install.typeElements[0].bindings[0].databaseTableName", equalTo("Universe"))
                .body("install.typeElements[0].bindings[0].developerName", equalTo("Universe"))
                .body("install.typeElements[0].bindings[0].developerSummary", equalTo("The Universe object structure"))
                .body("install.typeElements[0].bindings[0].id", nullValue())
                .body("install.typeElements[0].bindings[0].propertyBindings", hasSize(3))
                .body("install.typeElements[0].bindings[0].propertyBindings[0].databaseContentType", nullValue())
                .body("install.typeElements[0].bindings[0].propertyBindings[0].databaseFieldName", equalTo("ID"))
                .body("install.typeElements[0].bindings[0].propertyBindings[0].typeElementPropertyDeveloperName", equalTo("ID"))
                .body("install.typeElements[0].bindings[0].propertyBindings[0].typeElementPropertyId", nullValue())
                .body("install.typeElements[0].bindings[0].propertyBindings[1].databaseContentType", nullValue())
                .body("install.typeElements[0].bindings[0].propertyBindings[1].databaseFieldName", equalTo("Name"))
                .body("install.typeElements[0].bindings[0].propertyBindings[1].typeElementPropertyDeveloperName", equalTo("Name"))
                .body("install.typeElements[0].bindings[0].propertyBindings[1].typeElementPropertyId", nullValue())
                .body("install.typeElements[0].bindings[0].propertyBindings[2].databaseContentType", nullValue())
                .body("install.typeElements[0].bindings[0].propertyBindings[2].databaseFieldName", equalTo("Version"))
                .body("install.typeElements[0].bindings[0].propertyBindings[2].typeElementPropertyDeveloperName", equalTo("Version"))
                .body("install.typeElements[0].bindings[0].propertyBindings[2].typeElementPropertyId", nullValue())
                .body("install.typeElements[0].bindings[0].serviceElementId", nullValue())
                .body("install.typeElements[0].developerName", equalTo("Universe"))
                .body("install.typeElements[0].developerSummary", equalTo("The Universe object structure"))
                .body("install.typeElements[0].elementType", equalTo("TYPE"))
                .body("install.typeElements[0].id", nullValue())
                .body("install.typeElements[0].properties", hasSize(3))
                .body("install.typeElements[0].properties[0].contentFormat", nullValue())
                .body("install.typeElements[0].properties[0].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[0].developerName", equalTo("ID"))
                .body("install.typeElements[0].properties[0].id", nullValue())
                .body("install.typeElements[0].properties[0].typeElementDeveloperName", nullValue())
                .body("install.typeElements[0].properties[0].typeElementId", nullValue())
                .body("install.typeElements[0].properties[1].contentFormat", nullValue())
                .body("install.typeElements[0].properties[1].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[1].developerName", equalTo("Name"))
                .body("install.typeElements[0].properties[1].id", nullValue())
                .body("install.typeElements[0].properties[1].typeElementDeveloperName", nullValue())
                .body("install.typeElements[0].properties[1].typeElementId", nullValue())
                .body("install.typeElements[0].properties[2].contentFormat", nullValue())
                .body("install.typeElements[0].properties[2].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[2].developerName", equalTo("Version"))
                .body("install.typeElements[0].properties[2].id", nullValue())
                .body("install.typeElements[0].properties[2].typeElementDeveloperName", nullValue())
                .body("install.typeElements[0].properties[2].typeElementId", nullValue())
                .body("install.typeElements[0].serviceElementId", nullValue())
                .body("install.typeElements[0].updateByName", equalTo(false));
        response
                .body("install.typeElements[1].bindings", hasSize(2))
                .body("install.typeElements[1].bindings[0].databaseTableName", equalTo("ad7820f3-92a7-4919-9647-db934bda0065 golden-record"))
                .body("install.typeElements[1].bindings[0].developerName", equalTo("artist Golden Record"))
                .body("install.typeElements[1].bindings[0].developerSummary", equalTo("The structure of a golden record for the artist universe"))
                .body("install.typeElements[1].bindings[0].id", nullValue())
                .body("install.typeElements[1].bindings[0].propertyBindings", hasSize(4))
                .body("install.typeElements[1].bindings[0].propertyBindings[0].databaseContentType", nullValue())
                .body("install.typeElements[1].bindings[0].propertyBindings[0].databaseFieldName", equalTo("name"))
                .body("install.typeElements[1].bindings[0].propertyBindings[0].typeElementPropertyDeveloperName", equalTo("name"))
                .body("install.typeElements[1].bindings[0].propertyBindings[0].typeElementPropertyId", nullValue())
                .body("install.typeElements[1].bindings[0].propertyBindings[1].databaseContentType", nullValue())
                .body("install.typeElements[1].bindings[0].propertyBindings[1].databaseFieldName", equalTo("___sourceId"))
                .body("install.typeElements[1].bindings[0].propertyBindings[1].typeElementPropertyDeveloperName", equalTo("Filter: Source ID"))
                .body("install.typeElements[1].bindings[0].propertyBindings[1].typeElementPropertyId", nullValue())
                .body("install.typeElements[1].bindings[0].propertyBindings[2].databaseContentType", nullValue())
                .body("install.typeElements[1].bindings[0].propertyBindings[2].databaseFieldName", equalTo("___filterCreatedDate"))
                .body("install.typeElements[1].bindings[0].propertyBindings[2].typeElementPropertyDeveloperName", equalTo("Filter: Created Date"))
                .body("install.typeElements[1].bindings[0].propertyBindings[2].typeElementPropertyId", nullValue())
                .body("install.typeElements[1].bindings[0].propertyBindings[3].databaseContentType", nullValue())
                .body("install.typeElements[1].bindings[0].propertyBindings[3].databaseFieldName", equalTo("___filterUpdatedDate"))
                .body("install.typeElements[1].bindings[0].propertyBindings[3].typeElementPropertyDeveloperName", equalTo("Filter: Updated Date"))
                .body("install.typeElements[1].bindings[0].propertyBindings[3].typeElementPropertyId", nullValue())
                .body("install.typeElements[1].bindings[0].serviceElementId", nullValue())

                .body("install.typeElements[1].bindings[1].databaseTableName", equalTo("ad7820f3-92a7-4919-9647-db934bda0065 quarantine"))
                .body("install.typeElements[1].bindings[1].developerName", equalTo("artist Quarantine"))
                .body("install.typeElements[1].bindings[1].developerSummary", equalTo("The structure of a Quarantine artist for the artist universe"))
                .body("install.typeElements[1].bindings[1].id", nullValue())
                .body("install.typeElements[1].bindings[1].propertyBindings", hasSize(10))
                .body("install.typeElements[1].bindings[1].propertyBindings[0].databaseContentType", nullValue())
                .body("install.typeElements[1].bindings[1].propertyBindings[0].databaseFieldName", equalTo("name"))
                .body("install.typeElements[1].bindings[1].propertyBindings[0].typeElementPropertyDeveloperName", equalTo("name"))
                .body("install.typeElements[1].bindings[1].propertyBindings[0].typeElementPropertyId", nullValue())

                .body("install.typeElements[1].bindings[1].propertyBindings[1].databaseContentType", nullValue())
                .body("install.typeElements[1].bindings[1].propertyBindings[1].databaseFieldName", equalTo("status"))
                .body("install.typeElements[1].bindings[1].propertyBindings[1].typeElementPropertyDeveloperName", equalTo("Status"))
                .body("install.typeElements[1].bindings[1].propertyBindings[1].typeElementPropertyId", nullValue())

                .body("install.typeElements[1].bindings[1].propertyBindings[2].databaseContentType", nullValue())
                .body("install.typeElements[1].bindings[1].propertyBindings[2].databaseFieldName", equalTo("___sourceId"))
                .body("install.typeElements[1].bindings[1].propertyBindings[2].typeElementPropertyDeveloperName", equalTo("Filter: Source ID"))
                .body("install.typeElements[1].bindings[1].propertyBindings[2].typeElementPropertyId", nullValue())

                .body("install.typeElements[1].bindings[1].propertyBindings[3].databaseContentType", nullValue())
                .body("install.typeElements[1].bindings[1].propertyBindings[3].databaseFieldName", equalTo("sourceEntityId"))
                .body("install.typeElements[1].bindings[1].propertyBindings[3].typeElementPropertyDeveloperName", equalTo("Source Entity ID"))
                .body("install.typeElements[1].bindings[1].propertyBindings[3].typeElementPropertyId", nullValue())

                .body("install.typeElements[1].bindings[1].propertyBindings[4].databaseContentType", nullValue())
                .body("install.typeElements[1].bindings[1].propertyBindings[4].databaseFieldName", equalTo("___filterCreatedDate"))
                .body("install.typeElements[1].bindings[1].propertyBindings[4].typeElementPropertyDeveloperName", equalTo("Filter: Created Date"))
                .body("install.typeElements[1].bindings[1].propertyBindings[4].typeElementPropertyId", nullValue())

                .body("install.typeElements[1].bindings[1].propertyBindings[5].databaseContentType", nullValue())
                .body("install.typeElements[1].bindings[1].propertyBindings[5].databaseFieldName", equalTo("endDate"))
                .body("install.typeElements[1].bindings[1].propertyBindings[5].typeElementPropertyDeveloperName", equalTo("End Date"))
                .body("install.typeElements[1].bindings[1].propertyBindings[5].typeElementPropertyId", nullValue())
                .body("install.typeElements[1].bindings[1].serviceElementId", nullValue())

                .body("install.typeElements[1].bindings[1].propertyBindings[6].databaseContentType", nullValue())
                .body("install.typeElements[1].bindings[1].propertyBindings[6].databaseFieldName", equalTo("transactionId"))
                .body("install.typeElements[1].bindings[1].propertyBindings[6].typeElementPropertyDeveloperName", equalTo("Transaction ID"))
                .body("install.typeElements[1].bindings[1].propertyBindings[6].typeElementPropertyId", nullValue())
                .body("install.typeElements[1].bindings[1].serviceElementId", nullValue())

                .body("install.typeElements[1].bindings[1].propertyBindings[7].databaseContentType", nullValue())
                .body("install.typeElements[1].bindings[1].propertyBindings[7].databaseFieldName", equalTo("cause"))
                .body("install.typeElements[1].bindings[1].propertyBindings[7].typeElementPropertyDeveloperName", equalTo("Cause"))
                .body("install.typeElements[1].bindings[1].propertyBindings[7].typeElementPropertyId", nullValue())
                .body("install.typeElements[1].bindings[1].serviceElementId", nullValue())

                .body("install.typeElements[1].bindings[1].propertyBindings[8].databaseContentType", nullValue())
                .body("install.typeElements[1].bindings[1].propertyBindings[8].databaseFieldName", equalTo("reason"))
                .body("install.typeElements[1].bindings[1].propertyBindings[8].typeElementPropertyDeveloperName", equalTo("Reason"))
                .body("install.typeElements[1].bindings[1].propertyBindings[8].typeElementPropertyId", nullValue())
                .body("install.typeElements[1].bindings[1].serviceElementId", nullValue())

                .body("install.typeElements[1].bindings[1].propertyBindings[9].databaseContentType", nullValue())
                .body("install.typeElements[1].bindings[1].propertyBindings[9].databaseFieldName", equalTo("resolution"))
                .body("install.typeElements[1].bindings[1].propertyBindings[9].typeElementPropertyDeveloperName", equalTo("Resolution"))
                .body("install.typeElements[1].bindings[1].propertyBindings[9].typeElementPropertyId", nullValue())
                .body("install.typeElements[1].bindings[1].serviceElementId", nullValue())

                .body("install.typeElements[1].developerName", equalTo("artist"))
                .body("install.typeElements[1].developerSummary", nullValue())
                .body("install.typeElements[1].elementType", equalTo("TYPE"))
                .body("install.typeElements[1].id", nullValue())

                .body("install.typeElements[1].properties", hasSize(11))
                .body("install.typeElements[1].properties[0].contentFormat", nullValue())
                .body("install.typeElements[1].properties[0].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[0].developerName", equalTo("name"))
                .body("install.typeElements[1].properties[0].id", nullValue())
                .body("install.typeElements[1].properties[0].typeElementDeveloperName", nullValue())
                .body("install.typeElements[1].properties[0].typeElementId", nullValue())

                .body("install.typeElements[1].properties[1].contentFormat", nullValue())
                .body("install.typeElements[1].properties[1].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[1].developerName", equalTo("Filter: Source ID"))
                .body("install.typeElements[1].properties[1].id", nullValue())
                .body("install.typeElements[1].properties[1].typeElementDeveloperName", nullValue())
                .body("install.typeElements[1].properties[1].typeElementId", nullValue())

                .body("install.typeElements[1].properties[2].contentFormat", nullValue())
                .body("install.typeElements[1].properties[2].contentType", equalTo("ContentDateTime"))
                .body("install.typeElements[1].properties[2].developerName", equalTo("Filter: Created Date"))
                .body("install.typeElements[1].properties[2].id", nullValue())
                .body("install.typeElements[1].properties[2].typeElementDeveloperName", nullValue())
                .body("install.typeElements[1].properties[2].typeElementId", nullValue())

                .body("install.typeElements[1].properties[3].contentFormat", nullValue())
                .body("install.typeElements[1].properties[3].contentType", equalTo("ContentDateTime"))
                .body("install.typeElements[1].properties[3].developerName", equalTo("Filter: Updated Date"))
                .body("install.typeElements[1].properties[3].id", nullValue())
                .body("install.typeElements[1].properties[3].typeElementDeveloperName", nullValue())
                .body("install.typeElements[1].properties[3].typeElementId", nullValue())

                .body("install.typeElements[1].properties[4].contentFormat", nullValue())
                .body("install.typeElements[1].properties[4].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[4].developerName", equalTo("Source Entity ID"))
                .body("install.typeElements[1].properties[4].id", nullValue())
                .body("install.typeElements[1].properties[4].typeElementDeveloperName",nullValue())
                .body("install.typeElements[1].properties[4].typeElementId", nullValue())

                .body("install.typeElements[1].properties[5].contentFormat", nullValue())
                .body("install.typeElements[1].properties[5].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[5].developerName", equalTo("Status"))
                .body("install.typeElements[1].properties[5].id", nullValue())
                .body("install.typeElements[1].properties[5].typeElementDeveloperName",nullValue())
                .body("install.typeElements[1].properties[5].typeElementId", nullValue())

                .body("install.typeElements[1].properties[6].contentFormat", nullValue())
                .body("install.typeElements[1].properties[6].contentType", equalTo("ContentDateTime"))
                .body("install.typeElements[1].properties[6].developerName", equalTo("End Date"))
                .body("install.typeElements[1].properties[6].id", nullValue())
                .body("install.typeElements[1].properties[6].typeElementDeveloperName",nullValue())
                .body("install.typeElements[1].properties[6].typeElementId", nullValue())

                .body("install.typeElements[1].properties[7].contentFormat", nullValue())
                .body("install.typeElements[1].properties[7].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[7].developerName", equalTo("Transaction ID"))
                .body("install.typeElements[1].properties[7].id", nullValue())
                .body("install.typeElements[1].properties[7].typeElementDeveloperName",nullValue())
                .body("install.typeElements[1].properties[7].typeElementId", nullValue())

                .body("install.typeElements[1].properties[8].contentFormat", nullValue())
                .body("install.typeElements[1].properties[8].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[8].developerName", equalTo("Cause"))
                .body("install.typeElements[1].properties[8].id", nullValue())
                .body("install.typeElements[1].properties[8].typeElementDeveloperName",nullValue())
                .body("install.typeElements[1].properties[8].typeElementId", nullValue())

                .body("install.typeElements[1].properties[9].contentFormat", nullValue())
                .body("install.typeElements[1].properties[9].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[9].developerName", equalTo("Reason"))
                .body("install.typeElements[1].properties[9].id", nullValue())
                .body("install.typeElements[1].properties[9].typeElementDeveloperName",nullValue())
                .body("install.typeElements[1].properties[9].typeElementId", nullValue())

                .body("install.typeElements[1].properties[10].contentFormat", nullValue())
                .body("install.typeElements[1].properties[10].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[10].developerName", equalTo("Resolution"))
                .body("install.typeElements[1].properties[10].id", nullValue())
                .body("install.typeElements[1].properties[10].typeElementDeveloperName",nullValue())
                .body("install.typeElements[1].properties[10].typeElementId", nullValue())

                .body("install.typeElements[1].serviceElementId", nullValue())
                .body("install.typeElements[1].updateByName", equalTo(false));
        response
                .body("providesAutoBinding", equalTo(false))
                .body("providesDatabase", equalTo(true))
                .body("providesFiles", equalTo(false))
                .body("providesIdentity", equalTo(false))
                .body("providesListening", equalTo(false))
                .body("providesLogic", equalTo(false))
                .body("providesNotifications", equalTo(false))
                .body("providesSharing", equalTo(false))
                .body("providesSmartSave", equalTo(false))
                .body("providesSocial", equalTo(false))
                .body("providesViews", equalTo(false))
                .body("providesVoting", equalTo(false))
                .body("uri", nullValue());
    }
}
