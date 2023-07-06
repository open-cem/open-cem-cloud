package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.dto.ComponentUpdateRequest;
import ch.fhnw.cemcloudbackend.entity.*;
import ch.fhnw.cemcloudbackend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ComponentControllerTest {

    final UUID userId = UUID.randomUUID();
    final String userEmail = "someone@example.com";

    private ComponentController controller;

    @Mock
    private ComponentRepository components;
    @Mock
    private ComponentTypeRepository types;
    @Mock
    private InstallationRepository installations;
    @Mock
    private ManufacturerRepository manufacturers;
    @Mock
    private ComponentParameterMetaRepository meta;
    @Mock
    private SmartGridreadyRepository sgrdefinitions;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new ComponentController(components, types, installations, manufacturers, meta, sgrdefinitions);
    }

    @Test
    public void getComponentShouldBeEqual() {
        HardwareComponent component = mockComponent();
        doReturn(Optional.of(component)).when(components).findById(any());
        JwtAuthenticationToken auth = mockAuth();

        var dto = controller.get(UUID.randomUUID(), auth);

        assertThat(dto.getBody().manufacturerId()).isEqualTo(component.getManufacturer().getId());
        assertThat(dto.getBody().modelId()).isEqualTo(component.getModel().getId());
    }

    @Test void getComponentNoModelShouldBeEqual() {
        HardwareComponent component = mockComponent();
        component.setModel(null);
        doReturn(Optional.of(component)).when(components).findById(any());
        JwtAuthenticationToken auth = mockAuth();

        var dto = controller.get(UUID.randomUUID(), auth);

        assertThat(dto.getBody().manufacturerId()).isEqualTo(component.getManufacturer().getId());
        assertThat(dto.getBody().modelId()).isNull();
    }

    @Test
    void getComponentNoManufacturerShouldBeEqual() {
        HardwareComponent component = mockComponent();
        component.setModel(null);
        component.setManufacturer(null);
        doReturn(Optional.of(component)).when(components).findById(any());
        JwtAuthenticationToken auth = mockAuth();

        var dto = controller.get(UUID.randomUUID(), auth);

        assertThat(dto.getBody().manufacturerId()).isNull();
        assertThat(dto.getBody().modelId()).isNull();
    }

    @Test
    void saveComponentShouldCallSave() {
        Model model = mockMockModel();
        Manufacturer manufacturer = mockManufacturer();
        manufacturer.setModels(Set.of(model));
        HardwareComponent component = mockComponent();
        component.setModel(model);
        component.setManufacturer(manufacturer);
        doReturn(Optional.of(component)).when(components).findById(any());
        doReturn(Optional.of(manufacturer)).when(manufacturers).findById(any());
        JwtAuthenticationToken auth = mockAuth();

        controller.put(component.getId(), new ComponentUpdateRequest(component.getName(),
                component.getManufacturer().getId(), component.getModel().getId(), null, null, null),
                auth);

        verify(components, times(1)).save(any());
    }

    @Test
    void saveComponentWithNoModelShouldCallSave() {
        Manufacturer manufacturer = mockManufacturer();
        HardwareComponent component = mockComponent();
        component.setModel(null);
        component.setManufacturer(manufacturer);
        doReturn(Optional.of(component)).when(components).findById(any());
        doReturn(Optional.of(manufacturer)).when(manufacturers).findById(any());
        JwtAuthenticationToken auth = mockAuth();

        controller.put(component.getId(), new ComponentUpdateRequest(component.getName(),
                component.getManufacturer().getId(), null, null, null, null),
                auth);

        verify(components, times(1)).save(any());
    }

    @Test
    void saveComponentWithNewModelShouldCallSave() {
        Model model = mockMockModel();
        Manufacturer manufacturer = mockManufacturer();
        manufacturer.setModels(Set.of(model));
        HardwareComponent component = mockComponent();
        component.setModel(null);
        component.setManufacturer(manufacturer);
        doReturn(Optional.of(component)).when(components).findById(any());
        doReturn(Optional.of(manufacturer)).when(manufacturers).findById(any());
        JwtAuthenticationToken auth = mockAuth();

        controller.put(component.getId(), new ComponentUpdateRequest(component.getName(),
                component.getManufacturer().getId(), model.getId(), null, null, null),
                auth);

        verify(components, times(1)).save(any());
    }

    @Test
    void saveComponentWithNoManufacturerShouldCallSave() {
        Manufacturer manufacturer = mockManufacturer();
        HardwareComponent component = mockComponent();
        component.setModel(null);
        component.setManufacturer(manufacturer);
        doReturn(Optional.of(component)).when(components).findById(any());
        doReturn(Optional.of(manufacturer)).when(manufacturers).findById(any());
        JwtAuthenticationToken auth = mockAuth();

        controller.put(component.getId(), new ComponentUpdateRequest(component.getName(),null, null, null, null, null), auth);

        verify(components, times(1)).save(any());
    }

    @Test
    void saveComponentWithNewManufacturerShouldCallSave() {
        Model model = mockMockModel();
        Manufacturer manufacturer = mockManufacturer();
        manufacturer.setModels(Set.of(model));
        HardwareComponent component = mockComponent();
        component.setModel(null);
        component.setManufacturer(null);
        doReturn(Optional.of(component)).when(components).findById(any());
        doReturn(Optional.of(manufacturer)).when(manufacturers).findById(any());
        JwtAuthenticationToken auth = mockAuth();

        controller.put(component.getId(), new ComponentUpdateRequest(component.getName(),
                manufacturer.getId(), model.getId(), null, null, null),
                auth);

        verify(components, times(1)).save(any());
    }

    private HardwareComponent mockComponent() {
        HardwareComponent component = new HardwareComponent();
        component.setId(UUID.randomUUID());
        component.setManufacturer(mockManufacturer());
        component.setModel(mockMockModel());
        component.setName("Mock Component");
        component.setType(mockComponentType());
        component.setInstallation(mockInstallation());

        return component;
    }

    private Manufacturer mockManufacturer() {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(UUID.randomUUID());

        return manufacturer;
    }

    private Model mockMockModel() {
        Model model = new Model();
        model.setId(UUID.randomUUID());

        return  model;
    }

    private ComponentType mockComponentType() {
        ComponentType type = new ComponentType();
        type.setId(UUID.randomUUID());
        type.setName("Mock type");
        type.setCode("Mock code");
        type.setComponentType(mockComponentFamily());

        return type;
    }

    private ComponentFamily mockComponentFamily() {
        ComponentFamily family = new ComponentFamily();
        family.setId(UUID.randomUUID());
        family.setName("Mock family");

        return family;
    }

    private Installation mockInstallation() {
        Installation installation = new Installation();
        installation.setOutOfSync(false);
        installation.setInstallationAccesses(Set.of(mockInstallationAccess(installation)));

        return installation;
    }

    private InstallationAccess mockInstallationAccess(Installation installation) {
        InstallationAccess access = new InstallationAccess();
        access.setId(UUID.randomUUID());
        access.setInstallation(installation);
        access.setUserId(userId);
        access.setUserEmail(userEmail);

        return  access;
    }

    private JwtAuthenticationToken mockAuth() {
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        when(auth.getName()).thenReturn(userId.toString());
        Map<String, Object> userAttributes = Map.of("email", userEmail);
        when(auth.getTokenAttributes()).thenReturn(userAttributes);

        return auth;
    }
}
