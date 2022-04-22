import { Component } from "react";
import './App.css';

export type Activity = {
  id: number;
  name: string;
  distance: number;
}

class App extends Component {
  state = {
    accessToken: '',
    activities: [] as Activity[],
    authorized: false,
  };

  async componentDidMount() {
    const code = new URLSearchParams(window.location.search).get('code');
    this.setState({ accessToken: code });
    const response = await fetch('/activities', {
      headers: {
        'ACCESS_TOKEN': code as string,
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