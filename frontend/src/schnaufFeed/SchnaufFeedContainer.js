import React, {Component} from 'react';
import {Spinner, Row} from 'react-bootstrap';
import SchnaufFeed from './SchnaufFeed';
import {withNotification} from '../NotificationProvider';
import {withRSocketClient} from '../rsocket/RSocketClientProvider'
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

  componentDidMount() {
    const {rSocketClient} = this.props;
      const requestSize = 1
      const requestData = {data: {}}

      this.unsubscribe = rSocketClient.subscribeRequestStream(requestData, requestSize,{
      onNext: this.onNext,
      onError: this.onError,
      onLimitReached: this.onLimitReached,
    });

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
  addNotification: PropTypes.func.isRequired,  // injected by withNotification
  rSocketClient: PropTypes.object.isRequired, // injected by withRSocketClient
};

export default withNotification(withRSocketClient(SchnaufFeedContainer));