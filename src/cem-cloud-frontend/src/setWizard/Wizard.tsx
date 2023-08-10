import { useCallback, useEffect, useRef, useState } from "react";
import { useAuth } from "react-oidc-context";
import WizardSetsService, { DataItem, PredefinedSet, Step } from "../app/WizardSetsService";
import { presentError } from "../app/NotificationPresenter";
import { Toast } from "primereact/toast";
import "./Sets.css";
import { useNavigate, useParams } from "react-router-dom";
import { Steps } from "primereact/steps";
import { MenuItem } from "primereact/menuitem";
import WizardStep from "./WizardStep";
import { Button } from "primereact/button";
import { ComponentsService } from "../component/ComponentsService";
import { CommunicationChannel, CommunicationChannelService } from "../installation/CommunicationChannelsService";
import { Skeleton } from "primereact/skeleton";

const Wizard = () => {
    
    const params = useParams<string>();
    const auth = useAuth();
    const toast = useRef<Toast>(null);
    const [step, setStep] = useState<number>(0);
    const [set, setSet] = useState<PredefinedSet>();
    const [comps, setComp] = useState(new Map());
    const loadingSet = useRef<boolean>(false);
    const [showUi, setShowUi] = useState<boolean>(false);
    const navigate = useNavigate();

    const addComp = (k: number, v: string) => {
        setComp(comps.set(k, v))
    }

    const installationId = params.installationId ?? "";

    const createParameterMap = (dataItems: DataItem[]) => {
      if (dataItems.length === 0) {
        return new Map<string, Object>();
      }
      return Object.fromEntries(
        new Map(
          dataItems.map((item) => {
            if (item.value_is_reference && item.value instanceof Array) {
              return [
                item.name,
                (item.value as Array<number>).map(i => comps.get(i))
              ];
            } else if (item.value_is_reference && item.value instanceof Number) {
              return [
                item.name,
                comps.get(item.value as number)
              ];
            } else {
              return [
                item.name,
                item.value,
              ];
            }
          })
        )
      ) as Map <string, Object>;

    }

    const runStep = (steps: Step[], token: string, index = 0): Promise<any> => {
        if (index >= steps.length) {
          return Promise.resolve();
        }
      
        const step = steps[index];
        if (loadingSet.current && !showUi && comps.size > 0) {
            setShowUi(true);
        }
        if (step.step_type === "CommunicationChannel") {
          return new CommunicationChannelService()
            .createCommunicationChannel(installationId, step.data.find(data => data.name === "type")?.id as string, token)
            .then((id) =>
              new CommunicationChannelService()
                .saveCommunicationChannel(
                  new CommunicationChannel(
                    id,
                    step.name,
                    createParameterMap(step.data)
                  ),
                  token
                ).then(() => {
                    addComp(step.number, id);
                })
            )
            .then(() => {
              return runStep(steps, token, index + 1);
            });
        } else if (step.step_type === "Component") {
          return new ComponentsService()
            .createComponent(installationId, step.data.find(data => data.name === "type")?.id as string, token)
            .then((id) =>
              new ComponentsService()
                .saveComponent(
                  {
                    id: id,
                    name: step.name,
                    family: "",
                    manufacturerId: step.data.find(data => data.name === "manufacturer")?.id as string,
                    modelId: step.data.find(data => data.name === "model")?.id as string,
                    channelId: comps.get(step.data.find(data => data.name === "communicationChannel")?.value) as string,
                    smartGridreadyDefinitionId: step.data.find(data => data.name === "smartgridready")?.id as string,
                    parameter: createParameterMap(step.data)
                  },
                  token
                ).then(() => {
                    addComp(step.number, id);
                })
            )
            .then(() => {
              return runStep(steps, token, index + 1);
            });
        } else {
          return runStep(steps, token, index + 1);
        }
      };

      const loadSet = useCallback(() => {
        if(loadingSet.current) {
            return;
        }
        loadingSet.current = true;
        if (auth.user) {
            const token = auth.user.access_token;
            return new WizardSetsService().loadAllSetsWithData(auth.user.access_token)
                .then(sets => {
                    let choosedSet = sets.find(set => params.setName !== undefined && set.headerinfo.setname === params.setName);
                    setSet(choosedSet);
                    runStep(choosedSet?.steps as Step[], token);
                })
                .catch(e => presentError('Set konnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        }// eslint-disable-next-line react-hooks/exhaustive-deps
    }, [auth.user, params.setName, setSet, loadingSet]);

    useEffect(() => {
        loadSet();
    }, [loadSet]);

    const progressBar = () => {
        let progressLabels: MenuItem[] = [];
        let activeIndex = 0;
        if (set) {
            activeIndex = set.steps.filter((_, idx) => idx <= step).length - 1;
            progressLabels = set.steps.map((stepObj, idx) => idx === step ? {label: stepObj.name} : {label: ""});
        }
        return (
            <div className="card">
                <Steps model={progressLabels} activeIndex={activeIndex}/> 
            </div>
        )    
    }

    const forward = () => {
        if (set && set.steps[step + 1]) {
            setStep(step + 1);
            setShowUi(false);
        }
    }

    const backward = () => {
        if (set && set.steps[step - 1]) {
            setStep(step - 1);
            setShowUi(false);
        }
    }

    const uiLoaded = () => {
        setShowUi(true);
    }

    return (
        <> 
           <Toast ref={toast} />
           {progressBar()}
           <div style={{"display": showUi ? "block" : "none"}}>
                {set && set.steps[step] && <WizardStep step={set.steps[step]} comps={comps} installationId={installationId} uiLoaded={() => uiLoaded()}/>}
           </div>
           <div style={{"display": !showUi ? "block" : "none"}}>
                <Skeleton height="10rem"></Skeleton>
           </div>
           <div className="button-bar">
                    <Button label="Zurück" onClick={() => backward()} disabled={step === 0 }/>
                    <Button style={{"display": set?.steps.length === undefined || step >= set.steps?.length - 1 ? "none" : "block"}} label="Weiter" onClick={() => forward()}/>
                    <Button style={{"display": set?.steps.length === undefined || step >= set.steps?.length - 1 ? "block" : "none"}} label="Fertig" onClick={() => navigate("/installations/" + params.installationId)}/>
           </div>
        </>
    );
}

export default Wizard;