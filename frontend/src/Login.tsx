import {FC, ReactElement, useEffect, useState} from "react";
import logo from './logo.svg';
import './App.css';
import { Location, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from './authorization/AuthProvider';

const Login: FC = (): ReactElement => {

    const [clientId, setClientId] = useState<string | ''>();
    const [clientSecret, setClientSecret] = useState<string | ''>();
    const navigate = useNavigate();
    const location: Location & {state: any} = useLocation();
    const auth = useAuth();
    const from = location.state?.from?.pathname || "/";

    useEffect(() => {
        const urlParams = new URLSearchParams(window.location.search.replace('amp;amp;', ''));
        const code = urlParams.get('code');
        const clientId = urlParams.get('client_id');
        const clientSecret = urlParams.get('client_secret');
        if (!code || !clientId || !clientSecret) {
            return;
        }
        auth.signIn('lchlebda',
                    () => navigate(from, { replace: true }),
                    () => {
                          return fetch('/strava/oauth?' + new URLSearchParams({
                                code: code as string,
                                clientId: clientId as string,
                                clientSecret: clientSecret as string }));
                          }
                    );
    }, []);

    const handleLogin = (): void => {
        window.location.href =
            `https://www.strava.com/oauth/authorize?client_id=${clientId}` +
            '&response_type=code' +
            '&scope=activity:read' +
            `&redirect_uri=`+encodeURIComponent(`http://localhost:3000/login?client_secret=${clientSecret}&client_id=${clientId}`);
    }

    return (
        <div className='App'>
            <h1>Welcome to Hamster App!</h1>
            <header className='App-header'>
                <img src={ logo } className='App-logo' alt='logo'/>
                <div className='App-intro'>
                    <h2>Please login with your client data:</h2>
                </div>
                <div>
                    <label>
                        Client id:
                        <input type="number" value={ clientId } onChange={ event => setClientId(event.target.value) }/>
                    </label>
                </div>
                <div>
                    <label>
                        Client secret:
                        <input type="text" value={ clientSecret } onChange={ event => setClientSecret(event.target.value) }/>
                    </label>
                </div>
                <div>
                    <input type="button" value="Login" onClick={ handleLogin }/>
                </div>
            </header>
        </div>
    );
}
export default Login;