import "primereact/resources/primereact.min.css";
import 'primeicons/primeicons.css';
import "./theme.css";
import './App.css';
import { useAuth, hasAuthParams } from 'react-oidc-context';
import { Button } from 'primereact/button'
import Installations from "./installation/Installations";
import { useEffect } from "react";

function App(props: any) {
  const auth = useAuth();

  useEffect(() => {
    if (!hasAuthParams() &&
        !auth.isAuthenticated && !auth.activeNavigator && !auth.isLoading) {
        auth.signinRedirect();
    }
}, [auth.isAuthenticated, auth.activeNavigator, auth.isLoading, auth.signinRedirect]);

  switch (auth.activeNavigator) {
    case "signinSilent":
        return <div>Anmelden...</div>;
    case "signoutRedirect":
        return <div>Abmelden...</div>;
  }

  if (auth.isLoading) {
    return <div>Laden...</div>;
  }

  if (auth.error) {
      return <div>Oops... {auth.error.message}</div>;
  }

  if (auth.isAuthenticated) {
      return (
      <div>
          Hallo {auth.user?.profile.name}{" "}
          <Button onClick={() => void auth.signoutRedirect()}>Abmelden</Button>
          <Installations apiUri={props.apiUri}></Installations>
      </div>
      );
  }

  return <Button onClick={() => void auth.signinRedirect()}>Anmelden</Button>;

};

export default App;
