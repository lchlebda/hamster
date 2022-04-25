import { FC, ReactElement, useState } from "react";
import logo from './logo.svg';
import './App.css';

const Login: FC = (): ReactElement => {

    const [clientId, setClientId] = useState<string | ''>();
    const [clientSecret, setClientSecret] = useState<string | ''>();

    const handleLogin = (): void => {
        window.location.href =
            `https://www.strava.com/oauth/authorize?client_id=${clientId}` +
            '&response_type=code' +
            '&scope=activity:read' +
            `&redirect_uri=http://localhost:3000/login?client_secret=${clientSecret}`;
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