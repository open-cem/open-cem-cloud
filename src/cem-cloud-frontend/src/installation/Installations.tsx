import { useEffect, useState } from 'react';
import { PrimeIcons } from 'primereact/api';
import { Card } from 'primereact/card';
import { Divider } from 'primereact/divider';
import { useAuth } from 'react-oidc-context';
import './Installations.css';
import { Installation, InstallationService } from './InstallationsService';
import { Link } from 'react-router-dom';

function Installations() {
    const auth = useAuth();
    const [installations, setInstallations] = useState<Installation[]>([]);

    useEffect(() => {
        if (auth.user) {
            new InstallationService().loadInstallations(auth.user.access_token)
                .then(setInstallations)
                .catch(console.error);
        }
    }, [auth]);

    const header = (i: Installation) => (
        <div>
            <Link to={`/installations/${i.id}/config`}>
                <i className={PrimeIcons.COG}></i>
            </Link>
            <Link to={`/installations/${i.id}`}>
                <img alt="Card" src="/img/placeholder.png" />
                <Divider />
            </Link>
        </div>
    );

    return (
        <div className="card-container">
            {
                installations.map(i =>
                    <Card key={i.id} title={
                        <Link to={`/installations/${i.id}`} key={i.id}>
                            {i.name}
                        </Link>
                    } header={header(i)} />
                )
            }
        </div>
    )
};

export default Installations;