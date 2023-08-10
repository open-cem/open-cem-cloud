import { useCallback, useEffect, useRef, useState } from "react";
import { useAuth } from "react-oidc-context";
import WizardSetsService, { PredefinedSet, Step } from "../app/WizardSetsService";
import { presentError } from "../app/NotificationPresenter";
import { Toast } from "primereact/toast";
import { Accordion, AccordionTab } from "primereact/accordion";
import ComponentListHeader from "../componentListHeader/ComponentListHeader";
import { DataView } from "primereact/dataview";
import AuthorizedImage from "../authorizedImage/authorizedImage";
import "./Sets.css";
import { useNavigate, useParams } from "react-router-dom";

const Sets = () => {
    
    const auth = useAuth();
    const toast = useRef<Toast>(null);
    const navigate = useNavigate();
    const params = useParams<string>();

    const [sets, setSets] = useState<PredefinedSet[]>([]);

    const loadSets = useCallback(() => {
        if (auth.user) {
            new WizardSetsService().loadAllSetsWithData(auth.user.access_token)
                .then(setSets)
                .catch(e => presentError('Sets konnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        }
    }, [auth.user]);

    useEffect(() => {
        loadSets();
    }, [loadSets]);

    const createSetGroups = (predefinedSets: PredefinedSet[]) => {
        return predefinedSets.map(set => createSetGroup(set.headerinfo.setname, set.steps));
    }

    const createSetGroup = (name: string, steps: Step[]) => {

        return <AccordionTab header={<ComponentListHeader label={name} addAction={() => navigate("sets/" + name)} />}>
            {
                steps.length > 0
                    ? createSetSteps(steps)
                    : <p>Leeres Set</p>
            }
        </AccordionTab>
    };

    const createSetSteps = (steps: Step[]) => {
        return (
            <DataView value={steps} itemTemplate={itemTemplate} />
        );
    }

    const itemTemplate = (step: Step) => {
        return (
            <div className="setComponent">
                <AuthorizedImage className="col" id={step.number as unknown as string} name={step.image} />
                <div className="col">{step.name}</div>

            </div>
        );
    };


    return (
        <>
            <Toast ref={toast} />
            <p>Aus einem vordefnierten Set von Komponenten wählen:</p>
            <Accordion>{createSetGroups(sets)}</Accordion>
        </>
    );
}

export default Sets;