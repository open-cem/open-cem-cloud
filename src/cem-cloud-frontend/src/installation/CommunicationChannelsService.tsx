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
    public loadCommunicationChannel(installationId: string, accessToken: string) {
        return this.apiBuilder<CommunicationChannel[]>()
            .withUri("communicationChannels")
            .withParameter("installationId", installationId)
            .withAuthorization(accessToken)
            .fetchBody();
    }
}

export { CommunicationChannel, CommunicationChannelService };