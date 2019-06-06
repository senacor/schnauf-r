import React, {Component} from 'react';
import {BrowserRouter as Router, Route, Redirect}  from 'react-router-dom';
import {Container, Alert} from 'react-bootstrap';
import Login from './login/Login';
import SchnaufFeedContainer from './schnaufFeed/SchnaufFeedContainer';
import Navigation from './navigation/Navigation';
import NotifiationContext from './NotificationContext';

class App extends Component {
  state = {
    isLoggedIn : false,
    notification: null
  }

  onLoginSuccess = (username, {history}) => {
    this.setState((prevState) => ({
      ... prevState,
      isLoggedIn: true,
      username
    }));

    history.push('/feed');
  }

  removeNotification = () => {
    this.setState((prevState) => ({
      ...prevState,
      notification: null,
    }));
  }

  addNotification = (notifiactionMessage) => {
    console.log(notifiactionMessage);
    this.setState((prevState) => ({
      ... prevState,
      notification: notifiactionMessage,
    }));
  }

  render() {
    return  (
      <NotifiationContext.Provider value={this.addNotification}>
        <Router>
          <Navigation isLoggedIn={this.state.isLoggedIn}/>
          {this.state.notification &&
            <Alert variant={'danger'}>
              {this.state.notification}
            </Alert>
          }
          <Container>
            <Route path="/login" render ={ (props) =>
              <Login
                onLoginSuccess={(username) => this.onLoginSuccess(username, props)}
              />
            }/>
            <Route path="/feed" render ={ () =>
              <SchnaufFeedContainer />
            }/>
            {!this.state.isLoggedIn  && <Redirect to = "login"/>}
          </Container>
        </Router>
      </NotifiationContext.Provider>
    )
  }

}

export default App;

