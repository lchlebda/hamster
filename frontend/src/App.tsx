import { Component } from "react";
import logo from './logo.svg';
import './App.css';

export type Activity = {
  id: number;
  name: string;
  distance: number;
}

class App extends Component {
  state = {
    activities: [] as Activity[],
    authorized: false,
  };

  async componentDidMount() {
    const response = await fetch('/activities', {
      headers: {
        'ACCESS_TOKEN': '625aa1aba055b8f0cfe9347665d1d28667b22e82',
      }
    });
    const body = await response.json();
    this.setState({ activities: body });
  }

  render() {
    const { activities } = this.state;
    return (
        <div className='App'>
          <header className='App-header'>
            <img src={ logo } className='App-logo' alt='logo' />
            <div className='App-intro'>
              <h2>Activities</h2>
              { activities.map(activity =>
                  <div key={ activity.id }>
                    { activity.name } | { activity.distance }
                  </div>
              )}
            </div>
          </header>
        </div>
    );
  }
}
export default App;