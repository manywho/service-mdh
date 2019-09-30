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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ElementIdFinderTest {
    @Mock
    UniverseRepository universeRepository;

    @Test
    public void testFindIdFromNameOfElement() {
        Universe.Layout.Model.Element element1 = new Universe.Layout.Model.Element();
        element1.setUniqueId("test_id");
        element1.setName("test_name");

        Universe.Layout.Model.Element element2 = new Universe.Layout.Model.Element();
        element2.setUniqueId("test_id_2");
        element2.setName("test_name_2");

        Universe universe = new Universe()
                .setId(UUID.fromString("12fa66f9-e14d-f642-878f-030b13b64731"))
                .setLayout(new Universe.Layout()
                        .setIdXPath("/item/id")
                        .setModel(new Universe.Layout.Model()
                                .setName("testing")
                                .setElements(Arrays.asList(element1, element2))
                        )
                );

        when(universeRepository
                .find(any(), any(), any(), any()))
                .thenReturn(universe);

        ElementIdFinder elementIdFinder = new ElementIdFinder(universeRepository);
        String testId = elementIdFinder.findIdFromNameOfElement(TestConstants.CONFIGURATION, "universe 1", "test_name");
        String testId2 = elementIdFinder.findIdFromNameOfElement(TestConstants.CONFIGURATION, "universe 1", "test_name_2");

        verify(universeRepository, times(1)).find(any(), any(), any(), any());

        Assert.assertEquals("test_id", testId);
        Assert.assertEquals("test_id_2", testId2);
    }
}
