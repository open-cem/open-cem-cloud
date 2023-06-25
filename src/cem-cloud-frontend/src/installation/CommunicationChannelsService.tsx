import { ApiService } from "../app/ApiService";
import { ParameterMeta } from "../parameterInput/ParameterInput";

class CommunicationChannel {
    id: string;
    name: string;
    parameter: Map<string, Object>;

    constructor(id: string, name: string, parameter: Map<string, Object>) {
        this.id = id;
        this.name = name;
        this.parameter = parameter;
    }
}

class NullCommunicationChannel extends CommunicationChannel {
    constructor() {
        super('', '', new Map<string, Object>());
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
    private readonly endpoint: string = "communicationChannels";

    public loadCommunicationChannel(channelId: string, accessToken: string) {
        return this.apiBuilder<CommunicationChannel>()
            .withUri(`${this.endpoint}/${channelId}`)
            .withAuthorization(accessToken)
            .fetchBody();
    }

    public loadCommunicationChannels(installationId: string, accessToken: string) {
        return this.apiBuilder<CommunicationChannel[]>()
            .withUri(this.endpoint)
            .withParameter("installationId", installationId)
            .withAuthorization(accessToken)
            .fetchBody();
    }

    public loadCommunicationChannelTypes(accessToken: string) {
        return this.apiBuilder<CommunicationChannelType[]>()
            .withUri(`${this.endpoint}/types`)
            .withAuthorization(accessToken)
            .fetchBody();
    }

    public loadCommunicationChannelParameterMeta(channelId: string, accessToken: string) {
        return this.apiBuilder<ParameterMeta[]>()
            .withUri(`${this.endpoint}/${channelId}/meta`)
            .withAuthorization(accessToken)
            .fetchBody();
    }

    public createCommunicationChannel(installationId: string, channelType: string, accessToken: string) {
        return this.apiBuilder()
            .withUri(this.endpoint)
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

    public deleteCommunicationChannel(channelId: string, accessToken: string) {
        return this.apiBuilder()
            .withUri(`${this.endpoint}/${channelId}`)
            .withAuthorization(accessToken)
            .delete()
            .fetch();
    }

    public saveCommunicationChannel(channel: CommunicationChannel, accessToken: string) {
        return this.apiBuilder()
            .withUri(`${this.endpoint}/${channel.id}`)
            .withAuthorization(accessToken)
            .withBody({ name: channel.name, parameters: channel.parameter })
            .put()
            .fetch();
    }
}

export { CommunicationChannel, NullCommunicationChannel, CommunicationChannelType, CommunicationChannelService };