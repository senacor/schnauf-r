import React, {Component} from 'react';
import PropTypes from 'prop-types'
import { Form, Button } from 'react-bootstrap'
import { prependListener } from 'cluster';

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
        <Button variant="primary" type="submit" onClick={() => this.props.onLoginSuccess(this.state.username)} disabled={!this.state.username}>
          Go and schnauf
        </Button>
      </Form>
    )
  }
}

Login.propTypes = {
  onLoginSuccess: PropTypes.func.isRequired
};
