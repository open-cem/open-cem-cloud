import { ApiService } from "../app/ApiService";

class CommunicationChannel {
    id: string;
    name: string;

    constructor(id: string, name: string) {
        this.id = id;
        this.name = name;
    }
}

class CommunicationChannelService extends ApiService {
    public loadCommunicationChannel(installationId: string) {
        return Promise.resolve([new CommunicationChannel("123", "Modbus RTU")]);
    }
}

export { CommunicationChannel, CommunicationChannelService };