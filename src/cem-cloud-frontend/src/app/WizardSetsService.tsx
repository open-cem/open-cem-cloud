import { ApiService } from "./ApiService";

interface PredefinedSet {
    headerinfo: Headerinfo
    steps: Step[]
}

interface Headerinfo {
    setname: string
}
  
interface Step {
    id?: string
    number: number
    step_type: string
    name: string
    image?: string
    description?: string
    data: DataItem[]
}

interface DataItem {
    name: string
    value: any
    hide: boolean
    readonly: boolean
    id?: string
}

class ComponentWizardConfiguration {
    installationId: string;
    componentId: string;
    config: Map<string, DataItem>;
    descriptionPanel : JSX.Element;
    
    constructor(installationId: string, componentId: string, config: Map<string, DataItem>, descriptionPanel : JSX.Element) {
        this.installationId = installationId;
        this.componentId = componentId;
        this.config = config;
        this.descriptionPanel = descriptionPanel;
    }

    isFieldHidden(fieldName: string) {
        return this.config.get(fieldName)?.hide ?? false;
    }

    isFieldReadonly(fieldName: string) {
        return this.config.get(fieldName)?.readonly ?? false;
    }
}
  

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

        return this.apiBuilder<string[]>()
            .withUri(this.endpoint)
            .withAuthorization(accessToken)
            .withFormData(formData)
            .post()
            .fetchBody();
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

    public loadAllSetsWithData(accessToken: string) {
        return this.apiBuilder<PredefinedSet[]>()
            .withUri(`${this.endpoint}/all`)
            .withAuthorization(accessToken)
            .fetchBody();
    }

    public loadAllSetImages(accessToken: string) {
        return this.apiBuilder<string[]>()
            .withUri(`${this.endpoint}/images`)
            .withAuthorization(accessToken)
            .fetchBody();
    }

    public loadSetImage(name: string, accessToken: string) {
        return this.apiBuilder()
            .withUri(`${this.endpoint}/images/${name}`)
            .withAuthorization(accessToken)
            .fetchBlobResponse();
    }

    public loadMissingFiles(accessToken: string) {
        return this.apiBuilder<string[]>()
            .withUri(`${this.endpoint}/missing`)
            .withAuthorization(accessToken)
            .fetchBody();
    }
}

export default WizardSetsService;
export type { PredefinedSet, Headerinfo, Step, DataItem };
export { ComponentWizardConfiguration };