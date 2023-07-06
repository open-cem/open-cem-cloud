import { ApiService } from "../app/ApiService";

interface SmartGridreadyFile {
    id: string;
    name: string;
}

class SmartGridreadyService extends ApiService {
    private readonly endpoint: string = "smartgridready";

    public loadFiles(accessToken: string) {
        return this.apiBuilder<SmartGridreadyFile[]>()
            .withUri(this.endpoint)
            .withAuthorization(accessToken)
            .fetchBody();
    }
};

export { SmartGridreadyService };
export type { SmartGridreadyFile };