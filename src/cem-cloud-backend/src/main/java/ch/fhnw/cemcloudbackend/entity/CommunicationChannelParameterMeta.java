package ch.fhnw.cemcloudbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class CommunicationChannelParameterMeta extends ParameterMeta {

    @ManyToOne
    private CommunicationChannelType communicationChannelType;

    public CommunicationChannelType getCommunicationChannelType() {
        return communicationChannelType;
    }

    public void setCommunicationChannelType(CommunicationChannelType communicationChannelType) {
        this.communicationChannelType = communicationChannelType;
    }
}
