import { useAuth } from "react-oidc-context";
import { useNavigate, useParams } from "react-router-dom";
import { InputGroup } from "../inputGroup/InputGroup";
import { CommunicationChannel as CommunicationChannelModel, CommunicationChannelService, NullCommunicationChannel } from "../installation/CommunicationChannelsService";
import { useEffect, useRef, useState } from "react";
import { Button } from "primereact/button";
import { presentError, presentSuccess } from "../app/NotificationPresenter";
import { Toast } from "primereact/toast";
import { ParameterInput, ParameterMeta } from "../parameterInput/ParameterInput";
import _ from "lodash";

const CommunicationChannel = () => {
    const auth = useAuth();
    const navigate = useNavigate();
    const params = useParams<string>();
    const toast = useRef<Toast>(null);

    const [channel, setChannel] = useState<CommunicationChannelModel>(new NullCommunicationChannel());
    const [parameters, setParameters] = useState<ParameterMeta[]>([]);
    const [hasChanges, setHasChanges] = useState<boolean>(false);

    useEffect(() => {
        if (params.channelId && auth.user) {
            const service = new CommunicationChannelService();
            service.loadCommunicationChannel(params.channelId, auth.user?.access_token)
                .then(c => {
                    setChannel(c);
                    if (auth.user) {
                        service.loadCommunicationChannelParameterMeta(c.id, auth.user?.access_token)
                            .then(ps => _.orderBy(ps, p => p.label))
                            .then(setParameters)
                    }
                    
                })
                .catch(e => presentError('Der Kommunikationskanal konnnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        }        
    }, [auth.user, params.channelId]);

    const onChange = (propertyName: string, value: any) => {
        const i = { ...channel } as CommunicationChannelModel;
        (i as { [key: string]: any })[propertyName] = value;
        setChannel(i);
        setHasChanges(true);
    };

    const onSave = () => {
        if (auth.user && !(channel instanceof NullCommunicationChannel)) {
            new CommunicationChannelService()
                .saveCommunicationChannel(channel, auth.user?.access_token)
                .then(_ => {
                    if (toast.current) {
                        presentSuccess(toast.current, 'Kommunikationskanal wurde gespeichert.');
                    }
                    setHasChanges(false);
                })
                .catch(e => presentError('Kommunikationskanal konnte nicht gespeichert werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        }
    };

    const onParameterChange = (parameterName: string, value: any) => {
        const i = { ...channel } as CommunicationChannelModel;
        (i.parameter as any)[parameterName] = value;
        setChannel(i);
        setHasChanges(true);
    };

    return (
        <>
            <Toast ref={toast} />
            <div className="form">
                <InputGroup id="formName" label="Name" value={channel?.name} changeFn={(e) => onChange('name', e.target.value)} />
                {
                    parameters.length === 0 && channel instanceof NullCommunicationChannel
                    ? <></>
                    : parameters.map(p => <ParameterInput key={p.name} meta={p} value={(channel.parameter as any)[p.name]} changeFn={onParameterChange} />)
                }
            </div>
            <div className="button-bar">
                <Button severity="secondary" outlined onClick={() => navigate(-1)}>Abbrechen</Button>
                {
                    hasChanges
                        ? <Button onClick={onSave}>Speichern</Button>
                        : <></>
                }
            </div>
        </>
    )
};

export default CommunicationChannel;