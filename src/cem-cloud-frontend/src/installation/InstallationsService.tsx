import { ApiService } from "../app/ApiService";

class Installation {
    id: string;
    name: string;
    serialNumber: number;

    constructor(id: string, name: string, serialNumber: number) {
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
            .fetch()
    };

    public loadInstallation = (id: string, accessToken: string) => {
        return this.apiBuilder<Installation>()
            .withUri(`installations/${id}`)
            .withAuthorization(accessToken)
            .fetch()
    };
}

export { Installation, InstallationService };