import React from 'react';
import { createRoot } from 'react-dom/client';
import { Provider } from 'react-redux';
import { store } from './app/store';
import App from './App';
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

fetch('application.json')
  .then(response => response.json())
  .then(json => {
    root.render(
      <React.StrictMode>
        <AuthProvider {...json.oidcConfig} onSigninCallback={onSigninCallback}>
          <Provider store={store}>
            <App apiUri={json.apiUri} />
          </Provider>
        </AuthProvider>
      </React.StrictMode>
    );
  })
  .catch(reason => console.error('application.json not found or error in configuration.', reason));
