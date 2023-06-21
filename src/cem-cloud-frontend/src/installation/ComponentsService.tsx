import { ApiService } from "../app/ApiService";

const DeviceFamily: string = "DEVICES";
const ActuatorFamily: string = "ACTUATORS";
const SensorFamily: string = "SENSORS";
const ControllerFamily: string = "CONTROLLERS";

class Component {
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

    public loadComponents(installationId: string, accessToken: string) {
        return this.apiBuilder<Component[]>()
            .withUri(this.endpoint)
            .withParameter("installationId", installationId)
            .withAuthorization(accessToken)
            .fetchBody();
    }
}

export { Component, ComponentsService, DeviceFamily, ActuatorFamily, SensorFamily, ControllerFamily };