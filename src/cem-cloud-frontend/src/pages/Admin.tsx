import { useCallback, useEffect, useRef, useState } from "react";
import { useAuth } from "react-oidc-context";
import FileList, { File } from "../fileList/FileList";
import { SmartGridreadyFile, SmartGridreadyService } from "../component/SmartGridreadyService";
import WizardSetsService from "../app/WizardSetsService";
import { presentError } from "../app/NotificationPresenter";
import { PrimeIcons } from 'primereact/api';
import { FileUpload, FileUploadHandlerEvent } from "primereact/fileupload";
import { Tooltip } from 'primereact/tooltip';
import { Panel } from "primereact/panel";
import { Toast } from "primereact/toast";
import _ from "lodash";
import "./Admin.css";


const Admin = () => {

    const auth = useAuth();
    const toast = useRef<Toast>(null);
    const fileUpload = useRef<FileUpload>(null);

    const [sets, setSets] = useState<File[]>([]);
    const [images, setImages] = useState<File[]>([]);
    const [sgrDefinitions, setSgrDefinitions] = useState<SmartGridreadyFile[]>([]);
    const [missingFiles, setMissingFiles] = useState<string[]>([]);

    const [setErrors, setSetErrors] = useState<string[]>([]);

    const loadSGrDefinitions = useCallback(() => {
        if (auth.user) {
            new SmartGridreadyService().loadFiles(auth.user.access_token)
                .then(setSgrDefinitions)
                .catch(e => presentError('SmartGridready Definitionen konnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        }
    }, [auth.user]);

    const loadSets = useCallback(() => {
        if (auth.user) {
            new WizardSetsService().loadAllSets(auth.user.access_token)
                .then(setSets)
                .catch(e => presentError('Sets konnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        }
    }, [auth.user]);

    const loadSetImages = useCallback(() => {
        if (auth.user) {
            new WizardSetsService().loadAllSetImages(auth.user.access_token)
                .then(setImages)
                .catch(e => presentError('Bilder konnte nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        }
    }, [auth.user]);

    const loadMissingFiles = useCallback(() => {
        if (auth.user) {
            new WizardSetsService().loadMissingFiles(auth.user.access_token)
                .then(setMissingFiles)
                .catch(e => presentError('Fehlende Dateien konnten nicht geladen werden, versuchen Sie es später erneut.', undefined, e, toast.current));
        }
    }, [auth.user]);

    useEffect(() => {
        loadSGrDefinitions();
        loadSets();
        loadSetImages();
        loadMissingFiles();
    }, [loadSGrDefinitions, loadSets, loadSetImages, loadMissingFiles]);

    const deleteSet = (name: string) => {
        if (auth.user && name) {
            new WizardSetsService().deleteSet(name, auth.user.access_token)
                .then(() => setSets(_.reject(sets, s => s === name)))
                .then(() => loadMissingFiles())
                .catch(e => presentError('Set konnte nicht gelöscht werden. Stellen Sie sicher, dass das Set nicht mehr verwendet wird oder versuchen Sie es später erneut.',
                    undefined, e, toast.current));
        }
    };

    const deleteImage = (name: string) => {
        if (auth.user && name) {
            new WizardSetsService().deleteImage(name, auth.user.access_token)
                .then(() => setImages(_.reject(images, i => i === name)))
                .then(() => loadMissingFiles())
                .catch(e => presentError('Bild konnte nicht gelöscht werden. Stellen Sie sicher, dass das Bild nicht mehr verwendet wird oder versuchen Sie es später erneut.',
                    undefined, e, toast.current));
        }
    };

    const deleteSgrDefinition = (name: string) => {
        const sgrDefinition = _.find(sgrDefinitions, f => f.name === name);
        if (auth.user && sgrDefinition) {
            new SmartGridreadyService().deleteFile(sgrDefinition.id, auth.user.access_token)
                .then(() => setSgrDefinitions(_.reject(sgrDefinitions, d => d.id === sgrDefinition.id)))
                .then(() => loadMissingFiles())
                .catch(e => presentError('SmartGridread Definition konnte nicht gelöscht werden. Stellen Sie sicher, dass die Defintion nicht mehr verwendet wird oder versuchen Sie es später erneut.',
                    undefined, e, toast.current));
        }
    };

    const chooseOptions = { icon: PrimeIcons.PLUS, iconOnly: true, className: 'custom-choose-btn' };
    const uploadOptions = { icon: PrimeIcons.CLOUD_UPLOAD, iconOnly: true, className: 'custom-upload-btn' };
    const cancelOptions = { icon: PrimeIcons.TIMES, iconOnly: true, className: 'custom-cancel-btn' };

    const errors = () => {
        if (setErrors.length > 0) {
        return (<Panel header="Fehler in Set(s)" className='invalid'>
                    <ul>
                        {setErrors.map((error) => {
                            return <li key={error}>{error}</li>})
                        }
                    </ul>
                </Panel>
            ) 
        } else {
            return <></>;
        }
    };

    const missingFilesPanel = () => {
        if (missingFiles.length > 0) {
            return (<Panel header="Fehlende Dateien" className='invalid'>
                        <ul>
                            {missingFiles.map((file) => {
                                return <li key={file}>{file}</li>}
                            )}
                        </ul>
                    </Panel>
                )
        } else {
            return <></>;
        }
    };


    const uploader = async (event: FileUploadHandlerEvent) => {
        if (!auth.user) {
            return;
        }

        const updateUploadList = (uploadedFiles: globalThis.File[]) => {
            if (fileUpload.current) {
                const allFiles = fileUpload.current.getFiles();
                fileUpload.current.setUploadedFiles(_.union(fileUpload.current.getUploadedFiles(), uploadedFiles));
                fileUpload.current.setFiles(_.reject(allFiles, file => _.includes(uploadedFiles, file)));
            }
        };

        const sets = _.filter(event.files, file => file.name.endsWith(".yaml"));
        let setsUploadTask = Promise.resolve(false);
        if (sets.length) {
            try {
                setsUploadTask = new WizardSetsService().addSets(sets, auth.user.access_token).then(setSetErrors).then(() => true);
            } catch (error) {
                presentError('Sets konnten nicht hochgeladen werden, versuchen Sie es später erneut.', undefined, error, toast.current);
            }
        }

        const sgrDefinitions = _.filter(event.files, file => file.name.endsWith(".xml"));
        let sgrDefinitionsUploadTask = Promise.resolve(false);
        if (sgrDefinitions.length) {
            try {
                sgrDefinitionsUploadTask = new SmartGridreadyService().addFiles(sgrDefinitions, auth.user.access_token).then(() => true);
            } catch (error) {
                presentError('SmartGridread Definitionen konnten nicht hochgeladen werden, versuchen Sie es später erneut.', undefined, error, toast.current);
            }
        }

        const images = _.filter(event.files, file => file.name.endsWith(".png") || file.name.endsWith(".jpeg"));
        let imagesUploadTask = Promise.resolve(false);
        if (images.length) {
            try {
                imagesUploadTask = new WizardSetsService().addImages(images, auth.user.access_token).then(() => true);
            } catch (error) {
                presentError('Bilder konnten nicht hochgeladen werden, versuchen Sie es später erneut.', undefined, error, toast.current);
            }
        }

        Promise.all([setsUploadTask, sgrDefinitionsUploadTask, imagesUploadTask])
            .then(tasks => {
                let files: globalThis.File[] = [];
                if (tasks[0]) {
                    files = _.union(files, sets);
                    loadSets();
                }
                if (tasks[1]) {
                    files = _.union(files, sgrDefinitions);
                    loadSGrDefinitions();
                }
                if (tasks[2]) {
                    files = _.union(files, images);
                    loadSetImages();
                }

                updateUploadList(files);
            })
            .then(() => loadMissingFiles());
    };

    return (
        <>
            <Toast ref={toast} />
            <div className='container'>
                <div>
                    <Tooltip target=".custom-choose-btn" content="Auswählen" position="bottom" />
                    <Tooltip target=".custom-upload-btn" content="Hochladen" position="bottom" />
                    <Tooltip target=".custom-cancel-btn" content="Abbrechen" position="bottom" />
                    <FileUpload ref={fileUpload} multiple accept="application/xml,application/yaml,.yaml,.png,.jpeg" maxFileSize={1_000_000}
                        emptyTemplate={<p>Dateien zum Hochladen hierherziehen und ablegen.</p>}
                        chooseOptions={chooseOptions} uploadOptions={uploadOptions} cancelOptions={cancelOptions}
                        customUpload uploadHandler={uploader} />
                </div>
                
                {errors()}
                {missingFilesPanel()}
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