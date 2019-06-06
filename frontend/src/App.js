import React, {Component} from 'react';
import {BrowserRouter as Router, Route, Redirect}  from 'react-router-dom';
import Login from './login/Login';
import SchnaufFeedContainer from './schnaufFeed/SchnaufFeedContainer';
import Navigation from './navigation/Navigation';

class App extends Component {
  state = {
    isLoggedIn : false
  }

  onLoginSuccess = (username, {history}) => {
    this.setState((prevState) => ({
      ... prevState,
      isLoggedIn: true,
      username
    }));

    history.push('/feed');
  }
  render() {
    return (
      <Router>
        <Navigation/>
        <Route path="/login" render ={ (props) =>
          <Login
            onLoginSuccess={(username) => this.onLoginSuccess(username, props)}
          />
        }/>
        <Route path="/feed" render ={ () =>
          <SchnaufFeedContainer />
        }/>
        {!this.state.isLoggedIn  && <Redirect to = "login"/>}
      </Router>
    )
  }

}

export default App;

