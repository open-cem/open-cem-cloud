import { ApiService } from "../app/ApiService";

class Installation {
    id: string;
    name: string;
    serialNumber: string;

    constructor(id: string, name: string, serialNumber: string) {
        this.id = id;
        this.name = name;
        this.serialNumber = serialNumber;
    }
}

class InstallationService extends ApiService {
    public loadInstallations = (accessToken: string) => {
        return this.apiBuilder<Installation[]>()
            .withUri("installations")
            .withAuthorization(accessToken)
            .fetchBody();
    }

    public loadInstallation = (id: string, accessToken: string) => {
        return this.apiBuilder<Installation>()
            .withUri(`installations/${id}`)
            .withAuthorization(accessToken)
            .fetchBody();
    }

    public createInstallation = (name: string, accessToken: string) => {
        return this.apiBuilder()
            .withUri('installations')
            .withAuthorization(accessToken)
            .withBody({ name: name, serialNumber: crypto.randomUUID() })
            .post()
            .fetchLocationHeader()
            .then(location => {
                if (location) {
                    return location?.substring(location.lastIndexOf('/') + 1)
                } else {
                    throw new Error("No location header present.")
                }
            });
    }
}

export { Installation, InstallationService };