import React, {Component} from 'react';
import PropTypes from 'prop-types'
import SchnaufForm from './SchnaufForm';
import createRSocketClient from '../rsocket/rSocketClient';

class SchnaufFormContainer extends Component {

   submitSchnauf = async (title) => {
     const {onSchnaufSuccess, onSchnaufError, userId} = this.props;
     try {
       const {requestResponse} = await createRSocketClient('ws://35.246.79.96:8080')
       await requestResponse({data: {submitter: userId, title}, metadata: {operation: 'createSchnauf'}})
       onSchnaufSuccess();
     } catch (error) {
       onSchnaufError();
     }
   }

   render() {
     return (
       <SchnaufForm onSubmit={this.submitSchnauf}/>
     )
   }
}

SchnaufFormContainer.propTypes = {
  userId : PropTypes.string.isRequired,
  onSchnaufSuccess: PropTypes.func.isRequired,
  onSchnaufError: PropTypes.func.isRequired,
}

export default SchnaufFormContainer;