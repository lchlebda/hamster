import { Component } from "react";
import logo from './logo.svg';
import './App.css';

class App extends Component {
  state = {
    activities: []
  };

  async componentDidMount() {
    const response = await fetch('/activities');
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
                    { activity.name } | { activity.time } | { activity.regenerationTime }
                  </div>
              )}
            </div>
          </header>
        </div>
    );
  }
}
export default App;