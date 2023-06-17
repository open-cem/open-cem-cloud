import { ApiService } from "../app/ApiService";

class CommunicationChannel {
    id: string;
    name: string;

    constructor(id: string, name: string) {
        this.id = id;
        this.name = name;
    }
}

class CommunicationChannelType {
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

    public loadCommunicationChannelTypes(accessToken: string) {
        return this.apiBuilder<CommunicationChannelType[]>()
            .withUri("communicationChannels/types")
            .withAuthorization(accessToken)
            .fetchBody();
    }

    public createCommunicationChannel(installationId: string, channelType: string, accessToken: string) {
        return this.apiBuilder()
            .withUri('communicationChannels')
            .withAuthorization(accessToken)
            .withBody({ installationId: installationId, typeId: channelType })
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
}

export { CommunicationChannel, CommunicationChannelType, CommunicationChannelService };