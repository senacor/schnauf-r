import React, {Component} from 'react'
import {BrowserRouter as Router, Route, Redirect}  from 'react-router-dom'
import {Container} from 'react-bootstrap'
import LoginContainer from './login/LoginContainer'
import SchnaufFeedContainer from './schnaufFeed/SchnaufFeedContainer'
import Navigation from './navigation/Navigation'
import NotificationProvider from './NotificationProvider'
import SchnaufFormContainer from './schnaufForm/SchnaufFormContainer'
import RSocketClientProvider from './rsocket/RSocketClientProvider'

const PATH = {
  LOGIN: '/login',
  FEED: '/feed',
  SCHNAUF: '/feed/schnauf',
}

const WS_SOCKET_URL = `ws://schnau.fr:8080`

class App extends Component {
  state = {
    isLoggedIn : false,
  }

  onLoginSuccess = (user, {history}) => {

    this.setState((prevState) => ({
      ... prevState,
      isLoggedIn: true,
      userId: user.id
    }))

    history.push(PATH.FEED)
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
          <Router>
            <Navigation loggedIn={this.state.isLoggedIn}/>
            {notification}
            <Container>
              <RSocketClientProvider wsSocketUrl={WS_SOCKET_URL}>
                <Route path={PATH.LOGIN} render={(props) =>
                  <LoginContainer
                    onLoginSuccess={(username) => this.onLoginSuccess(username, props)}
                  />
                }/>
                <Route exact path={PATH.FEED} render={() => (<SchnaufFeedContainer/>)}/>
                <Route exact path={PATH.SCHNAUF} render={(props) =>
                  <SchnaufFormContainer
                    userId={this.state.userId}
                    onSchnaufSuccess={this.navigateTo(props, '/feed')}
                    onSchnaufError={this.logout}
                  />
                } />
                {!this.state.isLoggedIn && <Redirect to={PATH.LOGIN}/>}
              </RSocketClientProvider>
            </Container>
          </Router>
        )}
      </NotificationProvider>
    )
  }
}

export default App