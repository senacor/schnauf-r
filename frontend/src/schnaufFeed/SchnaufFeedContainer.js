import React, {Component} from 'react';
import {Spinner, Row} from 'react-bootstrap';
import SchnaufFeed from './SchnaufFeed';
import createRequestStreamClient from '../requestStreamClient'
import createRSocket from '../rSocketClient'
import {withNotification} from '../NotificationProvider';
import PropTypes from 'prop-types';

class SchnaufFeedContainer extends Component {

  constructor(props) {
    super(props);
    this.state = {
      schnaufs :[],
      loading: true,

    }
    this.unsubscribe = () => {};
  }

  onNext = (schnauf) => {
    this.setState((prevState) => ({
      ...prevState,
      loading: false,
      schnaufs: [schnauf, ...prevState.schnaufs ]
    }));
  }

  onError = () => {
    this.setState((prevState) => ({
      ...prevState,
      loading: false
    }));
    this.props.addNotification('Fehler beim Laden');
  }

  onLimitReached = (requestNext) => {
    setTimeout(requestNext, 2000);
  }

  componentDidMount = async () => {
    try {
      const socket = await createRSocket({url: 'ws://127.0.0.1:8080'})
      const subscribe = await createRequestStreamClient(1)(socket)
      this.unsubscribe = subscribe({
        onNext: this.onNext,
        onError: this.onError,
        onLimitReached: this.onLimitReached,
      });
    } catch (error) {
      this.onError();
    }
  }

  componentWillUnmount = () => {
    this.unsubscribe();
  }

  render() {
    if (this.state.loading) {
      return (
        <Row className="justify-content-md-center">
          <Spinner animation="border" />
        </Row>
      );
    }
    return (
      <SchnaufFeed schnaufs={this.state.schnaufs}/>
    )
  }

}

SchnaufFeedContainer.propTypes = {
  addNotification: PropTypes.func.isRequired // injected by withNotification
};

export default withNotification(SchnaufFeedContainer);