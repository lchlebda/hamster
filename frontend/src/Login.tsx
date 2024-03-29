import { FC, ReactElement, useEffect, useState } from 'react';
import './App.css';
import { Location, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from './authorization/AuthProvider';
import { Spinner } from 'react-bootstrap';

const Login: FC = (): ReactElement => {

    const [clientId, setClientId] = useState<string | ''>('74001');
    const [clientSecret, setClientSecret] = useState<string | ''>('f6cd43178a104002c63442870561330229a0bbfe');
    const [loading, setLoading] = useState<boolean>(false);
    const navigate = useNavigate();
    const location: Location & {state: any} = useLocation();
    const auth = useAuth();
    const from = location.state?.from?.pathname || "/";

    useEffect(() => {
        const urlParams = new URLSearchParams(window.location.search.replace('amp;amp;', ''));
        const code = urlParams.get('code');
        const clientId = '74001'; //urlParams.get('client_id');
        const clientSecret = urlParams.get('client_secret');
        if (!code || !clientId || !clientSecret) {
            return;
        }
        setLoading(true);
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
        <>
        { loading ? <div className='login-spinner-div'><Spinner animation={"border"} variant={"primary"}
                                              style={{ width: '5rem', height: '5rem'}} /></div> :
                <div className='App'>
                    <div className='App-intro'>
                        <h2>Please login with your client data:</h2>
                    </div>
                    <div>
                        <label>
                            Client id:
                            <input type="text" value={clientId} onChange={event => setClientId(event.target.value)}/>
                        </label>
                    </div>
                    <div>
                        <label>
                            Client secret:
                            <input type="text" value={clientSecret}
                                   onChange={event => setClientSecret(event.target.value)}/>
                        </label>
                    </div>
                    <div>
                        <input type="button" value="Login" onClick={handleLogin}/>
                    </div>
                </div>
        }
        </>
    );
}
export default Login;