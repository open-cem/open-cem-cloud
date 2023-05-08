
import { useState } from "react";
import { useAuth } from "react-oidc-context";


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
                <input  onChange={e => setMessage(e.target.value)} placeholder="Message..." name="event"/> 
                <input type="button" onClick={handleEvent} value="Send" />
        </div>
        );
}

export default SendEvent;