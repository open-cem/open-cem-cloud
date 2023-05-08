import './App.css';
import { useAuth } from 'react-oidc-context';
import { Counter } from './counter/Counter';
import Demo from './demo/Demo';
import SendEvent from './demo/SendEvent'

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
          <Demo />
          <Counter />
          <SendEvent />
      </div>
      );
  }

  return <button onClick={() => void auth.signinRedirect()}>Anmelden</button>;

};

export default App;
