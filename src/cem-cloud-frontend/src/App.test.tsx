import { render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { store } from './app/store';
import App from './App';
import { AuthProvider } from 'react-oidc-context';

test('renders loading on startup', () => {
  const { getByText } = render(
    <AuthProvider>
      <Provider store={store}>
        <App />
      </Provider>
    </AuthProvider>
  );

  expect(getByText(/Laden/i)).toBeInTheDocument();
});