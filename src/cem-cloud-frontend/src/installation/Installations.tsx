import { useEffect, useState } from 'react';
import { PrimeIcons } from 'primereact/api';
import { Card } from 'primereact/card';
import { Divider } from 'primereact/divider';
import { useAuth } from 'react-oidc-context';
import './Installations.css';
import { Installation, InstallationService } from './InstallationsService';

function Installations(props: any) {
    const auth = useAuth();
    const [installations, setInstallations] = useState<Installation[]>([]);

    useEffect(() => {
        new InstallationService(props.apiUri, auth).loadInstallations()
            .then(setInstallations)
            .catch(console.error);
    }, [auth, props.apiUri]);

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