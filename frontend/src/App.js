import React, {Component} from 'react';
import {BrowserRouter as Router, Route, Redirect}  from 'react-router-dom';
import {Container} from 'react-bootstrap';
import Login from './login/Login';
import SchnaufFeedContainer from './schnaufFeed/SchnaufFeedContainer';
import Navigation from './navigation/Navigation';
import NotificationProvider from './NotificationProvider';

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

  render() {
    return  (
      <NotificationProvider>
        {(notification) => (
          < Router>
            <Navigation isLoggedIn={this.state.isLoggedIn}/>
            {notification}
            <Container>
              <Route path="/login" render={(props) =>
                <Login
                  onLoginSuccess={(username) => this.onLoginSuccess(username, props)}
                />
              }/>
              <Route path="/feed" render={() =>
                <SchnaufFeedContainer/>
              }/>
              {!this.state.isLoggedIn && <Redirect to="login"/>}
            </Container>
          </Router>
        )}
      </NotificationProvider>
    )
  }
}

export default App;