import { ApiService } from "../app/ApiService";

interface SmartGridreadyFile {
    id: string;
    name: string;
}

class SmartGridreadyService extends ApiService {
    private readonly endpoint: string = "smartgridready";

    public deleteFile(id: string, accessToken: string) {
        return this.apiBuilder()
            .withUri(`${this.endpoint}/${id}`)
            .withAuthorization(accessToken)
            .delete()
            .fetch();
    }

    public loadFiles(accessToken: string) {
        return this.apiBuilder<SmartGridreadyFile[]>()
            .withUri(this.endpoint)
            .withAuthorization(accessToken)
            .fetchBody();
    }

    public addFiles(files: File[], accessToken: string) {
        const formData = new FormData();
        if (files.length) {
            files.forEach(file => formData.append("file", file));
        }

        return this.apiBuilder()
            .withUri(this.endpoint)
            .withAuthorization(accessToken)
            .withFormData(formData)
            .post()
            .fetch();
    }
};

export { SmartGridreadyService };
export type { SmartGridreadyFile };