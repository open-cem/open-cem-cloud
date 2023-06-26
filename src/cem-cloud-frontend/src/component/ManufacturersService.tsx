import { ApiService } from "../app/ApiService";

interface Manufacturer {
    id: string;
    name: string;
}

interface Model {
    id: string;
    name: string;
}

class ManufacturersService extends ApiService {
    private readonly endpoint: string = "manufacturers";
    
    public loadManufacturers(accessToken: string) {
        return this.apiBuilder<Manufacturer[]>()
            .withUri(this.endpoint)
            .withAuthorization(accessToken)
            .fetchBody();
    }

    public loadModels(manufacturerId: string, accessToken: string) {
        return this.apiBuilder<Model[]>()
            .withUri(`${this.endpoint}/${manufacturerId}/models`)
            .withAuthorization(accessToken)
            .fetchBody();
    }
}

export { ManufacturersService };
export type { Manufacturer, Model };