package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.dto.ComponentUpdateRequest;
import ch.fhnw.cemcloudbackend.entity.*;
import ch.fhnw.cemcloudbackend.repository.ComponentRepository;
import ch.fhnw.cemcloudbackend.repository.ComponentTypeRepository;
import ch.fhnw.cemcloudbackend.repository.InstallationRepository;
import ch.fhnw.cemcloudbackend.repository.ManufacturerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ComponentControllerTest {

    private ComponentController controller;

    @Mock
    private ComponentRepository components;
    @Mock
    private ComponentTypeRepository types;
    @Mock
    private InstallationRepository installations;
    @Mock
    private ManufacturerRepository manufacturers;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new ComponentController(components, types, installations, manufacturers);
    }

    @Test
    public void getComponentShouldBeEqual() {
        HardwareComponent component = mockComponent();
        doReturn(Optional.of(component)).when(components).findById(any());

        var dto = controller.get(UUID.randomUUID());

        assertThat(dto.getBody().manufacturerId()).isEqualTo(component.getManufacturer().getId());
        assertThat(dto.getBody().modelId()).isEqualTo(component.getModel().getId());
    }

    @Test void getComponentNoModelShouldBeEqual() {
        HardwareComponent component = mockComponent();
        component.setModel(null);
        doReturn(Optional.of(component)).when(components).findById(any());

        var dto = controller.get(UUID.randomUUID());

        assertThat(dto.getBody().manufacturerId()).isEqualTo(component.getManufacturer().getId());
        assertThat(dto.getBody().modelId()).isNull();
    }

    @Test
    void getComponentNoManufacturerShouldBeEqual() {
        HardwareComponent component = mockComponent();
        component.setModel(null);
        component.setManufacturer(null);
        doReturn(Optional.of(component)).when(components).findById(any());

        var dto = controller.get(UUID.randomUUID());

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

        controller.put(component.getId(), new ComponentUpdateRequest(component.getName(),
                component.getManufacturer().getId(), component.getModel().getId()));

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

        controller.put(component.getId(), new ComponentUpdateRequest(component.getName(),
                component.getManufacturer().getId(), null));

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

        controller.put(component.getId(), new ComponentUpdateRequest(component.getName(),
                component.getManufacturer().getId(), model.getId()));

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

        controller.put(component.getId(), new ComponentUpdateRequest(component.getName(),null, null));

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

        controller.put(component.getId(), new ComponentUpdateRequest(component.getName(),
                manufacturer.getId(), model.getId()));

        verify(components, times(1)).save(any());
    }

    private HardwareComponent mockComponent() {
        HardwareComponent component = new HardwareComponent();
        component.setId(UUID.randomUUID());
        component.setManufacturer(mockManufacturer());
        component.setModel(mockMockModel());
        component.setName("Mock Component");
        component.setType(mockComponentType());

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
}
