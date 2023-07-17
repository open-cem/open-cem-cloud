import { useEffect, useRef, useState } from "react";
import { useAuth } from "react-oidc-context";
import FileList, { File } from "../fileList/FileList";
import { SmartGridreadyFile, SmartGridreadyService } from "../component/SmartGridreadyService";
import WizardSetsService from "../app/WizardSetsService";
import { presentError } from "../app/NotificationPresenter";
import { PrimeIcons } from 'primereact/api';
import { FileUpload } from "primereact/fileupload";
import { Tooltip } from 'primereact/tooltip';
import { Panel } from "primereact/panel";
import { Toast } from "primereact/toast";
import _ from "lodash";
import "./Admin.css";


const Admin = () => {

    const auth = useAuth();
    const toast = useRef<Toast>(null);

    const [sets, setSets] = useState<File[]>([]);
    const [images, setImages] = useState<File[]>([]);
    const [sgrDefinitions, setSgrDefinitions] = useState<SmartGridreadyFile[]>([]);

    useEffect(() => {

        if (!auth.user) {
            return;
        }

        new SmartGridreadyService().loadFiles(auth.user.access_token)
            .then(setSgrDefinitions)
            .catch(e => presentError('SmartGridready Definitionen konnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));;
        const setsService = new WizardSetsService();
        setsService.loadAllSets(auth.user.access_token)
            .then(setSets)
            .catch(e => presentError('Sets konnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));;
        setsService.loadAllSetImages(auth.user.access_token)
            .then(setImages)
            .catch(e => presentError('Bilder konnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));
    }, [auth.user]);

    const deleteSet = (name: string) => {
        if (auth.user && name) {
            new WizardSetsService().deleteSet(name, auth.user.access_token)
                .then(() => setSets(_.reject(sets, s => s === name)))
                .catch(e => presentError('Set konnte nicht gelöscht werden. Stellen Sie sicher, dass das Set nicht mehr verwendet wird oder versuchen Sie es später erneut.', 
                    undefined, e, toast.current));
        }
    };

    const deleteImage = (name: string) => {
        if (auth.user && name) {
            new WizardSetsService().deleteImage(name, auth.user.access_token)
                .then(() => setImages(_.reject(images, i => i === name)))
                .catch(e => presentError('Bild konnte nicht gelöscht werden. Stellen Sie sicher, dass das Bild nicht mehr verwendet wird oder versuchen Sie es später erneut.', 
                    undefined, e, toast.current));
        }
    };

    const deleteSgrDefinition = (name: string) => {
        const sgrDefinition = _.find(sgrDefinitions, f => f.name === name);
        if (auth.user && sgrDefinition) {
            new SmartGridreadyService().deleteFile(sgrDefinition.id, auth.user.access_token)
                .then(() => setSgrDefinitions(_.reject(sgrDefinitions, d => d.id === sgrDefinition.id)))
                .catch(e => presentError('SmartGridread Definition konnte nicht gelöscht werden. Stellen Sie sicher, dass die Defintion nicht mehr verwendet wird oder versuchen Sie es später erneut.', 
                    undefined, e, toast.current));
        }
    }

    const chooseOptions = { icon: PrimeIcons.PLUS, iconOnly: true, className: 'custom-choose-btn' };
    const uploadOptions = { icon: PrimeIcons.CLOUD_UPLOAD, iconOnly: true, className: 'custom-upload-btn' };
    const cancelOptions = { icon: PrimeIcons.TIMES, iconOnly: true, className: 'custom-cancel-btn' };

    return (
        <>
            <Toast ref={toast} />
            <div className='container'>
                <div>
                    <Tooltip target=".custom-choose-btn" content="Auswählen" position="bottom" />
                    <Tooltip target=".custom-upload-btn" content="Hochladen" position="bottom" />
                    <Tooltip target=".custom-cancel-btn" content="Abbrechen" position="bottom" />
                    <FileUpload name="demo[]" url={'/api/upload'} multiple accept="image/*,application/xml,application/yaml,.yaml" maxFileSize={1_000_000}
                        emptyTemplate={<p>Dateien zum Hochladen hierherziehen und ablegen.</p>}
                        chooseOptions={chooseOptions} uploadOptions={uploadOptions} cancelOptions={cancelOptions} />
                </div>
                {/* TODO only visbile if there are missing files */}
                <Panel header="Fehlende Dateien" className='invalid'>
                    <p>
                        Küche.png, ABB_X356M.png, SGr_04_00016_xyz_ABB_meterV0.0.2.xml
                    </p>
                </Panel>
                <div className='files-container'>
                    <FileList title={"Sets"} initFiles={sets} deleteFn={deleteSet} />
                    <FileList title={"Bilder"} initFiles={images} deleteFn={deleteImage} />
                    <FileList title={"SmartGridready Definitionen"} initFiles={sgrDefinitions.map(file => file.name)} deleteFn={deleteSgrDefinition} />
                </div>
            </div>
        </>
    )
};

export default Admin;