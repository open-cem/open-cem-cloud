import { ApiService } from "../app/ApiService";

const DeviceFamily: string = "DEVICES";
const ActuatorFamily: string = "ACTUATORS";
const SensorFamily: string = "SENSORS";
const ControllerFamily: string = "CONTROLLERS";

interface Component {
    id: string;
    name: string;
    family: string
    manufacturerId?: string;
    modelId?: string;
    channelId?: string;
}

class NullComponent implements Component {
    id: string = '';
    name: string = '';
    family: string = '';
}

class ComponentListItem {
    id: string;
    name: string;
    family: string;

    constructor(id: string, name: string, family: string) {
        this.id = id;
        this.name = name;
        this.family = family;
    }
}

class ComponentType {
    id: string;
    name: string;
    family: string;

    constructor(id: string, name: string, family: string) {
        this.id = id;
        this.name = name;
        this.family = family;
    }
}

class ComponentsService extends ApiService {
    private readonly endpoint: string = "components";

    public loadComponent(componentId: string, accessToken: string) {
        return this.apiBuilder<Component>()
            .withUri(`${this.endpoint}/${componentId}`)
            .withAuthorization(accessToken)
            .fetchBody();
    }

    public loadComponents(installationId: string, accessToken: string) {
        return this.apiBuilder<ComponentListItem[]>()
            .withUri(this.endpoint)
            .withParameter("installationId", installationId)
            .withAuthorization(accessToken)
            .fetchBody();
    }

    public loadTypes(accessToken: string) {
        return this.apiBuilder<ComponentType[]>()
            .withUri(`${this.endpoint}/types`)
            .withAuthorization(accessToken)
            .fetchBody();
    }

    public createComponent(installationId: string, typeId: string, accessToken: string) {
        return this.apiBuilder()
            .withUri(this.endpoint)
            .withAuthorization(accessToken)
            .withBody({ installationId: installationId, typeId:typeId })
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

    public saveComponent(component: Component, accessToken: string) {
        return this.apiBuilder()
            .withUri(`${this.endpoint}/${component.id}`)
            .withAuthorization(accessToken)
            .withBody(component)
            .put()
            .fetch();
    }

    public deleteComponent(componentId: string, accessToken: string) {
        return this.apiBuilder()
            .withUri(`${this.endpoint}/${componentId}`)
            .withAuthorization(accessToken)
            .delete()
            .fetch();
    }
}

export { NullComponent, ComponentListItem, ComponentType, ComponentsService, DeviceFamily, ActuatorFamily, SensorFamily, ControllerFamily };
export type { Component };
