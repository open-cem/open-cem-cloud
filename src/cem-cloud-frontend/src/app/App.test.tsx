import React from 'react';
import { render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { store } from './store';
import App from './App';
import { AuthProvider } from 'react-oidc-context';

test('renders learn react link', () => {
  const { getByText } = render(
    <AuthProvider>
      <Provider store={store}>
        <App />
      </Provider>
    </AuthProvider>
  );

  expect(getByText(/Loading/i)).toBeInTheDocument();
});
