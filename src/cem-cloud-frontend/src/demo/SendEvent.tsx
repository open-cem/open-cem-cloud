import { useState } from "react";
import { useAuth } from "react-oidc-context";
import { Button } from 'primereact/button'
import { InputText } from 'primereact/inputtext';


function SendEvent() {
    const [message, setMessage] = useState<string>("");
    const auth = useAuth();

    const handleEvent = () => {
        
        const formData = new FormData();
        formData.append('message', message);
        
        fetch(
            new URL("http://localhost:8080/api/sendEvent"),
            {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${auth.user?.access_token}`
                },
                body: formData
            }
        )
            .then((result) => {
                console.log('Success:', result);
            })
            .catch((error) => {
                console.error('Error:', error);
            });
        }



    return (
        <div>
            <h3>MQTT Events</h3>
                <InputText  onChange={e => setMessage(e.target.value)} placeholder="Message..." name="event"/> 
                <Button onClick={handleEvent} label="Send" />
        </div>
        );
}

export default SendEvent;