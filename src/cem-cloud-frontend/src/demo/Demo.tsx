import { useAuth } from 'react-oidc-context';
import { PrimeIcons } from 'primereact/api';
import { Toast } from 'primereact/toast';
import { Tooltip } from 'primereact/tooltip';
import { FileUpload } from 'primereact/fileupload';
import { useRef } from 'react';

function Demo() {
    const auth = useAuth();
    const toast = useRef<Toast>(null);
    const fileUpload = useRef<FileUpload>(null);

	const handleSubmission = (event : any) => {
        const file = event.files[0];
        if (file != null) {
            const formData = new FormData();
            formData.append('file', file);
            
            fetch(
                new URL("http://localhost:8080/api/upload"),
                {
                    method: 'POST',
                    headers: {
                        Authorization: `Bearer ${auth.user?.access_token}`
                    },
                    body: formData
                }
            )
                .then((response) => response.json())
                .then((result) => {
                    console.log('Success:', result);
                    toast?.current?.show({ severity: 'info', summary: 'Success', detail: 'File Uploaded' });
                    fileUpload?.current?.clear();
                })
                .catch((error) => {
                    console.error('Error:', error);
                });
        }
	};

    const chooseOptions = { icon: PrimeIcons.PLUS, iconOnly: true, className: 'custom-choose-btn' };
    const uploadOptions = { icon: PrimeIcons.UPLOAD, iconOnly: true, className: 'custom-upload-btn' };
    const cancelOptions = { icon: PrimeIcons.TIMES, iconOnly: true, className: 'custom-cancel-btn p-button-danger' };

    return (
        <div>
            <Toast ref={toast}></Toast>

            <Tooltip target=".custom-choose-btn" content="Wählen" position="bottom" />
            <Tooltip target=".custom-upload-btn" content="Hochladen" position="bottom" />
            <Tooltip target=".custom-cancel-btn" content="Löschen" position="bottom" />

            <FileUpload ref={fileUpload} name="file" chooseOptions={chooseOptions} uploadOptions={uploadOptions} cancelOptions={cancelOptions} customUpload uploadHandler={handleSubmission} multiple accept=".xml, .yml" maxFileSize={1000000} emptyTemplate={<p className="m-0">Dateien per Drag And Drop hierher ziehen um diese hochzuladen.</p>} />
        </div>
    )
}

export default Demo