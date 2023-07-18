import { ApiService } from "./ApiService";

class WizardSetsService extends ApiService {
    private readonly endpoint: string = "sets";

    public addImages(images: File[], accessToken: string) {
        const formData = new FormData();
        if (images.length) {
            images.forEach(image => formData.append("file", image));
        }

        return this.apiBuilder()
            .withUri(`${this.endpoint}/images`)
            .withAuthorization(accessToken)
            .withFormData(formData)
            .post()
            .fetch();
    }

    public addSets(sets: File[], accessToken: string) {
        const formData = new FormData();
        if (sets.length) {
            sets.forEach(set => formData.append("file", set));
        }

        return this.apiBuilder()
            .withUri(this.endpoint)
            .withAuthorization(accessToken)
            .withFormData(formData)
            .post()
            .fetch();
    }

    public deleteImage(name: string, accessToken: string) {
        return this.apiBuilder()
            .withUri(`${this.endpoint}/images/${name}`)
            .withAuthorization(accessToken)
            .delete()
            .fetch();
    }

    public deleteSet(name: string, accessToken: string) {
        return this.apiBuilder()
            .withUri(`${this.endpoint}/${name}`)
            .withAuthorization(accessToken)
            .delete()
            .fetch();
    }

    public loadAllSets(accessToken: string) {
        return this.apiBuilder<string[]>()
            .withUri(this.endpoint)
            .withAuthorization(accessToken)
            .fetchBody();
    }

    public loadAllSetImages(accessToken: string) {
        return this.apiBuilder<string[]>()
            .withUri(`${this.endpoint}/images`)
            .withAuthorization(accessToken)
            .fetchBody();
    }
}

export default WizardSetsService;