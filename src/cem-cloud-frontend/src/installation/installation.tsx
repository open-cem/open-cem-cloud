import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { useAuth } from "react-oidc-context";
import { Accordion, AccordionTab } from "primereact/accordion";
import { Toast } from "primereact/toast";
import { presentError } from "../app/NotificationPresenter";
import { ActuatorFamily, Component, ComponentsService, ControllerFamily, DeviceFamily, SensorFamily } from "./ComponentsService";
import ComponentListHeader from "../componentListHeader/ComponentListHeader";
import ComponentListEntry from "../componentListEntry/ComponentListEntry";
import _ from "lodash";

const Installation = () => {
    const params = useParams<string>();
    const auth = useAuth();
    const toast = useRef<Toast>(null);

    const [activeIndex, setActiveIndex] = useState<number | undefined>(undefined);
    const [components, setComponents] = useState<Component[]>([]);

    useEffect(() => {
        const installationId = params.installationId;
        if (!auth.user || !installationId) {
            return;
        }

        new ComponentsService().loadComponents(installationId, auth.user.access_token)
            .then(setComponents)
            .then(_ => setActiveIndex(0))
            .catch(e => presentError('Geräte konnnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        ;
    }, [auth.user, params.installationId]);

    const deleteComponent = (component: Component) => { };

    const createComponentEntries = (components: Component[]) => {
        return components.map(component =>
            <ComponentListEntry key={component.id} label={component.name} selectAction={() => { }} deleteAction={() => deleteComponent(component)} />
        );
    };

    const createComponentGroup = (name: string, group: string, components: Component[]) => {
        return <AccordionTab header={<ComponentListHeader label={name} addAction={() => { }} />}>
            {
                components.length
                    ? createComponentEntries(_.filter(components, component => component.family === group))
                    : <p>Erstellen Sie neue {name} mit dem +.</p>
            }
        </AccordionTab>
    }

    return (
        <>
            <Toast ref={toast} />
            <Accordion multiple activeIndex={activeIndex}>
                {createComponentGroup("Geräte", DeviceFamily, components)}
                {createComponentGroup("Sensoren", SensorFamily, components)}
                {createComponentGroup("Aktuatoren", ActuatorFamily, components)}
                {createComponentGroup("Kontroller", ControllerFamily, components)}
            </Accordion>
        </>
    );
};

export default Installation;