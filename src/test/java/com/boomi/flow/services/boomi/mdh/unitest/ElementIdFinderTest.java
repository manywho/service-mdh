package com.boomi.flow.services.boomi.mdh.unitest;

import com.boomi.flow.services.boomi.mdh.TestConstants;
import com.boomi.flow.services.boomi.mdh.records.ElementIdFinder;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.boomi.flow.services.boomi.mdh.universes.UniverseRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ElementIdFinderTest {
    @Mock
    UniverseRepository universeRepository;

    @Test
    public void testFindIdFromNameOfElement() {
        var element = new Universe.Layout.Model.Element();
        element.setUniqueId("test_id");
        element.setName("test_name");

        var universe = new Universe()
                .setId(UUID.fromString("12fa66f9-e14d-f642-878f-030b13b64731"))
                .setLayout(new Universe.Layout()
                        .setIdXPath("/item/id")
                        .setModel(new Universe.Layout.Model()
                                .setName("testing")
                                .setElements(List.of(element))
                        )
                );

        when(universeRepository
                .find(any(), any(), any(), any()))
                .thenReturn(universe);

        var elementIdFinder = new ElementIdFinder(universeRepository);
        var testId = elementIdFinder.findIdFromNameOfElement(TestConstants.CONFIGURATION, "universe 1", "test_name");

        Assert.assertEquals("test_id", testId);
    }
}
