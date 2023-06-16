import { useEffect, useState } from "react";
import { useAuth } from "react-oidc-context";
import { Image } from "primereact/image";
import { Installation, InstallationService } from "../installation/InstallationsService";

const AuthorizedImage = ({ installation, id }: ({ installation: Installation, id?: string })) => {
    const auth = useAuth();
    const [source, setSource] = useState<any>();
    useEffect(() => {
        if (auth.user) {
            new InstallationService()
                .loadInstallationImage(installation, auth.user?.access_token)
                .then(imageUrl => {
                    if (imageUrl === null) {
                        imageUrl = "/img/placeholder.png";
                    }
                    setSource(imageUrl);
                });
        }
    }, [installation, auth]);

    return (
        <Image id={id} src={source} />
    )
};

export default AuthorizedImage;