import React, {Component} from 'react';
import {BrowserRouter as Router, Route, Redirect}  from 'react-router-dom';
import {Container} from 'react-bootstrap';
import LoginContainer from './login/LoginContainer';
import SchnaufFeedContainer from './schnaufFeed/SchnaufFeedContainer';
import Navigation from './navigation/Navigation';
import NotificationProvider from './NotificationProvider';
import SchnaufFormContainer from './schnaufForm/SchnaufFormContainer';

class App extends Component {
  state = {
    isLoggedIn : false,
  }

  onLoginSuccess = (userId, {history}) => {
    this.setState((prevState) => ({
      ... prevState,
      isLoggedIn: true,
      userId
    }));

    history.push('/feed');
  }

  navigateTo = ({history}, target) => {
    return () => history.push(target)
  }

  logout = () => {
    this.setState((prevState) => ({
      ...prevState,
      isLoggedIn: false
    }))
  }

  render() {
    return  (
      <NotificationProvider>
        {(notification) => (
          < Router>
            <Navigation loggedIn={this.state.isLoggedIn}/>
            {notification}
            <Container>
              <Route path="/login" render={(props) =>
                <LoginContainer
                  onLoginSuccess={(username) => this.onLoginSuccess(username, props)}
                />
              }/>
              <Route exact path="/feed" render={() => (<SchnaufFeedContainer/>)}/>
              <Route exact path="/feed/schnauf" render={(props) =>
                <SchnaufFormContainer
                  userId={this.state.userId}
                  onSchnaufSuccess={this.navigateTo(props, '/feed')}
                  onSchnaufError={this.logout}
                />
              } />
              {!this.state.isLoggedIn && <Redirect to="/login"/>}
            </Container>
          </Router>
        )}
      </NotificationProvider>
    )
  }
}

export default App;