import React, {Component} from 'react';
import PropTypes from 'prop-types'
import {withRSocketClient} from '../rsocket/RSocketClientProvider'
import Login from './Login';

class LoginContainer extends Component {

  constructor(props) {
    super(props);
    this.state = {}
  }

  login = async (username) => {
    const {rSocketClient} = this.props;
    const result = await rSocketClient.requestResponse({ data: { username }, metadata: { operation: 'findUserByUsername' }})
    this.props.onLoginSuccess(username)
  }

  render() {
    return (
      <Login onLogin={this.login} />
    )
  }

}

LoginContainer.propTypes = {
  onLoginSuccess: PropTypes.func.isRequired,
  rSocketClient: PropTypes.object.isRequired,
};

export default withRSocketClient(LoginContainer);

