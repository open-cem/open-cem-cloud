import { useCallback, useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useAuth } from "react-oidc-context";
import { DropdownInputGroup, InputGroup, SwitchInputGroup } from "../inputGroup/InputGroup";
import { Toast } from "primereact/toast";
import { Button } from "primereact/button";
import { Component as ComponentModel, ComponentsService, NullComponent } from "./ComponentsService";
import { presentError, presentSuccess } from "../app/NotificationPresenter";
import { Manufacturer, ManufacturersService, Model } from "./ManufacturersService";
import _ from "lodash";
import { CommunicationChannelListItem, CommunicationChannelService } from "../installation/CommunicationChannelsService";
import { ParameterInput, ParameterMeta } from "../parameterInput/ParameterInput";
import { SmartGridreadyFile, SmartGridreadyService } from "./SmartGridreadyService";
import { ComponentWizardConfiguration } from "../app/WizardSetsService";

const Component = ({inputConfig, uiLoaded} : {inputConfig? : ComponentWizardConfiguration, uiLoaded?: Function}) => {
    const params = useParams();
    const auth = useAuth();
    const navigate = useNavigate();
    const toast = useRef<Toast>(null);

    const [component, setComponent] = useState<ComponentModel>(new NullComponent());
    const [hasChanges, setHasChanges] = useState<boolean>(false);
    const [manufacturers, setManufacturers] = useState<Manufacturer[]>([]);
    const [selectedManufacturer, setSelectedManufacturer] = useState<Manufacturer | undefined>(undefined);
    const [models, setModels] = useState<Model[]>([]);
    const [selectedModel, setSelectedModel] = useState<Model | undefined>(undefined);
    const [channels, setChannels] = useState<CommunicationChannelListItem[]>([]);
    const [selectedChannel, setSelectedChannel] = useState<CommunicationChannelListItem | undefined>(undefined);
    const [parameters, setParameters] = useState<ParameterMeta[]>([]);
    const [isSmartGridready, setIsSmartGridready] = useState<boolean>(false);
    const [smartGridReadyFiles, setSmartGridReadyFiles] = useState<SmartGridreadyFile[]>([]);
    const [selectedSmartGridreadyFile, setSelectedSmartGridreadyFile] = useState<SmartGridreadyFile | undefined>(undefined);

    const installationId: string = inputConfig?.installationId ?? params.installationId ?? "";

    const loadModels = useCallback((manufacturer: Manufacturer, component: ComponentModel) => {
        if (auth.user) {
            new ManufacturersService()
                .loadModels(manufacturer.id, auth.user.access_token)
                .then(models => {
                    setModels(_.orderBy(models, m => m.name));
                    selectModel(component, models);
                })
                .catch(e => presentError('Modelle konnnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        }
    }, [auth.user]);

    useEffect(() => {
        const componentId = inputConfig?.componentId ?? params.componentId;
        if (!auth.user || !componentId) {
            return;
        }

        const selectManufacturer = (component: ComponentModel, manufacturers: Manufacturer[]) => {
            const manufacturer = _.find(manufacturers, m => m.id === component.manufacturerId);
            setSelectedManufacturer(manufacturer);
            if (manufacturer) {
                loadModels(manufacturer, component);
            }
        };

        const service = new ComponentsService();
        const components = service.loadComponent(componentId, auth.user.access_token);
        const manufacturers = new ManufacturersService().loadManufacturers(auth.user.access_token);
        const channels = new CommunicationChannelService().loadCommunicationChannelsByComponentId(componentId, auth.user.access_token);
        const parameterMeta = service.loadComponentParameterMeta(componentId, auth.user.access_token);
        const files = new SmartGridreadyService().loadFiles(auth.user.access_token);

        Promise.all([components, manufacturers, channels, parameterMeta, files])
            .then(([c, ms, cs, ps, fs]) => {
                setComponent(c);
                setManufacturers(_.orderBy(ms, m => m.name));
                setChannels(_.orderBy(cs, c => c.name));
                setParameters(_.orderBy(ps, p => p.label));
                setSmartGridReadyFiles(fs);
                if (isHardwareComponent(c)) {
                    selectManufacturer(c, ms);
                    setSelectedChannel(_.find(cs, x => x.id === c.channelId));
                    setIsSmartGridready(c.smartGridreadyDefinitionId !== null);
                    setSelectedSmartGridreadyFile(_.find(fs, f => f.id === c.smartGridreadyDefinitionId));
                }
                if (uiLoaded) {
                    uiLoaded();
                }
            })
            .catch(e => presentError('Komponente konnnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));
    }, [auth.user, params.componentId, loadModels, inputConfig, uiLoaded]);

    const selectModel = (component: ComponentModel, models: Model[]) => {
        const model = _.find(models, m => m.id === component.modelId);
        setSelectedModel(model);
    };

    const onSelectedManufacturerChanged = (manufacturer: Manufacturer | undefined, component: ComponentModel) => {
        if (component.manufacturerId === manufacturer?.id) {
            return;
        }

        const model = { propertyName: 'modelId', value: undefined };
        setSelectedManufacturer(manufacturer);
        if (manufacturer) {
            onChange([{ propertyName: 'manufacturerId', value: manufacturer.id}, model], component);
        } else {
            onChange([{ propertyName: 'manufacturerId', value: undefined}, model], component);
        }

        setSelectedModel(undefined);
        if (manufacturer) {
            loadModels(manufacturer, component);
        } else {
            setModels([]);
        }
    };

    const onSelectedModelChanged = (model: Model | undefined, component: ComponentModel) => {
        setSelectedModel(model);
        if (model) {
            onChange([{ propertyName: 'modelId', value: model.id}], component);
        } else {
            onChange([{ propertyName: 'modelId', value: undefined}], component);
        }
    };

    const onSelectedSmartGridreadyFileChanged = (file: SmartGridreadyFile | undefined, component: ComponentModel) => {
        setSelectedSmartGridreadyFile(file);
        if (file) {
            onChange([{ propertyName: 'smartGridreadyDefinitionId', value: file.id}], component);
        } else {
            onChange([{ propertyName: 'smartGridreadyDefinitionId', value: undefined}], component);
        }
    };

    const onSelectedChannelChanged = (channel: CommunicationChannelListItem | undefined, component: ComponentModel) => {
        setSelectedChannel(channel);
        onChange([{propertyName: 'channelId', value: channel?.id}], component);
    };

    const onChange = (properties: {propertyName: string, value: any}[], component: ComponentModel) => {
        const i = { ...component } as ComponentModel;
        properties.forEach(p => {
            (i as { [key: string]: any })[p.propertyName] = p.value;
        });
        setComponent(i);
        setHasChanges(true);
    };

    const onParameterChange = (parameterName: string, value: any) => {
        const c = { ...component } as ComponentModel;
        (c.parameter as any)[parameterName] = value;
        setComponent(c);
        setHasChanges(true);
    };

    const onIsSmartGridreadyChanged = (isSmartGridready: boolean, component: ComponentModel) => {
        setIsSmartGridready(isSmartGridready);
        component.manufacturerId = undefined;
        component.modelId = undefined;
        component.smartGridreadyDefinitionId = undefined
        onSelectedSmartGridreadyFileChanged(undefined, component);
        setSelectedManufacturer(undefined);
        setSelectedModel(undefined);
    };

    const onSave = () => {
        if (auth.user && !(component instanceof NullComponent)) {
            new ComponentsService()
                .saveComponent(component, auth.user.access_token)
                .then(_ => {
                    if (toast.current) {
                        presentSuccess(toast.current, 'Komponente wurde gespeichert.');
                    }
                    setHasChanges(false);
                })
                .catch(e => presentError('Komponente konnte nicht gespeichert werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        }
    };

    const isHardwareComponent = (component: ComponentModel) => component.family && component.family !== 'CONTROLLERS';

    const getInputConfig = (field: string) => {
        if (!inputConfig) {
            return {};
        }
        const additionalProps = {isReadOnly: false, disabled: false};
        if (inputConfig.isFieldReadonly(field)) {
            additionalProps["isReadOnly"] = true;
            additionalProps["disabled"] = true;
        }
        return additionalProps;
    }

    return (
        <>
            <Toast ref={toast} />
            <div className="form">
                <InputGroup id="formName" label="Name" {...getInputConfig("name")} value={component?.name} isReadOnly={inputConfig?.isFieldReadonly("name")} changeFn={(e) => onChange([{propertyName: 'name', value: e.target.value}], component)} />
                {inputConfig?.descriptionPanel}
                {
                    isHardwareComponent(component)
                        ?
                        <>
                            <SwitchInputGroup {...getInputConfig("smartgridready")} id="formIsSmartGridReady" label="SmartGridready?" value={isSmartGridready} onChangeFn={(e) => onIsSmartGridreadyChanged(e.target.value ?? false, component)} />
                            {
                                isSmartGridready
                                ? <DropdownInputGroup {...getInputConfig("smartgridready")} id="formSmartGridreadyDefinition" label="XML Datei" value={selectedSmartGridreadyFile} options={smartGridReadyFiles} optionLabel="name" onChangeFn={(e) => onSelectedSmartGridreadyFileChanged(e.value, component)} />
                                : <>
                                    <DropdownInputGroup {...getInputConfig("manufacturer")} id="formManufacturer" label="Hersteller" value={selectedManufacturer} options={manufacturers} optionLabel="name" onChangeFn={(e) => onSelectedManufacturerChanged(e.value, component)} />
                                    <DropdownInputGroup id="formModel" label="Modell" value={selectedModel} options={models} optionLabel="name" onChangeFn={(e) => onSelectedModelChanged(e.value, component)} disabled={!selectedManufacturer || inputConfig?.isFieldReadonly("model")} />
                                </>
                            }
                            <DropdownInputGroup {...getInputConfig("communicationChannel")} id="formChannel" label="Kommunikationskanal" value={selectedChannel} options={channels} optionLabel="name" onChangeFn={(e) => onSelectedChannelChanged(e.target.value, component)} />
                        </>
                        : <></>
                }
                {
                    parameters.length === 0 && component instanceof NullComponent
                    ? <></>
                    : parameters.map(p => <ParameterInput key={p.name} meta={p} value={(component.parameter as any)[p.name]} changeFn={onParameterChange} installationId={installationId} inputConfig={inputConfig}/>)
                }
            </div>
            {
                inputConfig !== undefined
                ? <></>
                :<div className="button-bar">
                    <Button severity="secondary" outlined onClick={() => navigate(`/installations/${installationId}`)}>{ hasChanges ? "Abbrechen" : "Zurück" }</Button>
                    {
                        hasChanges
                            ? <Button onClick={onSave}>Speichern</Button>
                            : <></>
                    }
                </div>
            }
        </>
    )
};

export default Component;