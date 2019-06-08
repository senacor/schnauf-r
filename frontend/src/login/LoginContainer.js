import React, {Component} from 'react';
import PropTypes from 'prop-types'
import rSocketClient from '../rsocket/rSocketClient'
import Login from './Login';

class LoginContainer extends Component {

  constructor(props) {
    super(props);
    this.state = {}
  }

  login = async (username) => {
    // const client = await rSocketClient({url: 'ws://127.0.0.1:8080'})
    // const result = await client.requestResponse({ data: { username }})
    this.props.onLoginSuccess(username)
  }

  componentDidMount = async () => {
    this.socket = await rSocketClient({url: 'ws://127.0.0.1:8080'})
  }

  render() {
    return (
      <Login onLogin={this.login} />
    )
  }

}

export default LoginContainer;

LoginContainer.propTypes = {
  onLoginSuccess: PropTypes.func.isRequired
};

