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

public class DescribeTest {
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
                );

        given()
                .contentType(ContentType.JSON)
                .body(request.toString())
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
                .body("install.typeElements", hasSize(5))
                .body("install.typeElements[0].developerName", equalTo("Universe"))
                .body("install.typeElements[0].developerSummary", not(isEmptyOrNullString()))
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
                .body("install.typeElements[0].elementType", equalTo("TYPE"))
                .body("install.typeElements[0].properties", hasSize(3))
                .body("install.typeElements[0].properties[0].developerName", equalTo("ID"))
                .body("install.typeElements[0].properties[0].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[1].developerName", equalTo("Name"))
                .body("install.typeElements[0].properties[1].contentType", equalTo("ContentString"))
                .body("install.typeElements[0].properties[2].developerName", equalTo("Version"))
                .body("install.typeElements[0].properties[2].contentType", equalTo("ContentString"))

                .body("install.typeElements[1].developerName", equalTo("account Model"))
                .body("install.typeElements[1].developerSummary", isEmptyOrNullString())
                .body("install.typeElements[1].bindings", hasSize(1))
                .body("install.typeElements[1].bindings[0].developerName", equalTo("account Model"))
                .body("install.typeElements[1].bindings[0].developerSummary", equalTo("The model for the account universe"))
                .body("install.typeElements[1].bindings[0].databaseTableName", equalTo("2df80af3-1035-4732-ba12-6c84b0d9f568"))
                .body("install.typeElements[1].bindings[0].propertyBindings", hasSize(13))
                .body("install.typeElements[1].bindings[0].propertyBindings[0].databaseFieldName", equalTo("name"))
                .body("install.typeElements[1].bindings[0].propertyBindings[0].typeElementPropertyDeveloperName", equalTo("Name"))
                .body("install.typeElements[1].bindings[0].propertyBindings[1].databaseFieldName", equalTo("description"))
                .body("install.typeElements[1].bindings[0].propertyBindings[1].typeElementPropertyDeveloperName", equalTo("Description"))
                .body("install.typeElements[1].bindings[0].propertyBindings[2].databaseFieldName", equalTo("account_number"))
                .body("install.typeElements[1].bindings[0].propertyBindings[2].typeElementPropertyDeveloperName", equalTo("Account Number"))
                .body("install.typeElements[1].bindings[0].propertyBindings[3].databaseFieldName", equalTo("type"))
                .body("install.typeElements[1].bindings[0].propertyBindings[3].typeElementPropertyDeveloperName", equalTo("Type"))
                .body("install.typeElements[1].bindings[0].propertyBindings[4].databaseFieldName", equalTo("phone_number"))
                .body("install.typeElements[1].bindings[0].propertyBindings[4].typeElementPropertyDeveloperName", equalTo("Phone Number"))
                .body("install.typeElements[1].bindings[0].propertyBindings[5].databaseFieldName", equalTo("fax"))
                .body("install.typeElements[1].bindings[0].propertyBindings[5].typeElementPropertyDeveloperName", equalTo("Fax"))
                .body("install.typeElements[1].bindings[0].propertyBindings[6].databaseFieldName", equalTo("industry"))
                .body("install.typeElements[1].bindings[0].propertyBindings[6].typeElementPropertyDeveloperName", equalTo("Industry"))
                .body("install.typeElements[1].bindings[0].propertyBindings[7].databaseFieldName", equalTo("website"))
                .body("install.typeElements[1].bindings[0].propertyBindings[7].typeElementPropertyDeveloperName", equalTo("Website"))
                .body("install.typeElements[1].bindings[0].propertyBindings[8].databaseFieldName", equalTo("number_of_employees"))
                .body("install.typeElements[1].bindings[0].propertyBindings[8].typeElementPropertyDeveloperName", equalTo("Number of Employees"))
                .body("install.typeElements[1].bindings[0].propertyBindings[9].databaseFieldName", equalTo("contact_first_name"))
                .body("install.typeElements[1].bindings[0].propertyBindings[9].typeElementPropertyDeveloperName", equalTo("Contact First Name"))
                .body("install.typeElements[1].bindings[0].propertyBindings[10].databaseFieldName", equalTo("contact_last_name"))
                .body("install.typeElements[1].bindings[0].propertyBindings[10].typeElementPropertyDeveloperName", equalTo("Contact Last Name"))
                .body("install.typeElements[1].bindings[0].propertyBindings[11].databaseFieldName", equalTo("cortera_id"))
                .body("install.typeElements[1].bindings[0].propertyBindings[11].typeElementPropertyDeveloperName", equalTo("Cortera ID"))
                .body("install.typeElements[1].bindings[0].propertyBindings[12].databaseFieldName", equalTo("duns_number"))
                .body("install.typeElements[1].bindings[0].propertyBindings[12].typeElementPropertyDeveloperName", equalTo("DUNS Number"))
                .body("install.typeElements[1].elementType", equalTo("TYPE"))
                .body("install.typeElements[1].properties", hasSize(13))
                .body("install.typeElements[1].properties[0].developerName", equalTo("Name"))
                .body("install.typeElements[1].properties[0].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[1].developerName", equalTo("Description"))
                .body("install.typeElements[1].properties[1].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[2].developerName", equalTo("Account Number"))
                .body("install.typeElements[1].properties[2].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[3].developerName", equalTo("Type"))
                .body("install.typeElements[1].properties[3].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[4].developerName", equalTo("Phone Number"))
                .body("install.typeElements[1].properties[4].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[5].developerName", equalTo("Fax"))
                .body("install.typeElements[1].properties[5].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[6].developerName", equalTo("Industry"))
                .body("install.typeElements[1].properties[6].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[7].developerName", equalTo("Website"))
                .body("install.typeElements[1].properties[7].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[8].developerName", equalTo("Number of Employees"))
                .body("install.typeElements[1].properties[8].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[9].developerName", equalTo("Contact First Name"))
                .body("install.typeElements[1].properties[9].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[10].developerName", equalTo("Contact Last Name"))
                .body("install.typeElements[1].properties[10].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[11].developerName", equalTo("Cortera ID"))
                .body("install.typeElements[1].properties[11].contentType", equalTo("ContentString"))
                .body("install.typeElements[1].properties[12].developerName", equalTo("DUNS Number"))
                .body("install.typeElements[1].properties[12].contentType", equalTo("ContentString"))

                .body("install.typeElements[2].developerName", equalTo("account Quarantine Entry"))
                .body("install.typeElements[2].developerSummary", isEmptyOrNullString())
                .body("install.typeElements[2].bindings", hasSize(1))
                .body("install.typeElements[2].bindings[0].developerName", equalTo("account Quarantine Entry"))
                .body("install.typeElements[2].bindings[0].developerSummary", not(isEmptyOrNullString()))
                .body("install.typeElements[2].bindings[0].databaseTableName", equalTo("quarantine-2df80af3-1035-4732-ba12-6c84b0d9f568"))
                .body("install.typeElements[2].bindings[0].propertyBindings", hasSize(10))
                .body("install.typeElements[2].bindings[0].propertyBindings[0].databaseFieldName", equalTo("status"))
                .body("install.typeElements[2].bindings[0].propertyBindings[0].typeElementPropertyDeveloperName", equalTo("Status"))
                .body("install.typeElements[2].bindings[0].propertyBindings[1].databaseFieldName", equalTo("sourceId"))
                .body("install.typeElements[2].bindings[0].propertyBindings[1].typeElementPropertyDeveloperName", equalTo("Source ID"))
                .body("install.typeElements[2].bindings[0].propertyBindings[2].databaseFieldName", equalTo("sourceEntityId"))
                .body("install.typeElements[2].bindings[0].propertyBindings[2].typeElementPropertyDeveloperName", equalTo("Source Entity ID"))
                .body("install.typeElements[2].bindings[0].propertyBindings[3].databaseFieldName", equalTo("createdDate"))
                .body("install.typeElements[2].bindings[0].propertyBindings[3].typeElementPropertyDeveloperName", equalTo("Created Date"))
                .body("install.typeElements[2].bindings[0].propertyBindings[4].databaseFieldName", equalTo("endDate"))
                .body("install.typeElements[2].bindings[0].propertyBindings[4].typeElementPropertyDeveloperName", equalTo("End Date"))
                .body("install.typeElements[2].bindings[0].propertyBindings[5].databaseFieldName", equalTo("transactionId"))
                .body("install.typeElements[2].bindings[0].propertyBindings[5].typeElementPropertyDeveloperName", equalTo("Transaction ID"))
                .body("install.typeElements[2].bindings[0].propertyBindings[6].databaseFieldName", equalTo("cause"))
                .body("install.typeElements[2].bindings[0].propertyBindings[6].typeElementPropertyDeveloperName", equalTo("Cause"))
                .body("install.typeElements[2].bindings[0].propertyBindings[7].databaseFieldName", equalTo("reason"))
                .body("install.typeElements[2].bindings[0].propertyBindings[7].typeElementPropertyDeveloperName", equalTo("Reason"))
                .body("install.typeElements[2].bindings[0].propertyBindings[8].databaseFieldName", equalTo("resolution"))
                .body("install.typeElements[2].bindings[0].propertyBindings[8].typeElementPropertyDeveloperName", equalTo("Resolution"))
                .body("install.typeElements[2].bindings[0].propertyBindings[9].databaseFieldName", equalTo("entity"))
                .body("install.typeElements[2].bindings[0].propertyBindings[9].typeElementPropertyDeveloperName", equalTo("Entity"))
                .body("install.typeElements[2].elementType", equalTo("TYPE"))
                .body("install.typeElements[2].properties", hasSize(10))
                .body("install.typeElements[2].properties[0].developerName", equalTo("Source ID"))
                .body("install.typeElements[2].properties[0].contentType", equalTo("ContentString"))
                .body("install.typeElements[2].properties[1].developerName", equalTo("Source Entity ID"))
                .body("install.typeElements[2].properties[1].contentType", equalTo("ContentString"))
                .body("install.typeElements[2].properties[2].developerName", equalTo("Status"))
                .body("install.typeElements[2].properties[2].contentType", equalTo("ContentString"))
                .body("install.typeElements[2].properties[3].developerName", equalTo("Created Date"))
                .body("install.typeElements[2].properties[3].contentType", equalTo("ContentDateTime"))
                .body("install.typeElements[2].properties[4].developerName", equalTo("End Date"))
                .body("install.typeElements[2].properties[4].contentType", equalTo("ContentDateTime"))
                .body("install.typeElements[2].properties[5].developerName", equalTo("Transaction ID"))
                .body("install.typeElements[2].properties[5].contentType", equalTo("ContentString"))
                .body("install.typeElements[2].properties[6].developerName", equalTo("Cause"))
                .body("install.typeElements[2].properties[6].contentType", equalTo("ContentString"))
                .body("install.typeElements[2].properties[7].developerName", equalTo("Reason"))
                .body("install.typeElements[2].properties[7].contentType", equalTo("ContentString"))
                .body("install.typeElements[2].properties[8].developerName", equalTo("Resolution"))
                .body("install.typeElements[2].properties[8].contentType", equalTo("ContentString"))
                .body("install.typeElements[2].properties[9].developerName", equalTo("Entity"))
                .body("install.typeElements[2].properties[9].contentType", equalTo("ContentObject"))
                .body("install.typeElements[2].properties[9].typeElementDeveloperName", equalTo("account Model"))

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
}
