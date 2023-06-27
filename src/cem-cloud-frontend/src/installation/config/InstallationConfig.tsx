import { useAuth } from "react-oidc-context";
import { useNavigate, useParams } from "react-router-dom";
import { InstallationService, Installation, NullInstallation } from "../InstallationsService";
import { useEffect, useRef, useState } from "react";
import { InputGroup, PictureInputGroup } from "../../inputGroup/InputGroup";
import { Button } from "primereact/button";
import { presentError, presentSuccess } from "../../app/NotificationPresenter";
import { Toast } from "primereact/toast";
import { FileUpload, FileUploadHandlerEvent } from "primereact/fileupload";
import { Accordion, AccordionTab } from "primereact/accordion";
import { Dialog } from "primereact/dialog";
import { CommunicationChannel, CommunicationChannelService, CommunicationChannelType } from "../CommunicationChannelsService";
import ComponentListEntry from "../../componentListEntry/ComponentListEntry";
import ComponentListHeader from "../../componentListHeader/ComponentListHeader";
import _ from "lodash";

const InstallationConfig = () => {
    const params = useParams<string>();
    const navigate = useNavigate();
    const auth = useAuth();
    const toast = useRef<Toast>(null);

    const [installation, setInstallation] = useState<Installation>(new NullInstallation());
    const [image, setImage] = useState<File>();
    const [hasChanges, setHasChanges] = useState<boolean>(false);
    const [communicationChannels, setCommunicationChannels] = useState<CommunicationChannel[]>([]);
    const [activeIndex, setActiveIndex] = useState<number|undefined>(undefined);
    const [typesVisible, setTypesVisible] = useState<boolean>(false);
    const [channelTypes, setChannelTypes] = useState<CommunicationChannelType[]>([]);

    useEffect(() => {
        const installationId = params.installationId;
        if (installationId === undefined || !auth.user) {
            return;
        }

        new InstallationService().loadInstallation(installationId, auth.user.access_token)
            .then(setInstallation)
            .catch(e => presentError('Installation konnnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));

        const channels = new CommunicationChannelService()
        channels.loadCommunicationChannels(installationId, auth.user.access_token)
            .then(setCommunicationChannels)
            .then(_ => setActiveIndex(0))
            .catch(e => presentError('Kommunikationskanäle konnnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        channels.loadCommunicationChannelTypes(auth.user.access_token)
            .then(types => setChannelTypes(_.sortBy(types, t => t.name)));
    }, [auth, params.installationId]);

    const channelUrl = (channelId: string) => {
        return `/installations/${params.installationId}/communicationChannels/${channelId}`;
    };

    const saveImage = async (event: FileUploadHandlerEvent) => {
        const install = { ...installation, imageUrl: URL.createObjectURL(event.files[0]) };
        setInstallation(install);
        setHasChanges(true);
        setImage(event.files[0]);
    };

    const onChange = (propertyName: string, value: any) => {
        const i = { ...installation } as Installation;
        (i as {[key: string]: any})[propertyName] = value;
        setInstallation(i);
        setHasChanges(true);
    };

    const onSave = () => {
        if (auth.user && !(installation instanceof NullInstallation)) {
            new InstallationService()
                .saveInstallation(installation, auth.user?.access_token, image)
                .then(_ => {
                    if (toast.current) {
                        presentSuccess(toast.current, 'Installation wurde gespeichert.');
                    }
                    setHasChanges(false);
                })
                .catch(e => presentError('Installation konnte nicht gespeichert werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        }
    };

    const channels = (cs: CommunicationChannel[]) => {
        return cs.map(c =>
            <ComponentListEntry key={c.id} label={c.name} selectAction={() => navigate(channelUrl(c.id))} deleteAction={() => deleteChannel(c.id)} />
        );
    };

    const addChannel = (typeId: string) => {
        if (auth.user) {
            new CommunicationChannelService()
                .createCommunicationChannel(installation.id, typeId, auth.user.access_token)
                .then(id => navigate(channelUrl(id)))
                .catch(e => presentError('Der Kommunikationskanal konnte nicht erstellt werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        }
    };

    const deleteChannel = (channelId: string) => {
        if (auth.user) {
            new CommunicationChannelService()
                .deleteCommunicationChannel(channelId, auth.user.access_token)
                .then((v) => setCommunicationChannels(_.reject(communicationChannels, c => c.id === channelId)))
                .catch(e => presentError('Der Kommunikationskanal konnte nicht gelöscht werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        }
    };

    return (
        <>
            <Toast ref={toast} />
            <Dialog header="Kommunikationskanal Typ auswählen" visible={typesVisible} onHide={() => setTypesVisible(false)}>
                <div className="types-container">
                    {
                        channelTypes.map(type => <Button key={type.id} onClick={() => addChannel(type.id)}>{type.name}</Button>)
                    }
                </div>
            </Dialog>
            <div className="form">
                <InputGroup id="formName" label="Name" value={installation?.name} changeFn={(e) => onChange('name', e.target.value)} />
                <InputGroup id="id" label="ID der Installation" value={installation?.serialNumber} changeFn={(e) => onChange('serialNumber', e.target.value)} />
                {
                    installation.imageUrl
                    ? (
                        <>
                            <PictureInputGroup id="image" label="Anzeigebild" installation={installation} />
                            <FileUpload mode="basic" name="image" accept="image/*" auto={true} customUpload uploadHandler={saveImage} maxFileSize={1_000_000} chooseLabel="Bild wählen" />
                        </>
                        
                    )
                    : <></>
                }
            </div>
            <Accordion activeIndex={activeIndex}>
                <AccordionTab header={<ComponentListHeader label="Kommunikationskanäle" addAction={() => setTypesVisible(true)} />}>
                    {
                        communicationChannels.length
                        ? channels(communicationChannels)
                        : <p>Erstellen Sie neue Kommunikationskanäle mit dem +.</p>
                    }
                </AccordionTab>
            </Accordion> 
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

export default InstallationConfig;