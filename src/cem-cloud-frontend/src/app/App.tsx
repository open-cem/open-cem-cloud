import "primereact/resources/primereact.min.css";
import 'primeicons/primeicons.css';
import "../theme.css";
import './App.css';
import { useAuth, hasAuthParams } from 'react-oidc-context';
import { Button } from 'primereact/button'
import { useEffect } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Layout from "../pages/Layout";
import Home from "../pages/Home";
import Installations from "../installation/Installations";
import NotFound from "../pages/NotFound";
import InstallationConfig from "../installation/config/InstallationConfig";
import Installation from "../installation/Installation";
import { setApiUri } from "./AppSettings";
import CommunicationChannel from "../communicationChannel/communicationChannel";
import Component from "../component/Component";

function App(props: any) {
  setApiUri(props.apiUri);
  const auth = useAuth();

  useEffect(() => {
    if (!hasAuthParams() &&
      !auth.isAuthenticated && !auth.activeNavigator && !auth.isLoading) {
      auth.signinRedirect();
    }
  }, [auth, auth.isAuthenticated, auth.activeNavigator, auth.isLoading, auth.signinRedirect]);

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
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<Home />} />
            <Route path="installations" element={<Installations />} />
            <Route path="installations/:installationId" element={<Installation />} />
            <Route path="installations/:installationId/config" element={<InstallationConfig />} />
            <Route path="communicationChannels/:channelId" element={<CommunicationChannel />} />
            <Route path="components/:componentId" element={<Component />} />
            <Route path="*" element={<NotFound />} />
          </Route>
        </Routes>
      </BrowserRouter>
    );
  }

  return <Button onClick={() => void auth.signinRedirect()}>Anmelden</Button>;

};

export default App;
