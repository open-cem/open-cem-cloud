import { useEffect, useState } from "react";
import { useAuth } from "react-oidc-context";
import FileList, { File } from "../fileList/FileList";
import { SmartGridreadyService } from "../component/SmartGridreadyService";
import WizardSetsService from "../app/WizardSetsService";
import { PrimeIcons } from 'primereact/api';
import { FileUpload } from "primereact/fileupload";
import { Tooltip } from 'primereact/tooltip';
import { Panel } from "primereact/panel";
import "./Admin.css";

const Admin = () => {

    const auth = useAuth();

    const [sets, setSets] = useState<File[]>([]);
    const [images, setImages] = useState<File[]>([]);
    const [sgrDefinitions, setSgrDefinitions] = useState<File[]>([]);

    useEffect(() => {

        if (!auth.user) {
            return;
        }

        new SmartGridreadyService().loadFiles(auth.user.access_token)
            .then(files => setSgrDefinitions(files.map(f => f.name)));
        const setsService = new WizardSetsService();
        setsService.loadAllSets(auth.user.access_token)
            .then(setSets);
        setsService.loadAllSetImages(auth.user.access_token)
            .then(setImages);
    }, [auth.user]);

    const chooseOptions = { icon: PrimeIcons.PLUS, iconOnly: true, className: 'custom-choose-btn' };
    const uploadOptions = { icon: PrimeIcons.CLOUD_UPLOAD, iconOnly: true, className: 'custom-upload-btn' };
    const cancelOptions = { icon: PrimeIcons.TIMES, iconOnly: true, className: 'custom-cancel-btn' };

    return (
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
                <FileList title={"Sets"} initFiles={sets} deleteFn={() => {console.log("sets")}} />
                <FileList title={"Bilder"} initFiles={images} deleteFn={() => {console.log("images")}} />
                <FileList title={"SmartGridready Definitionen"} initFiles={sgrDefinitions} deleteFn={() => {console.log("sgr")}} />
            </div>
        </div>
    )
};

export default Admin;