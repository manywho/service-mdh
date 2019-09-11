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
                .body("install.typeElements[0].developerName", equalTo("Fuzzy Match Details"))
                .body("install.typeElements[0].developerSummary", not(isEmptyOrNullString()))
                .body("install.typeElements[0].properties", hasSize(7))
                .body("install.typeElements[0].properties[0].developerName", equalTo("Field"))
                .body("install.typeElements[0].properties[0].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[1].developerName", equalTo("First"))
                .body("install.typeElements[0].properties[1].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[2].developerName", equalTo("ID"))
                .body("install.typeElements[0].properties[2].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[3].developerName", equalTo("Match Strength"))
                .body("install.typeElements[0].properties[3].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[4].developerName", equalTo("Method"))
                .body("install.typeElements[0].properties[4].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[5].developerName", equalTo("Second"))
                .body("install.typeElements[0].properties[5].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[6].developerName", equalTo("Threshold"))
                .body("install.typeElements[0].properties[6].contentType", equalTo("ContentString"))

                .body("install.typeElements[0].bindings", hasSize(1))
                .body("install.typeElements[0].bindings[0].databaseTableName", equalTo("Fuzzy Match Details"))
                .body("install.typeElements[0].bindings[0].developerName", equalTo("Fuzzy Match Details"))
                .body("install.typeElements[0].bindings[0].developerSummary", equalTo("The Fuzzy Match Details object structure"))
                .body("install.typeElements[0].bindings[0].id", nullValue())

                .body("install.typeElements[0].bindings[0].propertyBindings[0].typeElementPropertyDeveloperName", equalTo("Field"))
                .body("install.typeElements[0].bindings[0].propertyBindings[0].databaseFieldName", equalTo("Field"))

                .body("install.typeElements[0].bindings[0].propertyBindings[1].typeElementPropertyDeveloperName", equalTo("First"))
                .body("install.typeElements[0].bindings[0].propertyBindings[1].databaseFieldName", equalTo("First"))

                .body("install.typeElements[0].bindings[0].propertyBindings[2].typeElementPropertyDeveloperName", equalTo("ID"))
                .body("install.typeElements[0].bindings[0].propertyBindings[2].databaseFieldName", equalTo("ID"))

                .body("install.typeElements[0].bindings[0].propertyBindings[3].typeElementPropertyDeveloperName", equalTo("Match Strength"))
                .body("install.typeElements[0].bindings[0].propertyBindings[3].databaseFieldName", equalTo("Match Strength"))

                .body("install.typeElements[0].bindings[0].propertyBindings[4].typeElementPropertyDeveloperName", equalTo("Method"))
                .body("install.typeElements[0].bindings[0].propertyBindings[4].databaseFieldName", equalTo("Method"))

                .body("install.typeElements[0].bindings[0].propertyBindings[5].typeElementPropertyDeveloperName", equalTo("Second"))
                .body("install.typeElements[0].bindings[0].propertyBindings[5].databaseFieldName", equalTo("Second"))

                .body("install.typeElements[0].bindings[0].propertyBindings[6].typeElementPropertyDeveloperName", equalTo("Threshold"))
                .body("install.typeElements[0].bindings[0].propertyBindings[6].databaseFieldName", equalTo("Threshold"))


                .body("install.typeElements", hasSize(2))
                .body("install.typeElements[1].developerName", equalTo("Universe"))
                .body("install.typeElements[1].developerSummary", not(isEmptyOrNullString()))
                .body("install.typeElements[1].properties", hasSize(3))
                .body("install.typeElements[1].properties[0].developerName", equalTo("ID"))
                .body("install.typeElements[1].properties[0].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[1].developerName", equalTo("Name"))
                .body("install.typeElements[1].properties[1].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[2].developerName", equalTo("Version"))
                .body("install.typeElements[1].properties[2].contentType", equalTo("ContentString"))

                .body("install.typeElements[1].bindings", hasSize(1))
                .body("install.typeElements[1].bindings[0].developerName", equalTo("Universe"))
                .body("install.typeElements[1].bindings[0].developerSummary", not(isEmptyOrNullString()))
                .body("install.typeElements[1].bindings[0].databaseTableName", equalTo("Universe"))
                .body("install.typeElements[1].bindings[0].propertyBindings", hasSize(3))
                .body("install.typeElements[1].bindings[0].propertyBindings[0].databaseFieldName", equalTo("ID"))
                .body("install.typeElements[1].bindings[0].propertyBindings[0].typeElementPropertyDeveloperName", equalTo("ID"))
                .body("install.typeElements[1].bindings[0].propertyBindings[1].databaseFieldName", equalTo("Name"))
                .body("install.typeElements[1].bindings[0].propertyBindings[1].typeElementPropertyDeveloperName", equalTo("Name"))
                .body("install.typeElements[1].bindings[0].propertyBindings[2].databaseFieldName", equalTo("Version"))
                .body("install.typeElements[1].bindings[0].propertyBindings[2].typeElementPropertyDeveloperName", equalTo("Version"))

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
                .body("install.typeElements[0].bindings", hasSize(1))
                .body("install.typeElements[0].developerName", equalTo("Fuzzy Match Details"))
                .body("install.typeElements[0].developerSummary", equalTo("The Fuzzy Match Details object structure"))
                .body("install.typeElements[0].elementType", equalTo("TYPE"))
                .body("install.typeElements[0].id", nullValue())
                .body("install.typeElements[0].properties", hasSize(7))

                .body("install.typeElements[0].properties[0].contentFormat", nullValue())
                .body("install.typeElements[0].properties[0].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[0].developerName", equalTo("Field"))
                .body("install.typeElements[0].properties[0].id", nullValue())
                .body("install.typeElements[0].properties[0].typeElementDeveloperName", nullValue())
                .body("install.typeElements[0].properties[0].typeElementId", nullValue())

                .body("install.typeElements[0].properties[1].contentFormat", nullValue())
                .body("install.typeElements[0].properties[1].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[1].developerName", equalTo("First"))
                .body("install.typeElements[0].properties[1].id", nullValue())
                .body("install.typeElements[0].properties[1].typeElementDeveloperName", nullValue())
                .body("install.typeElements[0].properties[1].typeElementId", nullValue())

                .body("install.typeElements[0].properties[2].contentFormat", nullValue())
                .body("install.typeElements[0].properties[2].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[2].developerName", equalTo("ID"))
                .body("install.typeElements[0].properties[2].id", nullValue())
                .body("install.typeElements[0].properties[2].typeElementDeveloperName", nullValue())
                .body("install.typeElements[0].properties[2].typeElementId", nullValue())

                .body("install.typeElements[0].properties[3].contentFormat", nullValue())
                .body("install.typeElements[0].properties[3].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[3].developerName", equalTo("Match Strength"))
                .body("install.typeElements[0].properties[3].id", nullValue())
                .body("install.typeElements[0].properties[3].typeElementDeveloperName", nullValue())
                .body("install.typeElements[0].properties[3].typeElementId", nullValue())

                .body("install.typeElements[0].properties[4].contentFormat", nullValue())
                .body("install.typeElements[0].properties[4].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[4].developerName", equalTo("Method"))
                .body("install.typeElements[0].properties[4].id", nullValue())
                .body("install.typeElements[0].properties[4].typeElementDeveloperName", nullValue())
                .body("install.typeElements[0].properties[4].typeElementId", nullValue())

                .body("install.typeElements[0].properties[5].contentFormat", nullValue())
                .body("install.typeElements[0].properties[5].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[5].developerName", equalTo("Second"))
                .body("install.typeElements[0].properties[5].id", nullValue())
                .body("install.typeElements[0].properties[5].typeElementDeveloperName", nullValue())
                .body("install.typeElements[0].properties[5].typeElementId", nullValue())

                .body("install.typeElements[0].properties[6].contentFormat", nullValue())
                .body("install.typeElements[0].properties[6].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[6].developerName", equalTo("Threshold"))
                .body("install.typeElements[0].properties[6].id", nullValue())
                .body("install.typeElements[0].properties[6].typeElementDeveloperName", nullValue())
                .body("install.typeElements[0].properties[6].typeElementId", nullValue())

                .body("install.typeElements[0].serviceElementId", nullValue())
                .body("install.typeElements[0].updateByName", equalTo(false));

        response
                .body("install.typeElements", hasSize(4))
                .body("install.typeElements[1].bindings", hasSize(1))
                .body("install.typeElements[1].bindings[0].databaseTableName", equalTo("Universe"))
                .body("install.typeElements[1].bindings[0].developerName", equalTo("Universe"))
                .body("install.typeElements[1].bindings[0].developerSummary", equalTo("The Universe object structure"))
                .body("install.typeElements[1].bindings[0].id", nullValue())
                .body("install.typeElements[1].bindings[0].propertyBindings", hasSize(3))
                .body("install.typeElements[1].bindings[0].propertyBindings[0].databaseContentType", nullValue())
                .body("install.typeElements[1].bindings[0].propertyBindings[0].databaseFieldName", equalTo("ID"))
                .body("install.typeElements[1].bindings[0].propertyBindings[0].typeElementPropertyDeveloperName", equalTo("ID"))
                .body("install.typeElements[1].bindings[0].propertyBindings[0].typeElementPropertyId", nullValue())
                .body("install.typeElements[1].bindings[0].propertyBindings[1].databaseContentType", nullValue())
                .body("install.typeElements[1].bindings[0].propertyBindings[1].databaseFieldName", equalTo("Name"))
                .body("install.typeElements[1].bindings[0].propertyBindings[1].typeElementPropertyDeveloperName", equalTo("Name"))
                .body("install.typeElements[1].bindings[0].propertyBindings[1].typeElementPropertyId", nullValue())
                .body("install.typeElements[1].bindings[0].propertyBindings[2].databaseContentType", nullValue())
                .body("install.typeElements[1].bindings[0].propertyBindings[2].databaseFieldName", equalTo("Version"))
                .body("install.typeElements[1].bindings[0].propertyBindings[2].typeElementPropertyDeveloperName", equalTo("Version"))
                .body("install.typeElements[1].bindings[0].propertyBindings[2].typeElementPropertyId", nullValue())
                .body("install.typeElements[1].bindings[0].serviceElementId", nullValue())
                .body("install.typeElements[1].developerName", equalTo("Universe"))
                .body("install.typeElements[1].developerSummary", equalTo("The Universe object structure"))
                .body("install.typeElements[1].elementType", equalTo("TYPE"))
                .body("install.typeElements[1].id", nullValue())
                .body("install.typeElements[1].properties", hasSize(3))
                .body("install.typeElements[1].properties[0].contentFormat", nullValue())
                .body("install.typeElements[1].properties[0].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[0].developerName", equalTo("ID"))
                .body("install.typeElements[1].properties[0].id", nullValue())
                .body("install.typeElements[1].properties[0].typeElementDeveloperName", nullValue())
                .body("install.typeElements[1].properties[0].typeElementId", nullValue())
                .body("install.typeElements[1].properties[1].contentFormat", nullValue())
                .body("install.typeElements[1].properties[1].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[1].developerName", equalTo("Name"))
                .body("install.typeElements[1].properties[1].id", nullValue())
                .body("install.typeElements[1].properties[1].typeElementDeveloperName", nullValue())
                .body("install.typeElements[1].properties[1].typeElementId", nullValue())
                .body("install.typeElements[1].properties[2].contentFormat", nullValue())
                .body("install.typeElements[1].properties[2].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[2].developerName", equalTo("Version"))
                .body("install.typeElements[1].properties[2].id", nullValue())
                .body("install.typeElements[1].properties[2].typeElementDeveloperName", nullValue())
                .body("install.typeElements[1].properties[2].typeElementId", nullValue())
                .body("install.typeElements[1].serviceElementId", nullValue())
                .body("install.typeElements[1].updateByName", equalTo(false));

        response
                .body("install.typeElements[2].bindings", hasSize(3))
                .body("install.typeElements[2].bindings[0].databaseTableName", equalTo("ad7820f3-92a7-4919-9647-db934bda0065-golden-record"))
                .body("install.typeElements[2].bindings[0].developerName", equalTo("artist Golden Record"))
                .body("install.typeElements[2].bindings[0].developerSummary", equalTo("The structure of a golden record for the artist universe"))
                .body("install.typeElements[2].bindings[0].id", nullValue())
                .body("install.typeElements[2].bindings[0].propertyBindings", hasSize(4))
                .body("install.typeElements[2].bindings[0].propertyBindings[0].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[0].propertyBindings[0].databaseFieldName", equalTo("name"))
                .body("install.typeElements[2].bindings[0].propertyBindings[0].typeElementPropertyDeveloperName", equalTo("name"))
                .body("install.typeElements[2].bindings[0].propertyBindings[0].typeElementPropertyId", nullValue())
                .body("install.typeElements[2].bindings[0].propertyBindings[1].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[0].propertyBindings[1].databaseFieldName", equalTo("___sourceId"))
                .body("install.typeElements[2].bindings[0].propertyBindings[1].typeElementPropertyDeveloperName", equalTo("Golden Record: Source ID"))
                .body("install.typeElements[2].bindings[0].propertyBindings[1].typeElementPropertyId", nullValue())
                .body("install.typeElements[2].bindings[0].propertyBindings[2].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[0].propertyBindings[2].databaseFieldName", equalTo("___filterCreatedDate"))
                .body("install.typeElements[2].bindings[0].propertyBindings[2].typeElementPropertyDeveloperName", equalTo("Golden Record (Filter): Created Date"))
                .body("install.typeElements[2].bindings[0].propertyBindings[2].typeElementPropertyId", nullValue())
                .body("install.typeElements[2].bindings[0].propertyBindings[3].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[0].propertyBindings[3].databaseFieldName", equalTo("___filterUpdatedDate"))
                .body("install.typeElements[2].bindings[0].propertyBindings[3].typeElementPropertyDeveloperName", equalTo("Golden Record (Filter): Updated Date"))
                .body("install.typeElements[2].bindings[0].propertyBindings[3].typeElementPropertyId", nullValue())
                .body("install.typeElements[2].bindings[0].serviceElementId", nullValue())

                .body("install.typeElements[2].bindings[1].databaseTableName", equalTo("ad7820f3-92a7-4919-9647-db934bda0065-quarantine"))
                .body("install.typeElements[2].bindings[1].developerName", equalTo("artist Quarantine"))
                .body("install.typeElements[2].bindings[1].developerSummary", equalTo("The structure of a Quarantine artist for the artist universe"))
                .body("install.typeElements[2].bindings[1].id", nullValue())
                .body("install.typeElements[2].bindings[1].propertyBindings", hasSize(10))
                .body("install.typeElements[2].bindings[1].propertyBindings[0].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[1].propertyBindings[0].databaseFieldName", equalTo("name"))
                .body("install.typeElements[2].bindings[1].propertyBindings[0].typeElementPropertyDeveloperName", equalTo("name"))
                .body("install.typeElements[2].bindings[1].propertyBindings[0].typeElementPropertyId", nullValue())

                .body("install.typeElements[2].bindings[1].propertyBindings[1].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[1].propertyBindings[1].databaseFieldName", equalTo("___status"))
                .body("install.typeElements[2].bindings[1].propertyBindings[1].typeElementPropertyDeveloperName", equalTo("Quarantine: Status"))
                .body("install.typeElements[2].bindings[1].propertyBindings[1].typeElementPropertyId", nullValue())

                .body("install.typeElements[2].bindings[1].propertyBindings[2].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[1].propertyBindings[2].databaseFieldName", equalTo("___sourceId"))
                .body("install.typeElements[2].bindings[1].propertyBindings[2].typeElementPropertyDeveloperName", equalTo("Quarantine (Filter): Source ID"))
                .body("install.typeElements[2].bindings[1].propertyBindings[2].typeElementPropertyId", nullValue())

                .body("install.typeElements[2].bindings[1].propertyBindings[3].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[1].propertyBindings[3].databaseFieldName", equalTo("___sourceEntityId"))
                .body("install.typeElements[2].bindings[1].propertyBindings[3].typeElementPropertyDeveloperName", equalTo("Quarantine: Source Entity ID"))
                .body("install.typeElements[2].bindings[1].propertyBindings[3].typeElementPropertyId", nullValue())

                .body("install.typeElements[2].bindings[1].propertyBindings[4].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[1].propertyBindings[4].databaseFieldName", equalTo("___createdDate"))
                .body("install.typeElements[2].bindings[1].propertyBindings[4].typeElementPropertyDeveloperName", equalTo("Quarantine (Filter): Created Date"))
                .body("install.typeElements[2].bindings[1].propertyBindings[4].typeElementPropertyId", nullValue())

                .body("install.typeElements[2].bindings[1].propertyBindings[5].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[1].propertyBindings[5].databaseFieldName", equalTo("___endDate"))
                .body("install.typeElements[2].bindings[1].propertyBindings[5].typeElementPropertyDeveloperName", equalTo("Quarantine (Filter): End Date"))
                .body("install.typeElements[2].bindings[1].propertyBindings[5].typeElementPropertyId", nullValue())
                .body("install.typeElements[2].bindings[1].serviceElementId", nullValue())

                .body("install.typeElements[2].bindings[1].propertyBindings[6].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[1].propertyBindings[6].databaseFieldName", equalTo("___transactionId"))
                .body("install.typeElements[2].bindings[1].propertyBindings[6].typeElementPropertyDeveloperName", equalTo("Quarantine: Transaction ID"))
                .body("install.typeElements[2].bindings[1].propertyBindings[6].typeElementPropertyId", nullValue())
                .body("install.typeElements[2].bindings[1].serviceElementId", nullValue())

                .body("install.typeElements[2].bindings[1].propertyBindings[7].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[1].propertyBindings[7].databaseFieldName", equalTo("___cause"))
                .body("install.typeElements[2].bindings[1].propertyBindings[7].typeElementPropertyDeveloperName", equalTo("Quarantine: Cause"))
                .body("install.typeElements[2].bindings[1].propertyBindings[7].typeElementPropertyId", nullValue())
                .body("install.typeElements[2].bindings[1].serviceElementId", nullValue())

                .body("install.typeElements[2].bindings[1].propertyBindings[8].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[1].propertyBindings[8].databaseFieldName", equalTo("___reason"))
                .body("install.typeElements[2].bindings[1].propertyBindings[8].typeElementPropertyDeveloperName", equalTo("Quarantine: Reason"))
                .body("install.typeElements[2].bindings[1].propertyBindings[8].typeElementPropertyId", nullValue())
                .body("install.typeElements[2].bindings[1].serviceElementId", nullValue())

                .body("install.typeElements[2].bindings[1].propertyBindings[9].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[1].propertyBindings[9].databaseFieldName", equalTo("___resolution"))
                .body("install.typeElements[2].bindings[1].propertyBindings[9].typeElementPropertyDeveloperName", equalTo("Quarantine: Resolution"))
                .body("install.typeElements[2].bindings[1].propertyBindings[9].typeElementPropertyId", nullValue())
                .body("install.typeElements[2].bindings[1].serviceElementId", nullValue())

                .body("install.typeElements[2].bindings[2].databaseTableName", equalTo("ad7820f3-92a7-4919-9647-db934bda0065-match"))
                .body("install.typeElements[2].bindings[2].developerName", equalTo("artist Match"))
                .body("install.typeElements[2].bindings[2].developerSummary", equalTo("The structure of matches for the artist universe"))
                .body("install.typeElements[2].bindings[2].id", nullValue())
                .body("install.typeElements[2].bindings[2].propertyBindings", hasSize(5))

                .body("install.typeElements[2].bindings[2].propertyBindings[0].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[2].propertyBindings[0].databaseFieldName", equalTo("name"))
                .body("install.typeElements[2].bindings[2].propertyBindings[0].typeElementPropertyDeveloperName", equalTo("name"))
                .body("install.typeElements[2].bindings[2].propertyBindings[0].typeElementPropertyId", nullValue())

                .body("install.typeElements[2].bindings[2].propertyBindings[1].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[2].propertyBindings[1].databaseFieldName", equalTo("Fuzzy Match Details"))
                .body("install.typeElements[2].bindings[2].propertyBindings[1].typeElementPropertyDeveloperName", equalTo("Fuzzy Match Details"))
                .body("install.typeElements[2].bindings[2].propertyBindings[1].typeElementPropertyId", nullValue())

                .body("install.typeElements[2].bindings[2].propertyBindings[2].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[2].propertyBindings[2].databaseFieldName", equalTo("Matching Entities"))
                .body("install.typeElements[2].bindings[2].propertyBindings[2].typeElementPropertyDeveloperName", equalTo("Matching Entities"))
                .body("install.typeElements[2].bindings[2].propertyBindings[2].typeElementPropertyId", nullValue())

                .body("install.typeElements[2].bindings[2].propertyBindings[3].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[2].propertyBindings[3].databaseFieldName", equalTo("Duplicate Entities"))
                .body("install.typeElements[2].bindings[2].propertyBindings[3].typeElementPropertyDeveloperName", equalTo("Duplicate Entities"))
                .body("install.typeElements[2].bindings[2].propertyBindings[3].typeElementPropertyId", nullValue())

                .body("install.typeElements[2].bindings[2].propertyBindings[4].databaseContentType", nullValue())
                .body("install.typeElements[2].bindings[2].propertyBindings[4].databaseFieldName", equalTo("Already Linked Entities"))
                .body("install.typeElements[2].bindings[2].propertyBindings[4].typeElementPropertyDeveloperName", equalTo("Already Linked Entities"))
                .body("install.typeElements[2].bindings[2].propertyBindings[4].typeElementPropertyId", nullValue())

                .body("install.typeElements[2].bindings[2].serviceElementId", nullValue())

                .body("install.typeElements[2].developerName", equalTo("artist"))
                .body("install.typeElements[2].developerSummary", nullValue())
                .body("install.typeElements[2].elementType", equalTo("TYPE"))
                .body("install.typeElements[2].id", nullValue())

                .body("install.typeElements[2].properties", hasSize(17))
                .body("install.typeElements[2].properties[0].contentFormat", nullValue())
                .body("install.typeElements[2].properties[0].contentType", equalTo("ContentString"))
                .body("install.typeElements[2].properties[0].developerName", equalTo("name"))
                .body("install.typeElements[2].properties[0].id", nullValue())
                .body("install.typeElements[2].properties[0].typeElementDeveloperName", nullValue())
                .body("install.typeElements[2].properties[0].typeElementId", nullValue())

                .body("install.typeElements[2].properties[1].contentFormat", nullValue())
                .body("install.typeElements[2].properties[1].contentType", equalTo("ContentString"))
                .body("install.typeElements[2].properties[1].developerName", equalTo("Golden Record: Source ID"))
                .body("install.typeElements[2].properties[1].id", nullValue())
                .body("install.typeElements[2].properties[1].typeElementDeveloperName", nullValue())
                .body("install.typeElements[2].properties[1].typeElementId", nullValue())

                .body("install.typeElements[2].properties[2].contentFormat", nullValue())
                .body("install.typeElements[2].properties[2].contentType", equalTo("ContentDateTime"))
                .body("install.typeElements[2].properties[2].developerName", equalTo("Golden Record (Filter): Created Date"))
                .body("install.typeElements[2].properties[2].id", nullValue())
                .body("install.typeElements[2].properties[2].typeElementDeveloperName", nullValue())
                .body("install.typeElements[2].properties[2].typeElementId", nullValue())

                .body("install.typeElements[2].properties[3].contentFormat", nullValue())
                .body("install.typeElements[2].properties[3].contentType", equalTo("ContentDateTime"))
                .body("install.typeElements[2].properties[3].developerName", equalTo("Golden Record (Filter): Updated Date"))
                .body("install.typeElements[2].properties[3].id", nullValue())
                .body("install.typeElements[2].properties[3].typeElementDeveloperName", nullValue())
                .body("install.typeElements[2].properties[3].typeElementId", nullValue())

                .body("install.typeElements[2].properties[4].contentFormat", nullValue())
                .body("install.typeElements[2].properties[4].contentType", equalTo("ContentString"))
                .body("install.typeElements[2].properties[4].developerName", equalTo("Quarantine (Filter): Source ID"))
                .body("install.typeElements[2].properties[4].id", nullValue())
                .body("install.typeElements[2].properties[4].typeElementDeveloperName",nullValue())
                .body("install.typeElements[2].properties[4].typeElementId", nullValue())

                .body("install.typeElements[2].properties[5].contentFormat", nullValue())
                .body("install.typeElements[2].properties[5].contentType", equalTo("ContentDateTime"))
                .body("install.typeElements[2].properties[5].developerName", equalTo("Quarantine (Filter): Created Date"))
                .body("install.typeElements[2].properties[5].id", nullValue())
                .body("install.typeElements[2].properties[5].typeElementDeveloperName",nullValue())
                .body("install.typeElements[2].properties[5].typeElementId", nullValue())

                .body("install.typeElements[2].properties[6].contentFormat", nullValue())
                .body("install.typeElements[2].properties[6].contentType", equalTo("ContentString"))
                .body("install.typeElements[2].properties[6].developerName", equalTo("Quarantine: Source Entity ID"))
                .body("install.typeElements[2].properties[6].id", nullValue())
                .body("install.typeElements[2].properties[6].typeElementDeveloperName",nullValue())
                .body("install.typeElements[2].properties[6].typeElementId", nullValue())

                .body("install.typeElements[2].properties[7].contentFormat", nullValue())
                .body("install.typeElements[2].properties[7].contentType", equalTo("ContentString"))
                .body("install.typeElements[2].properties[7].developerName", equalTo("Quarantine: Status"))
                .body("install.typeElements[2].properties[7].id", nullValue())
                .body("install.typeElements[2].properties[7].typeElementDeveloperName",nullValue())
                .body("install.typeElements[2].properties[7].typeElementId", nullValue())

                .body("install.typeElements[2].properties[8].contentFormat", nullValue())
                .body("install.typeElements[2].properties[8].contentType", equalTo("ContentDateTime"))
                .body("install.typeElements[2].properties[8].developerName", equalTo("Quarantine (Filter): End Date"))
                .body("install.typeElements[2].properties[8].id", nullValue())
                .body("install.typeElements[2].properties[8].typeElementDeveloperName",nullValue())
                .body("install.typeElements[2].properties[8].typeElementId", nullValue())

                .body("install.typeElements[2].properties[9].contentFormat", nullValue())
                .body("install.typeElements[2].properties[9].contentType", equalTo("ContentString"))
                .body("install.typeElements[2].properties[9].developerName", equalTo("Quarantine: Transaction ID"))
                .body("install.typeElements[2].properties[9].id", nullValue())
                .body("install.typeElements[2].properties[9].typeElementDeveloperName",nullValue())
                .body("install.typeElements[2].properties[9].typeElementId", nullValue())

                .body("install.typeElements[2].properties[10].contentFormat", nullValue())
                .body("install.typeElements[2].properties[10].contentType", equalTo("ContentString"))
                .body("install.typeElements[2].properties[10].developerName", equalTo("Quarantine: Cause"))
                .body("install.typeElements[2].properties[10].id", nullValue())
                .body("install.typeElements[2].properties[10].typeElementDeveloperName",nullValue())
                .body("install.typeElements[2].properties[10].typeElementId", nullValue())

                .body("install.typeElements[2].properties[11].contentFormat", nullValue())
                .body("install.typeElements[2].properties[11].contentType", equalTo("ContentString"))
                .body("install.typeElements[2].properties[11].developerName", equalTo("Quarantine: Reason"))
                .body("install.typeElements[2].properties[11].id", nullValue())
                .body("install.typeElements[2].properties[11].typeElementDeveloperName",nullValue())
                .body("install.typeElements[2].properties[11].typeElementId", nullValue())

                .body("install.typeElements[2].properties[12].contentFormat", nullValue())
                .body("install.typeElements[2].properties[12].contentType", equalTo("ContentString"))
                .body("install.typeElements[2].properties[12].developerName", equalTo("Quarantine: Resolution"))
                .body("install.typeElements[2].properties[12].id", nullValue())
                .body("install.typeElements[2].properties[12].typeElementDeveloperName",nullValue())
                .body("install.typeElements[2].properties[12].typeElementId", nullValue())

                .body("install.typeElements[2].properties[13].contentFormat", nullValue())
                .body("install.typeElements[2].properties[13].contentType", equalTo("ContentObject"))
                .body("install.typeElements[2].properties[13].developerName", equalTo("Fuzzy Match Details"))
                .body("install.typeElements[2].properties[13].id", nullValue())
                .body("install.typeElements[2].properties[13].typeElementDeveloperName", equalTo("Fuzzy Match Details"))
                .body("install.typeElements[2].properties[13].typeElementId", nullValue())

                .body("install.typeElements[2].properties[14].contentFormat", nullValue())
                .body("install.typeElements[2].properties[14].contentType", equalTo("ContentList"))
                .body("install.typeElements[2].properties[14].developerName", equalTo("Duplicate Entities"))
                .body("install.typeElements[2].properties[14].id", nullValue())
                .body("install.typeElements[2].properties[14].typeElementDeveloperName", equalTo("artist"))
                .body("install.typeElements[2].properties[14].typeElementId",  nullValue())

                .body("install.typeElements[2].properties[15].contentFormat", nullValue())
                .body("install.typeElements[2].properties[15].contentType", equalTo("ContentList"))
                .body("install.typeElements[2].properties[15].developerName", equalTo("Matching Entities"))
                .body("install.typeElements[2].properties[15].id", nullValue())
                .body("install.typeElements[2].properties[15].typeElementDeveloperName", equalTo("artist"))
                .body("install.typeElements[2].properties[15].typeElementId", nullValue())

                .body("install.typeElements[2].properties[16].contentFormat", nullValue())
                .body("install.typeElements[2].properties[16].contentType", equalTo("ContentList"))
                .body("install.typeElements[2].properties[16].developerName", equalTo("Already Linked Entities"))
                .body("install.typeElements[2].properties[16].id", nullValue())
                .body("install.typeElements[2].properties[16].typeElementDeveloperName",  equalTo("artist"))
                .body("install.typeElements[2].properties[16].typeElementId", nullValue())

                .body("install.typeElements[2].serviceElementId", nullValue())
                .body("install.typeElements[2].updateByName", equalTo(false));
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
