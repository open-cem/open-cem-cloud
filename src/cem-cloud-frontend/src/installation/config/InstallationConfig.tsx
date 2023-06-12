import { useAuth } from "react-oidc-context";
import { useNavigate, useParams } from "react-router-dom";
import { InstallationService, Installation, NullInstallation } from "../InstallationsService";
import { useEffect, useRef, useState } from "react";
import { InputGroup, PictureInputGroup } from "../../inputGroup/InputGroup";
import { Button } from "primereact/button";
import { presentError, presentSuccess } from "../../app/NotificationPresenter";
import { Toast } from "primereact/toast";

const InstallationConfig = () => {
    const params = useParams<string>();
    const navigate = useNavigate();
    const auth = useAuth();
    const toast = useRef<Toast>(null);

    const [installation, setInstallation] = useState<Installation>(new NullInstallation());
    const [hasChanges, setHasChanges] = useState<boolean>(false);

    useEffect(() => {
        const installationId = params.installationId;
        if (installationId === undefined || !auth.user) {
            return;
        }

        new InstallationService().loadInstallation(installationId, auth.user.access_token)
            .then(setInstallation)
            .catch(e => presentError('Installation konnnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));
    }, [auth, params.installationId]);

    const onChange = (propertyName: string, value: any) => {
        const i = { ...installation } as Installation;
        (i as {[key: string]: any})[propertyName] = value;
        setInstallation(i);
        setHasChanges(true);
    };

    const onSave = () => {
        if (auth.user && !(installation instanceof NullInstallation)) {
            new InstallationService()
                .saveInstallation(installation, auth.user?.access_token)
                .then(_ => {
                    if (toast.current) {
                        presentSuccess(toast.current, 'Installation wurde gespeichert.');
                    }
                    setHasChanges(false);
                })
                .catch(e => presentError('Installation konnte nicht gespeichert werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        }
    };

    return (
        <>
            <Toast ref={toast} />
            <div className="form">
                <InputGroup id="formName" label="Name" value={installation?.name} changeFn={(e) => onChange('name', e.target.value)} />
                <InputGroup id="id" label="ID der Installation" value={installation?.serialNumber} changeFn={(e) => onChange('serialNumber', e.target.value)} />
                <PictureInputGroup id="image" label="Anzeigbild" />
                <div className="button-bar">
                    <Button severity="secondary" outlined onClick={() => navigate(-1)}>Abbrechen</Button>
                    {
                        hasChanges 
                            ? <Button onClick={onSave}>Speichern</Button>       
                            : <></>
                    }
                </div>
            </div>
        </>
    )
};

export default InstallationConfig;