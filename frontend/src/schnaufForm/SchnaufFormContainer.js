import React, {Component} from 'react'
import PropTypes from 'prop-types'
import SchnaufForm from './SchnaufForm'
import {withRSocketClient} from '../rsocket/RSocketClientProvider'

class SchnaufFormContainer extends Component {

   submitSchnauf = async (title) => {
     const {onSchnaufSuccess, onSchnaufError, userId, rSocketClient} = this.props
     try {
       await rSocketClient.requestResponse({data: {submitter: userId, title}, metadata: {operation: 'createSchnauf'}})
       onSchnaufSuccess()
     } catch (error) {
       onSchnaufError()
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
  rSocketClient: PropTypes.object.isRequired, // injected by withRSocketClient
}

export default withRSocketClient(SchnaufFormContainer)