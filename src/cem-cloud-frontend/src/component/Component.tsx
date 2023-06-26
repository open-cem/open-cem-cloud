import { useCallback, useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useAuth } from "react-oidc-context";
import { DropdownInputGroup, InputGroup } from "../inputGroup/InputGroup";
import { Toast } from "primereact/toast";
import { Button } from "primereact/button";
import { Component as ComponentModel, ComponentsService, NullComponent } from "./ComponentsService";
import { presentError, presentSuccess } from "../app/NotificationPresenter";
import { Manufacturer, ManufacturersService, Model } from "./ManufacturersService";
import _ from "lodash";

const Component = () => {
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
        const componentId = params.componentId;
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

        const components = new ComponentsService().loadComponent(componentId, auth.user?.access_token);
        const manufacturers = new ManufacturersService().loadManufacturers(auth.user?.access_token);

        Promise.all([components, manufacturers])
            .then(([c, ms]) => {
                setComponent(c);
                setManufacturers(_.orderBy(ms, m => m.name));
                if (isHardwareComponent(c)) {
                    selectManufacturer(c, ms);
                }
            })
            .catch(e => presentError('Komponente konnnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));
    }, [auth.user, params.componentId, loadModels]);

    const selectModel = (component: ComponentModel, models: Model[]) => {
        const model = _.find(models, m => m.id === component.modelId);
        setSelectedModel(model);
    };

    const onSelectedManufacturerChanged = (manufacturer: Manufacturer | undefined, component: ComponentModel) => {
        if (component.manufacturerId === manufacturer?.id) {
            return;
        }

        setSelectedManufacturer(manufacturer);
        const c = { ...component };
        if (manufacturer) {
            c.manufacturerId = manufacturer.id;
        } else {
            c.manufacturerId = undefined;
        }
        c.modelId = undefined;
        setComponent(c);
        setHasChanges(true);
        
        setSelectedModel(undefined);
        if (manufacturer) {
            loadModels(manufacturer, component);
        } else {
            setModels([]);
        }
    };

    const onSelectedModelChanged = (model: Model | undefined, component: ComponentModel) => {
        setSelectedModel(model);
        const c = { ...component };
        if (model) {
            c.modelId = model.id;
        } else {
            c.modelId = undefined;
        }
        setComponent(c);
        setHasChanges(true);
    };

    const onChange = (propertyName: string, value: any) => {
        const i = { ...component } as ComponentModel;
        (i as { [key: string]: any })[propertyName] = value;
        setComponent(i);
        setHasChanges(true);
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

    return (
        <>
            <Toast ref={toast} />
            <div className="form">
                <InputGroup id="formName" label="Name" value={component?.name} changeFn={(e) => onChange('name', e.target.value)} />
                {
                    isHardwareComponent(component)
                        ?
                        <>
                            <DropdownInputGroup id="formManufacturer" label="Hersteller" value={selectedManufacturer} options={manufacturers} optionLabel="name" onChangeFn={(e) => onSelectedManufacturerChanged(e.value, component)} />
                            <DropdownInputGroup id="formModel" label="Modell" value={selectedModel} options={models} optionLabel="name" onChangeFn={(e) => onSelectedModelChanged(e.value, component)} disabled={!selectedManufacturer} />
                        </>
                        : <></>
                }
            </div>
            <div className="button-bar">
                <Button severity="secondary" outlined onClick={() => navigate(-1)}>Abbrechen</Button>
                {
                    hasChanges
                        ? <Button onClick={onSave}>Speichern</Button>
                        : <></>
                }
            </div>
        </>
    )
};

export default Component;