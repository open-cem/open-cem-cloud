import { ApiService } from "./ApiService";

class WizardSetsService extends ApiService {
    private readonly endpoint: string = "sets";

    public loadAllSets = (accessToken: string) => {
        return this.apiBuilder<string[]>()
            .withUri(this.endpoint)
            .withAuthorization(accessToken)
            .fetchBody();
    }

    public loadAllSetImages = (accessToken: string) => {
        return this.apiBuilder<string[]>()
            .withUri(`${this.endpoint}/images`)
            .withAuthorization(accessToken)
            .fetchBody();
    }
}

export default WizardSetsService;