import { render } from '@testing-library/react';
import App from './App';
import { AuthProvider } from 'react-oidc-context';

test('renders loading on startup', () => {
  const { getByText } = render(
    <AuthProvider>
        <App />
    </AuthProvider>
  );

  expect(getByText(/Laden/i)).toBeInTheDocument();
});