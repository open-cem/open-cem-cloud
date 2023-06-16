import { ApiService } from "../app/ApiService";

class Installation {
    id: string;
    name: string;
    serialNumber: string;
    imageUrl: string;


    constructor(id: string, name: string, serialNumber: string, image?: string) {
        this.id = id;
        this.name = name;
        this.serialNumber = serialNumber;
        this.imageUrl = image ?? '';
    }
}

class NullInstallation extends Installation {
    constructor() {
        super('', '', '');
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

    public saveInstallation = (installation: Installation, accessToken: string, file?: File) => {
        const formData = new FormData();
        formData.append("installation", new Blob([JSON.stringify(installation)], {
          type: 'application/json'
        }));
        if (file) {
            formData.append("image", file);
        }

        return this.apiBuilder()
            .withUri(`installations/${installation.id}`)
            .withAuthorization(accessToken)
            .withFormData(formData)
            .put()
            .fetch();
    }

    public loadInstallationImage = (installation: Installation, accessToken: string) => {
        return this.apiBuilder()
            .withUri(`installations/${installation.imageUrl}`)
            .withAuthorization(accessToken)
            .fetchBlobResponse();
    }
}

export { Installation, NullInstallation, InstallationService };