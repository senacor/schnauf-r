import React, {Component} from 'react';
import PropTypes from 'prop-types'
import createRSocketClient from '../rsocket/rSocketClient'
import Login from './Login';

class LoginContainer extends Component {

  constructor(props) {
    super(props);
    this.state = {}
  }

  login = async (username) => {
    const { requestResponse } = await createRSocketClient('ws://10.56.4.28:8080')
    const result = await requestResponse({ data: { username }, metadata: { operation: 'findUserByUsername' }})
    console.log(result)
    this.props.onLoginSuccess(username)
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

