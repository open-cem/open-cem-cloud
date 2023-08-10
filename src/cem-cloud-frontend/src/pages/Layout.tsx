import { Outlet, Link, useNavigate } from "react-router-dom";
import { PrimeIcons } from "primereact/api";
import { useAuth } from "react-oidc-context";
import "./Layout.css";
import { useEffect, useState } from "react";
import { KeycloakAppUser } from "../app/AppUser";

const Layout = () => {
    const auth = useAuth();
    const navigate = useNavigate();
    const [user, setUser] = useState<KeycloakAppUser>();

    useEffect(() => {
        if (auth.user) {
            setUser(new KeycloakAppUser(auth.user));
        }
    }, [auth.user]);

    return (
        <>
            <nav>
                <Link to="/"><span id="title-nav">CEM-Cloud</span></Link>
                <menu>
                    <li><i className={PrimeIcons.SIGN_OUT} onClick={() => void auth.signoutRedirect()} title="Abmelden"></i></li>
                    { user?.isAdministrator ? <li><i className={PrimeIcons.COG} onClick={() => navigate("/admin")} title="Settings"></i></li> : null}
                </menu>
            </nav>

            <main>
                <Outlet />
            </main>
        </>
    )
};

export default Layout;