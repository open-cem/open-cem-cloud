import { useEffect, useState } from 'react';
import { PrimeIcons } from 'primereact/api';
import { Card } from 'primereact/card';
import { Divider } from 'primereact/divider';
import { useAuth } from 'react-oidc-context';
import './Installations.css';

class Installation {
    id: string;
    name: string;
    serialNumber: number;

    constructor(id: string, name: string, serialNumber: number) {
        this.id = id;
        this.name = name;
        this.serialNumber = serialNumber;
    }
}

function Installations() {
    const auth = useAuth();
    const [installations, setInstallations] = useState<Installation[]>([]);

    const loadInstallations = () => {
        fetch("http://localhost:8080/api/installations", { headers: [["authorization", `Bearer ${auth.user?.access_token}`]] })
            .then(r => r.json())
            .then((installations: Installation[]) => setInstallations(installations))
            .catch(e => console.error(e))
    }

    useEffect(() => loadInstallations(), [auth]);

    const header = (
        <div>
            <i className={ PrimeIcons.COG }></i>
            <img alt="Card" src="/img/placeholder.png" />
            <Divider />
        </div>
    );

    return (
        <div className="card-container">
            {
                installations.map(i =>
                    <Card key={i.id} title={i.name} header={header}></Card>
                )
            }
        </div>
    )
};

export default Installations;