import React, {Component} from 'react';
import SchnaufFeed from './SchnaufFeed';
import createRequestStreamClient from '../requestStreamClient'
import createRSocket from '../rSocketClient'

export default class SchnaufFeedContainer extends Component {

  constructor(props) {
    super(props);
    this.state = {
      schnaufs :[],
      isLoading: true
    }
    this.unsubscribe = () => {};
  }

  onNext = (schnauf) => {
    this.setState((prevState) => ({
      ...prevState,
      isLoading: false,
      schnaufs: [schnauf, ...prevState.schnaufs ]
    }));
  }

  onError = (error) => {
    console.error(error);
  }

  onLimitReached = (requestNext) => {
    setTimeout(requestNext, 2000);
  }

  componentDidMount = async () => {
    const socket = await createRSocket({url: 'ws://127.0.0.1:8080'})
    const subscribe = await createRequestStreamClient(1)(socket)
    this.unsubscribe = subscribe({
      onNext: this.onNext,
      onError: this.onError,
      onLimitReached: this.onLimitReached,
    });
  }

  componentWillUnmount = () => {
    this.unsubscribe();
  }

  render() {
    return (
      <SchnaufFeed schnaufs={this.state.schnaufs}/>
    )
  }

}
