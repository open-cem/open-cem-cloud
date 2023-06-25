import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useAuth } from "react-oidc-context";
import { InputGroup } from "../inputGroup/InputGroup";
import { Toast } from "primereact/toast";
import { Button } from "primereact/button";
import { Component as ComponentModel, ComponentsService, NullComponent } from "./ComponentsService";
import { presentError, presentSuccess } from "../app/NotificationPresenter";

const Component = () => {
    const params = useParams();
    const auth = useAuth();
    const navigate = useNavigate();
    const toast = useRef<Toast>(null);

    const [component, setComponent] = useState<ComponentModel>(new NullComponent());
    const [hasChanges, setHasChanges] = useState<boolean>(false);

    useEffect(() => {
        const componentId = params.componentId;
        if (!auth.user || !componentId) {
            return;
        }

        new ComponentsService().loadComponent(componentId, auth.user?.access_token)
            .then(setComponent)
            .catch(e => presentError('Komponente konnnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));
    }, [auth.user, params.componentId]);

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

    return (
        <>
            <Toast ref={toast} />
            <div className="form">
                <InputGroup id="formName" label="Name" value={component?.name} changeFn={(e) => onChange('name', e.target.value)} />
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