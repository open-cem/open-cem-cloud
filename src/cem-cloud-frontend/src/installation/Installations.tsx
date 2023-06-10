import { useEffect, useState, Fragment } from 'react';
import { PrimeIcons } from 'primereact/api';
import { Card } from 'primereact/card';
import { Divider } from 'primereact/divider';
import { useAuth } from 'react-oidc-context';
import './Installations.css';
import { Installation, InstallationService } from './InstallationsService';
import { Link, useNavigate } from 'react-router-dom';
import { Toolbar } from 'primereact/toolbar';
import { Button } from 'primereact/button';
import { InputText } from 'primereact/inputtext';
import _ from 'lodash';
        

function Installations() {
    const auth = useAuth();
    const navigate = useNavigate();
    const [searchTerm, setSearchTerm] = useState<string>("");
    const [installations, setInstallations] = useState<Installation[]>([]);

    useEffect(() => {
        if (auth.user) {
            new InstallationService()
                .loadInstallations(auth.user.access_token)
                .then(i => _.filter(i, i => i.name.toLowerCase().includes(searchTerm.toLowerCase())))
                .then(i => _.sortBy(i, i => i.name))
                .then(setInstallations)
                .catch(console.error);
        }
    }, [auth, searchTerm]);

    const createNewInstallation = () => {
        if (auth.user) {
            new InstallationService()
                .createInstallation('Neue Installation', auth.user?.access_token)
                .then(id => navigate(`/installations/${id}/config`))
                .catch(console.error)
        }
    };

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

    const startContent = (
        <Fragment>
            <span className="p-input-icon-left">
                <i className={PrimeIcons.SEARCH} />
                <InputText placeholder="Suche" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
            </span>
        </Fragment>
    );

    const endContent = (
        <Fragment>
            <Button icon={PrimeIcons.PLUS} className="mr-2" onClick={createNewInstallation} />
        </Fragment>
    );

    return (
        <>
            <Toolbar start={startContent} end={endContent} />
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
        </>
    )
};

export default Installations;