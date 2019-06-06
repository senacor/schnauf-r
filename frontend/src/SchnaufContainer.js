import React, {Component} from 'react';
import PropTypes from 'prop-types'
import SchnaufList from './SchnaufList';

export default class SchnaufContainer extends Component {

  constructor(props) {
    super(props);
    this.state = {
      schnaufs :[],
      isLoading: true
    }
  }

  onNext = (schnauf) => {
    this.setState((prevState) => ({
      ...prevState,
      isLoading: false,
      schnaufs: [schnauf.data, ...prevState.schnaufs ]
    }));
  }

  onError = (error) => {
    console.error(error);
  }

  onLimitReached = (requestNext) => {
    setTimeout(requestNext, 2000);
  }

  componentDidMount() {
    this.unsubscribe = this.props.schnaufClient({
      onNext: this.onNext,
      onError: this.onError,
      onLimitReached: this.onLimitReached,
    });
  }

  componentWillUnmount() {
    this.unsubscribe()
  }

  render() {
    return (
      <SchnaufList schnaufs={this.state.schnaufs}/>
    )
  }

}

SchnaufContainer.propTypes = {
  schnaufClient: PropTypes.func.isRequired
}
