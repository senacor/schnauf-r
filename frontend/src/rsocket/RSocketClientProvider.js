import React, {Component} from 'react';
import { Row, Spinner} from 'react-bootstrap';
import PropTypes from 'prop-types';

import createRSocket from './rSocket';
import createRSocketClient from './rSocketClient';

const RSocketContext = React.createContext('schnaufr-notification');

export const withRSocketClient = (Component) => {
  const RSocketWrapper = (props) =>  (
    <RSocketContext.Consumer>
      { (rSocketClient) => <Component rSocketClient={rSocketClient} {... props} />}
    </RSocketContext.Consumer>
  );
  return RSocketWrapper;
}

class RSocketClientProvider extends Component {

  constructor(props) {
    super(props);
    this.state = {
      rSocketClient: null,
      initialized: false,
      error: false,
    }
  }

  componentDidMount() {
    const {wsSocketUrl} = this.props;

    createRSocket(wsSocketUrl, {
      onComplete: this.onRSocketInitialized,
      onError: this.onRSocketError
    });
  }

  onRSocketInitialized  = (rSocket) => {
    this.setState((previoutState) => ({
      ... previoutState,
      initialized: true,
      rSocketClient: createRSocketClient(rSocket),
    }));
  }

  onRSocketError  = (error) => {
    this.setState((previoutState) => ({
      ... previoutState,
      initialized: false,
      rSocketClient: null,
      error: error,
    }));
  }

  render() {
    if (this.state.error) {
      return (<div>{this.state.error}</div>);
    }

    if (!this.state.initialized) {
      return (
        <Row className="justify-content-md-center">
          <Spinner animation="border" />
        </Row>
      );
    }

    return (
      <RSocketContext.Provider value={this.state.rSocketClient}>
        {this.props.children}
      </RSocketContext.Provider>
    )

  }
}

RSocketClientProvider.propTypes = {
  wsSocketUrl: PropTypes.string.isRequired,
  children: PropTypes.node.isRequired,
}

export default RSocketClientProvider;