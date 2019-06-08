import React, {Component} from 'react';
import PropTypes from 'prop-types'
import { Form, Button } from 'react-bootstrap'

export default class Login extends Component {

  constructor(props) {
    super(props)
    this.state = {
      username: ''
    }
  }

  setUserName = (event) => {
    this.setState({
      username: event.target.value
    })
  }

  render() {
    return (
      <Form>
        <Form.Group controlId="formLogin">
          <Form.Label>Username</Form.Label>
          <Form.Control type="text" placeholder="Username" onChange={this.setUserName} value={this.state.username}/>
        </Form.Group>
        <Button variant="primary" type="submit" onClick={() => this.props.onLogin(this.state.username)} disabled={!this.state.username}>
          Go and schnauf
        </Button>
      </Form>
    )
  }
}

Login.propTypes = {
  onLogin: PropTypes.func.isRequired
};
