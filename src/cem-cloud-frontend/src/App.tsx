import "primereact/resources/primereact.min.css";
import 'primeicons/primeicons.css';
import "./theme.css";
import './App.css';
import { useAuth } from 'react-oidc-context';
import { Button } from 'primereact/button'
import Installations from "./installation/Installations";

function App() {
  const auth = useAuth();

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
          <Button onClick={() => void auth.removeUser()}>Abmelden</Button>
          <Installations></Installations>
      </div>
      );
  }

  return <Button onClick={() => void auth.signinRedirect()}>Anmelden</Button>;

};

export default App;
