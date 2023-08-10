import { ComponentWizardConfiguration, Step } from "../app/WizardSetsService";
import "./Sets.css";
import Component from "../component/Component";
import CommunicationChannel from "../communicationChannel/communicationChannel";
import AuthorizedImage from "../authorizedImage/authorizedImage";

const WizardStep = ({step, comps, installationId, uiLoaded}: {step: Step, comps:Map<number,String>, installationId: string, uiLoaded: Function}) => {
    
    const descriptionPanel = () => {
        return step.description ?
         <div className="setComponent">
            <AuthorizedImage className="col" id={step.number as unknown as string} name={step.image} />
            <div className="col">{step.description}</div>
        </div>
        : <></>
    }

    const getInputConfig = () => {
        return new ComponentWizardConfiguration(
            installationId,
            comps.get(step.number) as string,
            new Map(step.data.map((item) => [item.name, item.value])),
            descriptionPanel()
        )
    }

    const getEditForm = () : JSX.Element => {
        if (step.step_type === "CommunicationChannel") {
            return <CommunicationChannel inputConfig={getInputConfig()} uiLoaded={() => uiLoaded()}/>;
        } else if (step.step_type === "Component") {
            return (<Component inputConfig={getInputConfig()} uiLoaded={() => uiLoaded()}/>)
        }
        return <></>
    }

    return (
        <> 
            {getEditForm()}
        </>
    );
}

export default WizardStep;