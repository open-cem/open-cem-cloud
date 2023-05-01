import React from 'react';
import { createRoot } from 'react-dom/client';
import { Provider } from 'react-redux';
import { store } from './app/store';
import App from './app/App';
import './index.css';
import { AuthProvider } from 'react-oidc-context';
import { User } from 'oidc-client-ts';

const container = document.getElementById('root')!;
const root = createRoot(container);

const onSigninCallback = (_user: User | void): void => {
  window.history.replaceState(
  {},
  document.title,
  window.location.pathname
  )
}

const oidcConfig = {
  authority: "http://localhost:2000/",
  client_id: "react-app",
  redirect_uri: "http://localhost:3000",
  metadata: {
    authorization_endpoint: `http://localhost:2000/realms/cem-cloud/protocol/openid-connect/auth`,
    token_endpoint:         `http://localhost:2000/realms/cem-cloud/protocol/openid-connect/token`
  },
  scope: "openid",
  onSigninCallback: onSigninCallback
};

root.render(
  <React.StrictMode>
    <AuthProvider {... oidcConfig}>
      <Provider store={store}>
        <App />
      </Provider>
    </AuthProvider>
  </React.StrictMode>
);
