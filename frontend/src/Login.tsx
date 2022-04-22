import {ChangeEvent, Component, SyntheticEvent} from "react";
import logo from './logo.svg';
import './App.css';

interface ILogin {
    clientId: string;
    clientSecret: string;
}

class Login extends Component<{}, ILogin> {

    constructor(props: ILogin) {
        super(props);
        this.state = { clientId: '', clientSecret: '' };

        this.handleClientId = this.handleClientId.bind(this);
        this.handleClientSecret = this.handleClientSecret.bind(this);
        this.handleLogin = this.handleLogin.bind(this);
    }

    public handleClientId = (event: ChangeEvent<HTMLInputElement>): void => {
        this.setState({clientId: event.target.value});
    }

    public handleClientSecret = (event: ChangeEvent<HTMLInputElement>): void => {
        this.setState({clientSecret: event.target.value});
    }

    public handleLogin = (): void => {
        window.location.href =
            `https://www.strava.com/oauth/authorize?client_id=${this.state.clientId}` +
            '&response_type=code' +
            '&scope=activity:read' +
            `&redirect_uri=http://localhost:3000/login?client_secret=${this.state.clientSecret}`;
    }

    render() {
        return (
            <div className='App'>
                <h1>Welcome to Hamster App!</h1>
                <header className='App-header'>
                    <img src={ logo } className='App-logo' alt='logo' />
                    <div className='App-intro'>
                        <h2>Please login with your client data:</h2>
                    </div>
                    <div>
                        <label>
                            Client id:
                            <input type="number" value={this.state.clientId} onChange={this.handleClientId} />
                        </label>
                    </div>
                    <div>
                        <label>
                            Client secret:
                            <input type="text" value={this.state.clientSecret} onChange={this.handleClientSecret} />
                        </label>
                    </div>
                    <div>
                        <input type="button" value="Login" onClick={this.handleLogin}/>
                    </div>
                </header>
            </div>
        );
    }
}
export default Login;