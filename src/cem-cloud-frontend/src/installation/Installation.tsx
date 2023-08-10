import React, { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useAuth } from "react-oidc-context";
import { Accordion, AccordionTab } from "primereact/accordion";
import { Toast } from "primereact/toast";
import { Dialog } from "primereact/dialog";
import { Button } from "primereact/button";
import { Messages } from 'primereact/messages';
import { presentError, presentSuccess } from "../app/NotificationPresenter";
import { ActuatorFamily, ComponentListItem, ComponentType, ComponentsService, ControllerFamily, DeviceFamily, SensorFamily } from "../component/ComponentsService";
import ComponentListHeader from "../componentListHeader/ComponentListHeader";
import ComponentListEntry from "../componentListEntry/ComponentListEntry";
import _ from "lodash";
import { InstallationService } from "./InstallationsService";
import Sets from "../setWizard/Sets";

const Installation = () => {
    const params = useParams<string>();
    const auth = useAuth();
    const navigate = useNavigate();
    const toast = useRef<Toast>(null);
    const messages = useRef<Messages>(null);

    const [activeIndex, setActiveIndex] = useState<number | undefined>(undefined);
    const [components, setComponents] = useState<ComponentListItem[]>([]);
    const [types, setTypes] = useState<ComponentType[]>([]);
    const [familyTypes, setFamilyTypes] = useState<ComponentType[]>([]);
    const [typeDialogVisible, setTypeDialogVisible] = useState<boolean>(false);
    const [skipSets, setSkipSets] = useState<boolean>(false);

    useEffect(() => {
        const installationId = params.installationId;
        if (!auth.user || !installationId) {
            return;
        }

        messages.current?.clear();

        const service = new ComponentsService();
        service.loadComponents(installationId, auth.user.access_token)
            .then(setComponents)
            .then(_ => setActiveIndex(0))
            .catch(e => presentError('Komponenten konnnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        
        service.loadComponentTypes(auth.user.access_token)
            .then(setTypes)
            .catch(e => presentError('Komponenten-Typen konnnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));


        const sync = () => {
            if (auth.user) {
                new InstallationService().syncInstallation(installationId, auth.user.access_token)
                    .then(_ => {
                        if (toast.current) {
                            messages.current?.clear();
                            presentSuccess(toast.current, 'Synchronisation wurde ausgelöst. Es kann einige Minuten dauern, bis die Einstellungen von der Installation übernommen wurden.');
                        }
                    })
                    .catch(e => presentError('Synchronisation ist fehlgeschlagen, versuchen Sie es später erneut.', undefined, e, toast.current));
            }
        };

        new InstallationService().loadInstallation(installationId, auth.user.access_token)
            .then(i => {
                if (i.isOutOfSync) {
                    messages.current?.replace({
                        sticky: true, 
                        severity: 'warn', 
                        summary: 'Die Änderungen sind noch nicht aktiv. Wollen Sie die Änderungen jetzt an die lokale Installation senden?', 
                        detail: (
                            <React.Fragment>
                                <Button label="Änderungen senden" onClick={sync} />
                            </React.Fragment>
                        ), 
                        closable: false
                    });
                }
            });
    }, [auth.user, params.installationId]);

    const addComponent = (typeId: string) => {
        if (auth.user && params.installationId) {
            new ComponentsService()
                .createComponent(params.installationId, typeId, auth.user.access_token)
                .then(id => navigate(`components/${id}`))
                .catch(e => presentError('Die Komponente konnte nicht erstellt werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        }
    };

    const deleteComponent = (component: ComponentListItem) => {
        if (auth.user) {
            new ComponentsService()
                .deleteComponent(component.id, auth.user.access_token)
                .then((v) => setComponents(_.reject(components, c => c.id === component.id)))
                .catch(e => presentError('Die Komponente konnte nicht gelöscht werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        }
    };

    const showTypeDialog = (family: string) => {
        let familyTypes = _.filter(types, type => type.family === family);
        familyTypes = _.orderBy(familyTypes, t => t.name);
        setFamilyTypes(familyTypes);
        setTypeDialogVisible(true);
    };

    const createComponentEntries = (components: ComponentListItem[]) => {
        return components.map(component =>
            <ComponentListEntry key={component.id} label={component.name} selectAction={() => navigate(`components/${component.id}`)} deleteAction={() => deleteComponent(component)} />
        );
    };

    const createComponentGroup = (name: string, group: string, components: ComponentListItem[]) => {
        let componentsInFamily = _.filter(components, component => component.family === group);
        componentsInFamily = _.orderBy(componentsInFamily, f => f.name);
        return <AccordionTab header={<ComponentListHeader label={name} addAction={() => showTypeDialog(group)} />}>
            {
                componentsInFamily.length > 0
                    ? createComponentEntries(componentsInFamily)
                    : <p>Erstellen Sie neue {name} mit dem +.</p>
            }
        </AccordionTab>
    };

    const createModeButton = () => {
        if (components.length > 0) {
            return <></>
        } else {        
            if (skipSets) {
                return <Button label="Vordefinierte Sets verwenden" onClick={() => setSkipSets(!skipSets)} />;
            } else {
                return <Button label="Vordefinierte Sets überspringen" onClick={() => setSkipSets(!skipSets)} />;
            }
        }
    };

    const createSetView = () => {
        return <Sets/>;
    };

    const createInstallationView = () => {
        return (
            <>
                <Toast ref={toast} />
                <Dialog header="Typ auswählen" visible={typeDialogVisible} onHide={() => setTypeDialogVisible(false)}>
                    <div className="types-container">
                        {
                            familyTypes.map(type => <Button key={type.id} onClick={() => addComponent(type.id)}>{type.name}</Button>)
                        }
                    </div>
                </Dialog>
                <Messages ref={messages} />
                <Accordion multiple activeIndex={activeIndex}>
                    {createComponentGroup("Geräte", DeviceFamily, components)}
                    {createComponentGroup("Sensoren", SensorFamily, components)}
                    {createComponentGroup("Aktuatoren", ActuatorFamily, components)}
                    {createComponentGroup("Kontroller", ControllerFamily, components)}
                </Accordion>
            </>
        )
    };



    return (
        <>
            {components.length > 0 || skipSets ? createInstallationView() : createSetView()}
            {createModeButton()}
        </>
    );
};

export default Installation;