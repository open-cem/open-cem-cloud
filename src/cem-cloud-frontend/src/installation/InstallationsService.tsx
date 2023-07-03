import { ApiService } from "../app/ApiService";

class Installation {
    id: string;
    name: string;
    serialNumber: string;
    imageUrl: string;
    isOutOfSync: boolean;

    constructor(id: string, name: string, serialNumber: string, image?: string, isOutOfSync?: boolean) {
        this.id = id;
        this.name = name;
        this.serialNumber = serialNumber;
        this.imageUrl = image ?? '';
        this.isOutOfSync = isOutOfSync ?? false;
    }
}

class NullInstallation extends Installation {
    constructor() {
        super('', '', '');
    }
}

class InstallationService extends ApiService {
    private readonly endpoint: string = "installations";

    public loadInstallations = (accessToken: string) => {
        return this.apiBuilder<Installation[]>()
            .withUri(this.endpoint)
            .withAuthorization(accessToken)
            .fetchBody();
    }

    public loadInstallation = (id: string, accessToken: string) => {
        return this.apiBuilder<Installation>()
            .withUri(`${this.endpoint}/${id}`)
            .withAuthorization(accessToken)
            .fetchBody();
    }

    public createInstallation = (name: string, accessToken: string) => {
        return this.apiBuilder()
            .withUri(this.endpoint)
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

    public saveInstallation = (installation: Installation, accessToken: string, file?: File) => {
        const formData = new FormData();
        formData.append("installation", new Blob([JSON.stringify(installation)], {
          type: 'application/json'
        }));
        if (file) {
            formData.append("image", file);
        }

        return this.apiBuilder()
            .withUri(`${this.endpoint}/${installation.id}`)
            .withAuthorization(accessToken)
            .withFormData(formData)
            .put()
            .fetch();
    }

    public loadInstallationImage = (installation: Installation, accessToken: string) => {
        return this.apiBuilder()
            .withUri(`${this.endpoint}/${installation.imageUrl}`)
            .withAuthorization(accessToken)
            .fetchBlobResponse();
    }

    public syncInstallation = (installationId: string, accessToken: string) => {
        return this.apiBuilder()
            .withUri(`${this.endpoint}/${installationId}/sync`)
            .withAuthorization(accessToken)
            .post()
            .fetch();
    }
}

export { Installation, NullInstallation, InstallationService };