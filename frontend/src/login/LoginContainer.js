import React, {Component} from 'react'
import PropTypes from 'prop-types'
import {withRSocketClient} from '../rsocket/RSocketClientProvider'
import Login from './Login'

class LoginContainer extends Component {

  constructor(props) {
    super(props)
    this.state = {}
  }

  login = async (username) => {
    const {rSocketClient} = this.props
    try {
      const result = await rSocketClient.requestResponse({ data: { username }, metadata: { operation: 'findUserByUsername' }})
      this.props.onLoginSuccess(result.data.id)
    } catch (e) {
      alert(`error while trying to retrieve user: ${e}`)
    }
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
}

export default withRSocketClient(LoginContainer)

