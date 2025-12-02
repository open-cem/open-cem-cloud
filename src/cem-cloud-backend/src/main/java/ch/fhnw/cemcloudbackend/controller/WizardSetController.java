package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.dto.DataItem;
import ch.fhnw.cemcloudbackend.dto.HeaderInfo;
import ch.fhnw.cemcloudbackend.dto.PredefinedSet;
import ch.fhnw.cemcloudbackend.dto.Step;
import ch.fhnw.cemcloudbackend.entity.*;
import ch.fhnw.cemcloudbackend.model.Role;
import ch.fhnw.cemcloudbackend.model.User;
import ch.fhnw.cemcloudbackend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("sets")
public class WizardSetController extends BaseController {

    private final WizardSetRepository sets;
    private final ModelRepository models;
    private final CommunicationChannelTypeRepository channelTypes;
    private final ComponentTypeRepository componentTypes;
    private final ComponentParameterMetaRepository componentParameterMeta;
    private final CommunicationChannelParameterMetaRepository channelParameterMeta;
    private final SmartGridreadyRepository smartGridreadyRepository;

    public WizardSetController(WizardSetRepository sets,
                               ModelRepository models,
                               CommunicationChannelTypeRepository channelTypes,
                               ComponentTypeRepository componentTypes,
                               ComponentParameterMetaRepository componentParameterMeta,
                               CommunicationChannelParameterMetaRepository channelParameterMeta,
                               SmartGridreadyRepository smartGridreadyRepository) {
        this.sets = sets;
        this.models = models;
        this.channelTypes = channelTypes;
        this.componentTypes = componentTypes;
        this.componentParameterMeta = componentParameterMeta;
        this.channelParameterMeta = channelParameterMeta;
        this.smartGridreadyRepository = smartGridreadyRepository;
    }

    @GetMapping()
    public ResponseEntity<Iterable<String>> getAll(JwtAuthenticationToken auth) throws IOException {
        User user = getUser(auth);

        if (!user.isInRole(Role.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(sets.findAll());
    }

    @GetMapping("images")
    public ResponseEntity<Iterable<String>> getAllImages(JwtAuthenticationToken auth) throws IOException {
        User user = getUser(auth);

        if (!user.isInRole(Role.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(sets.findAllImages());
    }

    @GetMapping("missing")
    public ResponseEntity<List<String>> getMissingFiles(JwtAuthenticationToken auth) throws IOException {
        User user = getUser(auth);

        if (!user.isInRole(Role.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<String> neededFiles = new ArrayList<>();

        sets.findAll().forEach(set -> {
            try {
                Optional<byte[]> data = sets.loadFile(set, false);
                if (data.isPresent()) {
                    Yaml yaml = new Yaml(new Constructor(PredefinedSet.class));
                    PredefinedSet predefinedSet = yaml.load(new String(data.get()));

                    neededFiles.addAll(getFiles(predefinedSet));

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Set<String> missingFiles = new HashSet<>();

        for (String file : neededFiles) {
            if(sets.findImage(file).isEmpty() && smartGridreadyRepository.findByFileName(file).isEmpty()) {
                missingFiles.add(file);
            }
        }

        return ResponseEntity.ok(missingFiles.stream().toList());
    }

    private List<String> getFiles(PredefinedSet predefinedSet) {
        List<String> files = new ArrayList<>();
        List<Step> steps = predefinedSet.getSteps().stream().filter(step -> step.getImage() != null).toList();
        files.addAll(steps.stream().map(Step::getImage).toList());
        files.addAll(steps.stream().flatMap(step -> step.getData().stream()).filter(data -> "smartgridready".equals(data.getName())).map(dataItem -> (String)dataItem.getValue()).toList());
        return files;
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<List<String>> post(@RequestParam(name = "file") List<MultipartFile> files,
                                     JwtAuthenticationToken auth) throws IOException {
        User user = getUser(auth);

        if (!user.isInRole(Role.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<String> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String filename = file.getOriginalFilename();

                if (filename == null || filename.isEmpty()) {
                    errors.add("Filename is empty.");
                    continue;
                }

                if (filename.endsWith(".yaml") || filename.endsWith(".yml")) {
                    try {
                        Yaml yaml = new Yaml(new Constructor(PredefinedSet.class));
                        PredefinedSet set = yaml.load(file.getInputStream());

                        errors.addAll(validateSet(set).stream().map(err -> filename + ": " + err).toList());
                    } catch (Exception e) {
                        errors.add("The structure of " + filename + " is invalid. Could not parse it.");
                    }
                }

                if (sets.find(filename).isPresent()) {
                    errors.add("File " + filename + " already exists. Please delete it first.");
                    continue;
                }

                if (errors.isEmpty()) {
                    sets.save(filename, file.getInputStream());
                }
            }
        }

        return ResponseEntity.ok(errors);
    }

    @PostMapping(value = "images", consumes = { "multipart/form-data" })
    public ResponseEntity<Void> postImages(@RequestParam(name = "file") List<MultipartFile> files,
                                     JwtAuthenticationToken auth) throws IOException {
        User user = getUser(auth);

        if (!user.isInRole(Role.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String filename = file.getOriginalFilename();
                if (filename == null || filename.isBlank()) {
                    filename = UUID.randomUUID().toString();
                }

                if (sets.find(filename).isEmpty()) {
                    sets.saveImage(filename, file.getInputStream());
                }
            }
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("all")
    public ResponseEntity<List<PredefinedSet>> getSets() throws IOException {
        List<PredefinedSet> predefinedSets = new ArrayList<>();

        sets.findAll().forEach(set -> {
            try {
                Optional<byte[]> data = sets.loadFile(set, false);
                if (data.isPresent()) {
                    Yaml yaml = new Yaml(new Constructor(PredefinedSet.class));
                    PredefinedSet predefinedSet = yaml.load(new String(data.get()));

                    enrichPredefinedSet(predefinedSet);

                    predefinedSets.add(predefinedSet);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return ResponseEntity.ok(predefinedSets);
    }

    private void enrichPredefinedSet(PredefinedSet predefinedSet) {
        predefinedSet.getSteps().forEach(step -> {
            // Enrich type
            getDataItem(step.getData(), "type").ifPresent(dataItem -> {
                if ("Component".equals(step.getStep_type())) {
                    Optional<ComponentType> typeCode = componentTypes.findByCode((String) dataItem.getValue());
                    typeCode.ifPresent(componentType -> dataItem.setId(componentType.getId()));
                } else if ("CommunicationChannel".equals(step.getStep_type())) {
                    Optional<CommunicationChannelType> typeCode = channelTypes.findByCode((String) dataItem.getValue());
                    typeCode.ifPresent(componentType -> dataItem.setId(componentType.getId()));
                }
            });

            // Enrich smartgridready
            getDataItem(step.getData(), "smartgridready").ifPresent(dataItem -> {
                Optional<SmartGridreadyDefinition> smartGridReady = smartGridreadyRepository.findByFileName((String) dataItem.getValue());
                smartGridReady.ifPresent(sgr -> dataItem.setId(sgr.getId()));
            });

            // Enrich model and manufacturer
            getDataItem(step.getData(), "model").ifPresent(dataItem -> {
                Optional<Model> model = models.findByName((String) dataItem.getValue());
                model.ifPresent(m -> {
                    dataItem.setId(m.getId());
                    getDataItem(step.getData(), "manufacturer").ifPresent(dataItem1 -> dataItem1.setId(m.getManufacturer().getId()));
                });
            });


        });
    }

    @GetMapping("images/{name:.*}")
    public ResponseEntity<byte[]> getImage(@PathVariable String name, JwtAuthenticationToken auth) throws IOException {
        Optional<byte[]> data = sets.loadFile(name, true);

        if (data.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(data.get());
    }

    @DeleteMapping("{name}")
    public ResponseEntity<Void> delete(@PathVariable String name, JwtAuthenticationToken auth) throws IOException {
        User user = getUser(auth);

        if (!user.isInRole(Role.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (name.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (sets.find(name).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        sets.delete(name);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("images/{name}")
    public ResponseEntity<Void> deleteImage(@PathVariable String name, JwtAuthenticationToken auth) throws IOException {
        User user = getUser(auth);

        if (!user.isInRole(Role.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (name.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (sets.findImage(name).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        sets.deleteImage(name);

        return ResponseEntity.noContent().build();
    }


    private List<String> validateSet(PredefinedSet predefinedSet) {
        List<String> errors = new ArrayList<>();
        Set<Integer> stepNumbers = new HashSet<>();

        // Validate Headerinfo
        HeaderInfo headerInfo = predefinedSet.getHeaderinfo();
        errors.addAll(validateHeader(headerInfo));

        // Validate Steps
        List<Step> steps = predefinedSet.getSteps();
        errors.addAll(validateSteps(stepNumbers, steps));

        return errors;
    }

    private List<String> validateSteps(Set<Integer> stepNumbers, List<Step> steps) {
        List<String> errors = new ArrayList<>();
        if (steps == null || steps.isEmpty()) {
            errors.add("Steps cannot be null or empty.");
        } else {
            List<Integer> communicationSteps = steps.stream().filter(step -> "CommunicationChannel".equals(step.getStep_type())).map(Step::getNumber).toList();
            for (Step step : steps) {
                int stepNumber = step.getNumber();
                if (stepNumbers.contains(stepNumber)) {
                    errors.add("Duplicate step number: " + stepNumber + ". Step numbers must be unique.");
                } else {
                    stepNumbers.add(stepNumber);
                }

                String name = step.getName();
                if (name == null || name.isBlank()) {
                    errors.add("Name in step " + stepNumber + " cannot be null or empty.");
                }

                // Step_type validation
                String stepType = step.getStep_type();
                if (!"CommunicationChannel".equals(stepType) && !"Component".equals(stepType)) {
                    errors.add("Invalid step_type: " + stepType + " (Expected 'CommunicationChannel' or 'Component').");
                }

                // Data validation
                List<DataItem> dataItems = step.getData();
                if (dataItems == null || dataItems.isEmpty()) {
                    errors.add("Data in step " + stepNumber + " cannot be null or empty.");
                } else {
                    if ("CommunicationChannel".equals(stepType)) {
                        errors.addAll(validateCommunicationChannelType(stepNumber, dataItems));
                    } else if ("Component".equals(stepType)) {
                        errors.addAll(validateComponent(stepNumber, dataItems, communicationSteps));
                    }
                }
            }
        }
        return errors;
    }

    private List<String> validateComponent(int stepNumber, List<DataItem> dataItems, List<Integer> communicationSteps) {
        List<String> errors = new ArrayList<>();
        Optional<DataItem> manufacturer = getDataItem(dataItems, "manufacturer");
        Optional<DataItem> model = getDataItem(dataItems, "model");

        List<DataItem> remainingItems = new ArrayList<>(dataItems);

        Optional<DataItem> smartgridready = getDataItem(dataItems, "smartgridready");
        if (smartgridready.isEmpty() || smartgridready.get().getValue() == null || !"null".equals(smartgridready.get().getValue())) {
            // Check if manufacturer and model are valid
            if (manufacturer.isEmpty() && model.isPresent()) {
                errors.add("Step " + stepNumber + " requires 'manufacturer' data item.");
            } else if (manufacturer.isPresent() && model.isEmpty()) {
                errors.add("Step " + stepNumber + " requires 'model' data item.");
            } else if (manufacturer.isPresent() && model.isPresent()) {
                Optional<Model> byName = model.get().getValue() instanceof String ? models.findByName((String) model.get().getValue()) : Optional.empty();
                if (byName.isEmpty()) {
                    errors.add("Step " + stepNumber + " has invalid 'model' data item.");
                } else if (!byName.get().getManufacturer().getName().equals(manufacturer.get().getValue())) {
                    errors.add("In step " + stepNumber + " the 'model' data item does not match the 'manufacturer' data item.");
                }
                remainingItems.remove(manufacturer.get());
                remainingItems.remove(model.get());
            }
        }

        // Check if all needed data items are present
        Optional<DataItem> type = getDataItem(dataItems, "type");
        if (type.isEmpty()) {
            errors.add("Step " + stepNumber + " requires 'type' data item.");
        } else {
            remainingItems.remove(type.get());
            Optional<ComponentType> componentTypesByName =  type.get().getValue() instanceof String ? componentTypes.findByCode((String) type.get().getValue()) : Optional.empty();
            if (componentTypesByName.isEmpty()) {
                errors.add("Step " + stepNumber + " has invalid 'type' data item.");
            } else {

                // Check smartgridready
                if (smartgridready.isPresent()) {
                    remainingItems.remove(smartgridready.get());
                    if (smartgridready.get().getValue() instanceof String name) {
                        Optional<SmartGridreadyDefinition> smartGridreadyDefinition = smartGridreadyRepository.findByFileName(name);
                        if (smartGridreadyDefinition.isEmpty()) {
                            errors.add("Step " + stepNumber + " has invalid 'smartgridready' data item.");
                        }
                    }
                }

                Optional<DataItem> channelRef = getDataItem(dataItems, "communicationChannel");
                if (channelRef.isPresent()) {
                    remainingItems.remove(channelRef.get());
                    if (!communicationSteps.contains(channelRef.get().getValue())) {
                        errors.add("Step " + stepNumber + " has invalid 'communicationChannel' data item. The value must be the number of a CommunicationChannel step.");
                    }
                }


                componentParameterMeta.findAllByComponentType(componentTypesByName.get())
                        .forEach(componentParameter -> {
                            Optional<DataItem> dataItem = getDataItem(dataItems, componentParameter.getName());
                            if (dataItem.isEmpty()) {
                                errors.add("Step " + stepNumber + " requires '" + componentParameter.getName() + "' data item.");
                            }else {
                                remainingItems.remove(dataItem.get());
                            }
                        });
                // Check if there are any remaining data items
                if (!remainingItems.isEmpty()) {
                    errors.add("Step " + stepNumber + " has invalid data items: " + remainingItems.stream().map(DataItem::getName).collect(Collectors.joining(", ")));
                }
            }
        }
        return errors;
    }

    private List<String> validateCommunicationChannelType(int stepNumber, List<DataItem> dataItems) {
        List<String> errors = new ArrayList<>();
        List<DataItem> remainingItems = new ArrayList<>(dataItems);

        Optional<DataItem> type = getDataItem(dataItems, "type");
        if (type.isEmpty()) {
            errors.add("Step " + stepNumber + " requires 'type' data item.");
        } else {
            remainingItems.remove(type.get());
            Optional<CommunicationChannelType> channelType = type.get().getValue() instanceof String ? channelTypes.findByCode((String) type.get().getValue()) : Optional.empty();
            if (channelType.isEmpty()) {
                errors.add("Step " + stepNumber + " has invalid 'type' data item.");
            } else {
                // Check if all needed data items are present
                channelParameterMeta.findAllByCommunicationChannelTypeId(channelType.get().getId())
                        .forEach(channelParameter -> {
                            Optional<DataItem> dataItem = getDataItem(dataItems, channelParameter.getName());
                            if (dataItem.isEmpty()) {
                                errors.add("Step " + stepNumber + " requires '" + channelParameter.getName() + "' data item.");
                            }else {
                                remainingItems.remove(dataItem.get());
                            }
                        });
                // Check if there are any remaining data items
                if (!remainingItems.isEmpty()) {
                    errors.add("Step " + stepNumber + " has invalid data items: " + remainingItems.stream().map(DataItem::getName).collect(Collectors.joining(", ")));
                }
            }
        }
        return errors;
    }

    private List<String> validateHeader(HeaderInfo headerInfo) {
        List<String> headerErrors = new ArrayList<>();
        if (headerInfo == null) {
            headerErrors.add("Headerinfo cannot be null.");
        } else {
            String setName = headerInfo.getSetname();
            if (setName == null || setName.trim().isEmpty()) {
                headerErrors.add("Setname in Headerinfo cannot be null or empty.");
            }
        }
        return headerErrors;
    }

    private Optional<DataItem> getDataItem(List<DataItem> dataItems, String name) {
        return dataItems.stream().filter(dataItem -> name.equals(dataItem.getName())).findFirst();
    }
}
