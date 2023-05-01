import './App.css';
import { useAuth } from 'react-oidc-context';

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
          <button onClick={() => void auth.removeUser()}>Abmelden</button>
      </div>
      );
  }

  return <button onClick={() => void auth.signinRedirect()}>Anmelden</button>;

};

export default App;
