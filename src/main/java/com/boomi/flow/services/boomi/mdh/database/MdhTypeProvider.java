package com.boomi.flow.services.boomi.mdh.database;

import com.boomi.flow.services.boomi.mdh.ApplicationConfiguration;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineEntryConstants;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordConstants;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.boomi.flow.services.boomi.mdh.universes.UniverseRepository;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.describe.DescribeServiceRequest;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.draw.elements.type.TypeElementBinding;
import com.manywho.sdk.api.draw.elements.type.TypeElementProperty;
import com.manywho.sdk.api.draw.elements.type.TypeElementPropertyBinding;
import com.manywho.sdk.services.types.TypeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MdhTypeProvider implements TypeProvider<ApplicationConfiguration> {
    private final static Logger LOGGER = LoggerFactory.getLogger(MdhTypeProvider.class);

    private final UniverseRepository repository;

    @Inject
    public MdhTypeProvider(UniverseRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean doesTypeExist(ApplicationConfiguration configuration, String name) {
        if (name == null) {
            return false;
        }

        if (name.startsWith("quarantine-")) {
            return true;
        }

        if (name.startsWith("golden-record-")) {
            return true;
        }

        if (name.endsWith("Model")) {
            return true;
        }

        return false;
    }

    static String createModelName(String universeName) {
        return String.format("%s Model", universeName);
    }

    static List<TypeElement> createModelTypes(List<Universe> universes) {
        return universes.stream()
                .filter(universe -> universe.getLayout() != null && universe.getLayout().getModel() != null)
                .map(universe -> {
                    var name = createModelName(universe.getName());

                    List<TypeElementProperty> properties = new ArrayList<>();

                    // TODO: This doesn't add an ID field... is that a problem? It's only in layout->fields, not layout->model->elements
                    for (var field : universe.getLayout().getModel().getElements()) {
                        var contentType = fieldTypeToContentType(field.getType());
                        if (contentType == null) {
                            // We don't want to convert this element as it's an unsupported type
                            continue;
                        }

                        properties.add(new TypeElementProperty(field.getPrettyName(), contentType));
                    }

                    List<TypeElementPropertyBinding> propertyBindings = new ArrayList<>();

                    // This links the properties found from the "fields" with the stuff in "elements" (which gives us the actual column name?)
                    for (var element : universe.getLayout().getModel().getElements()) {
                        var contentType = fieldTypeToContentType(element.getType());
                        if (contentType == null) {
                            // We don't want to convert this element as it's an unsupported type
                            continue;
                        }

                        propertyBindings.add(new TypeElementPropertyBinding(element.getPrettyName(), element.getName()));
                    }

                    List<TypeElementBinding> bindings = new ArrayList<>();
                    bindings.add(new TypeElementBinding(name, "The model for the " + universe.getName() + " universe", universe.getId().toString(), propertyBindings));

                    return new TypeElement(name, properties, bindings);
                })
                .collect(Collectors.toList());
    }

    static List<TypeElement> createGoldenRecordTypes(List<Universe> universes) {
        return universes.stream()
                .filter(universe -> universe.getLayout() != null && universe.getLayout().getModel() != null)
                .map(universe -> {
                    var name = String.format("%s Golden Record", universe.getName());

                    List<TypeElementProperty> properties = new ArrayList<>();

                    // TODO: Deduplicate this and below
                    for (var field : universe.getLayout().getModel().getElements()) {
                        var contentType = fieldTypeToContentType(field.getType());
                        if (contentType == null) {
                            // We don't want to convert this element as it's an unsupported type
                            continue;
                        }

                        properties.add(new TypeElementProperty(field.getPrettyName(), contentType));
                    }

                    // These properties are all for the request, only for filtering
                    properties.add(new TypeElementProperty(GoldenRecordConstants.SOURCE_ID, ContentType.String));
                    properties.add(new TypeElementProperty(GoldenRecordConstants.CREATED_DATE, ContentType.DateTime));
                    properties.add(new TypeElementProperty(GoldenRecordConstants.UPDATED_DATE, ContentType.DateTime));

                    List<TypeElementPropertyBinding> propertyBindings = new ArrayList<>();

                    // This links the properties found from the "fields" with the stuff in "elements" (which gives us the actual column name?)
                    for (var element : universe.getLayout().getModel().getElements()) {
                        var contentType = fieldTypeToContentType(element.getType());
                        if (contentType == null) {
                            // We don't want to convert this element as it's an unsupported type
                            continue;
                        }

                        propertyBindings.add(new TypeElementPropertyBinding(element.getPrettyName(), element.getName()));
                    }

                    // These properties are all for the request, only for filtering
                    propertyBindings.add(new TypeElementPropertyBinding(GoldenRecordConstants.SOURCE_ID, GoldenRecordConstants.SOURCE_ID_FIELD));
                    propertyBindings.add(new TypeElementPropertyBinding(GoldenRecordConstants.CREATED_DATE, GoldenRecordConstants.CREATED_DATE_FIELD));
                    propertyBindings.add(new TypeElementPropertyBinding(GoldenRecordConstants.UPDATED_DATE, GoldenRecordConstants.UPDATED_DATE_FIELD));

                    // TODO: Pretty sure this is only required because of a bug in the Engine
                    var tableName = "golden-record-" + universe.getId().toString();

                    List<TypeElementBinding> bindings = new ArrayList<>();
                    bindings.add(new TypeElementBinding(name, "The structure of a golden record for the " + universe.getName() + " universe", tableName, propertyBindings));

                    return new TypeElement(name, properties, bindings);
                })
                .collect(Collectors.toList());
    }

    static List<TypeElement> createMatchEntitiesTypes(List<Universe> universes) {
        return new ArrayList<>();
    }

    static List<TypeElement> createQuarantineEntriesTypes(List<Universe> universes) {
        return universes.stream()
                .map(universe -> {
                    var name = String.format("%s Quarantine Entry", universe.getName());

                    List<TypeElementProperty> properties = new ArrayList<>();

                    // These properties are all for the request, mostly used for filtering
                    properties.add(new TypeElementProperty(QuarantineEntryConstants.SOURCE_ID, ContentType.String));
                    properties.add(new TypeElementProperty(QuarantineEntryConstants.SOURCE_ENTITY_ID, ContentType.String));
                    properties.add(new TypeElementProperty(QuarantineEntryConstants.STATUS, ContentType.String));

                    // These properties are all for the response
                    properties.add(new TypeElementProperty(QuarantineEntryConstants.CREATED_DATE, ContentType.DateTime));
                    properties.add(new TypeElementProperty(QuarantineEntryConstants.END_DATE, ContentType.DateTime));
                    properties.add(new TypeElementProperty(QuarantineEntryConstants.TRANSACTION_ID, ContentType.String));

                    properties.add(new TypeElementProperty(QuarantineEntryConstants.CAUSE, ContentType.String));
                    properties.add(new TypeElementProperty(QuarantineEntryConstants.REASON, ContentType.String));
                    properties.add(new TypeElementProperty(QuarantineEntryConstants.RESOLUTION, ContentType.String));
                    properties.add(new TypeElementProperty(QuarantineEntryConstants.ENTITY, ContentType.Object, createModelName(universe.getName())));

                    List<TypeElementPropertyBinding> propertyBindings = new ArrayList<>();

                    propertyBindings.add(new TypeElementPropertyBinding(QuarantineEntryConstants.STATUS, QuarantineEntryConstants.STATUS_FIELD));
                    propertyBindings.add(new TypeElementPropertyBinding(QuarantineEntryConstants.SOURCE_ID, QuarantineEntryConstants.SOURCE_ID_FIELD));
                    propertyBindings.add(new TypeElementPropertyBinding(QuarantineEntryConstants.SOURCE_ENTITY_ID, QuarantineEntryConstants.SOURCE_ENTITY_ID_FIELD));
                    propertyBindings.add(new TypeElementPropertyBinding(QuarantineEntryConstants.CREATED_DATE, QuarantineEntryConstants.CREATED_DATE_FIELD));
                    propertyBindings.add(new TypeElementPropertyBinding(QuarantineEntryConstants.END_DATE, QuarantineEntryConstants.END_DATE_FIELD));
                    propertyBindings.add(new TypeElementPropertyBinding(QuarantineEntryConstants.TRANSACTION_ID, QuarantineEntryConstants.TRANSACTION_ID_FIELD));
                    propertyBindings.add(new TypeElementPropertyBinding(QuarantineEntryConstants.CAUSE, QuarantineEntryConstants.CAUSE_FIELD));
                    propertyBindings.add(new TypeElementPropertyBinding(QuarantineEntryConstants.REASON, QuarantineEntryConstants.REASON_FIELD));
                    propertyBindings.add(new TypeElementPropertyBinding(QuarantineEntryConstants.RESOLUTION, QuarantineEntryConstants.RESOLUTION_FIELD));
                    propertyBindings.add(new TypeElementPropertyBinding(QuarantineEntryConstants.ENTITY, QuarantineEntryConstants.ENTITY_FIELD));

                    List<TypeElementBinding> bindings = new ArrayList<>();

                    bindings.add(new TypeElementBinding(name, "Something about Quarantine Entries", "quarantine-" + universe.getId(), propertyBindings));

                    TypeElement typeElement = new TypeElement();
                    typeElement.setBindings(bindings);
                    typeElement.setDeveloperName(name);
                    typeElement.setProperties(properties);

                    return typeElement;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TypeElement> describeTypes(ApplicationConfiguration configuration, DescribeServiceRequest request) {
        // TODO: Get this bug fixed in the system flows
        if (request.hasConfigurationValues() == false) {
            return new ArrayList<>();
        }

        var universes = repository.findAll(configuration.getAtomHostname(), configuration.getAtomUsername(), configuration.getAtomPassword());

        var modelTypes = createModelTypes(universes);
        var goldenRecordTypes = createGoldenRecordTypes(universes);
        var matchEntitiesTypes = createMatchEntitiesTypes(universes);
        var quarantineEntryTypes = createQuarantineEntriesTypes(universes);

        var types = new ArrayList<TypeElement>();
        types.addAll(modelTypes);
        types.addAll(goldenRecordTypes);
        types.addAll(matchEntitiesTypes);
        types.addAll(quarantineEntryTypes);

        return types;
    }

    private static ContentType fieldTypeToContentType(String type) {
        switch (type) {
            case "STRING":
                return ContentType.String;
            case "INTEGER":
                return ContentType.Number;
            case "CONTAINER":
            case "ENUMERATION":
            default:
                LOGGER.warn("Encountered an unsupported element type of {}", type);

                return null;
        }
    }
}
