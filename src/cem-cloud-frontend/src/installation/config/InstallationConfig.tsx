import { useAuth } from "react-oidc-context";
import { useParams } from "react-router-dom";
import { InstallationService, Installation as InstallationModel } from "../InstallationsService";
import { useEffect, useState } from "react";
import { InputGroup, PictureInputGroup } from "../../inputGroup/InputGroup";

const InstallationConfig = () => {
    const params = useParams<string>();
    const auth = useAuth();

    const [installation, setInstallation] = useState<InstallationModel | null>(null);

    useEffect(() => {
        const installationId = params.installationId;
        if (installationId === undefined || !auth.user) {
            return;
        }

        new InstallationService().loadInstallation(installationId, auth.user.access_token)
            .then(setInstallation)
            .catch(console.error);
    }, [auth, params.installationId]);

    const onChange = (value: string) => {

    };

    return (
        <div className="form">
            <InputGroup id="name" label="Name" value={installation?.name} setValue={onChange} />
            <InputGroup id="id" label="ID der Installation" value={installation?.serialNumber} setValue={onChange} />
            <PictureInputGroup id="image" label="Anzeigbild" />
        </div>
    )
};

export default InstallationConfig;