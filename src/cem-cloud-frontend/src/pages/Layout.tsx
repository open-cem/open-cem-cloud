import { Outlet, Link } from "react-router-dom";
import { PrimeIcons } from "primereact/api";
import { useAuth } from "react-oidc-context";
import "./Layout.css";

const Layout = () => {
    const auth = useAuth();

    return (
        <>
            <nav>
                <Link to="/"><span id="title-nav">CEM-Cloud</span></Link>
                <menu>
                    <li><i className={PrimeIcons.SIGN_OUT} onClick={() => void auth.signoutRedirect()} title="Abmelden"></i></li>
                </menu>
            </nav>

            <main>
                <Outlet />
            </main>
        </>
    )
};

export default Layout;