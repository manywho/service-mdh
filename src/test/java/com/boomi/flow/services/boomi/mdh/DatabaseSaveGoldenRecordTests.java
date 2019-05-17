package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import com.boomi.flow.services.boomi.mdh.database.MdhRawDatabase;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineRepository;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordRepository;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.api.run.elements.type.Property;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseSaveGoldenRecordTests {
    @Mock
    private MdhClient client;

    private ObjectDataType objectDataType = new ObjectDataType()
            .setDeveloperName("golden-record-12fa66f9-e14d-f642-878f-030b13b64731");

    @Test
    public void testSaveWithSingleNewObjectReturnsObjectWithID() {
        when(client.findUniverse(any(), any(), any(), eq("12fa66f9-e14d-f642-878f-030b13b64731")))
                .thenReturn(new Universe()
                    .setId(UUID.fromString("12fa66f9-e14d-f642-878f-030b13b64731"))
                    .setLayout(new Universe.Layout()
                            .setIdXPath("/item/id")
                            .setModel(new Universe.Layout.Model()
                                    .setName("testing")
                            )
                    )
                );

        MObject object = new MObject(objectDataType.getDeveloperName());
        object.setExternalId(null);
        object.getProperties().add(new Property("id", (Object) null));
        object.getProperties().add(new Property("___sourceId", "TESTING"));
        object.getProperties().add(new Property("field 1 1", "some value 1"));
        object.getProperties().add(new Property("field 2 1", "some value 2"));
        object.getProperties().add(new Property("field 3 1", "some value 3"));

        MObject result = new MdhRawDatabase(new QuarantineRepository(client), new GoldenRecordRepository(client))
                .create(TestConstants.CONFIGURATION, objectDataType, object);

        assertThat(result, not(nullValue()));
        assertThat(result.getDeveloperName(), equalTo(objectDataType.getDeveloperName()));
        assertThat(result.getExternalId(), not(isEmptyOrNullString()));
        assertThat(result.getProperties(), hasSize(5));
        assertThat(result.getProperties().get(0).getDeveloperName(), equalTo("id"));
        assertThat(result.getProperties().get(0).getContentValue(), not(isEmptyOrNullString()));
        assertThat(result.getProperties().get(1).getDeveloperName(), equalTo("___sourceId"));
        assertThat(result.getProperties().get(1).getContentValue(), equalTo("TESTING"));
        assertThat(result.getProperties().get(2).getDeveloperName(), equalTo("field 1 1"));
        assertThat(result.getProperties().get(2).getContentValue(), equalTo("some value 1"));
        assertThat(result.getProperties().get(3).getDeveloperName(), equalTo("field 2 1"));
        assertThat(result.getProperties().get(3).getContentValue(), equalTo("some value 2"));
        assertThat(result.getProperties().get(4).getDeveloperName(), equalTo("field 3 1"));
        assertThat(result.getProperties().get(4).getContentValue(), equalTo("some value 3"));
    }
}
