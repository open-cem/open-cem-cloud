import { useState } from "react";
import { useAuth } from "react-oidc-context";

function Demo() {
    const auth = useAuth();
    const [selectedFile, setSelectedFile] = useState<File>();
	const [isFilePicked, setIsFilePicked] = useState(false);

    const changeHandler = (event : any) => {
		setSelectedFile(event.target.files[0]);
		setIsFilePicked(true);
	};

	const handleSubmission = () => {
        if (selectedFile != null) {
            const formData = new FormData();
            formData.append('file', selectedFile);
            
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
                })
                .catch((error) => {
                    console.error('Error:', error);
                });
        }
	};

    return (
        <div>
            <label htmlFor="file">Datei</label>
            <input name="file" type="file" onChange={changeHandler} ></input>
            {isFilePicked && selectedFile != null ? (
				<div>
					<p>Filename: {selectedFile.name}</p>
					<p>Filetype: {selectedFile.type}</p>
					<p>Size in bytes: {selectedFile.size}</p>
					<p>
						lastModifiedDate:{' '}
						{new Date(selectedFile.lastModified).toLocaleDateString()}
					</p>
				</div>
			) : (
				<p>Select a file to show details</p>
			)}
            <button onClick={handleSubmission}>Submit</button>
        </div>
    )
}

export default Demo