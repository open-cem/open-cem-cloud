import { SetStateAction, useEffect, useState } from "react";
import { useAuth } from "react-oidc-context";
import { Image } from "primereact/image";
import { Installation, InstallationService } from "../installation/InstallationsService";
import WizardSetsService from "../app/WizardSetsService";

interface Common {
    id?: string;
    className?: string;
}

interface AuthorizedImageInstallation extends Common {
    installation: Installation;
}

interface AuthorizedImageSet extends Common {
    name: string | undefined;
    id?: string;
}

function AuthorizedImage(authorizedImageInstallation: AuthorizedImageInstallation): JSX.Element;
function AuthorizedImage(authorizedImageSet: AuthorizedImageSet): JSX.Element;
function AuthorizedImage(authorizedImageInstallationOrSet: AuthorizedImageInstallation | AuthorizedImageSet): JSX.Element {
        const auth = useAuth();
        const [source, setSource] = useState<string>("/img/placeholder.png");

        useEffect(() => {
            if (auth.user) {
                let promise: Promise<string | null>;

                if ("installation" in authorizedImageInstallationOrSet) {
                    promise = new InstallationService().loadInstallationImage(authorizedImageInstallationOrSet.installation, auth.user?.access_token);
                } else {
                    if (authorizedImageInstallationOrSet.name === undefined || authorizedImageInstallationOrSet.name === null) {
                        setSource("/img/placeholder.png");
                        return;
                    }

                    promise = new WizardSetsService().loadSetImage(authorizedImageInstallationOrSet.name, auth.user?.access_token)
                }

                promise.then((imageUrl: SetStateAction<string> | null) => {
                        if (imageUrl === null) {
                            imageUrl = "/img/placeholder.png";
                        }
                        setSource(imageUrl);
                    });
            }
        }, [authorizedImageInstallationOrSet, auth]);
    
        return (
            <Image className={authorizedImageInstallationOrSet.className} id={authorizedImageInstallationOrSet.id} src={source} />
        )
}





export default AuthorizedImage;