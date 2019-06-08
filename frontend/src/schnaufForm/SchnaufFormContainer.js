import React, {Component} from 'react';
import PropTypes from 'prop-types'
import SchnaufForm from './SchnaufForm';

class SchnaufFormContainer extends Component {

   submitSchnauf = (schnaufText) => {
     const {onSchnaufSuccess, onSchnaufError, userId} = this.props;
     console.log(userId, schnaufText);

     onSchnaufSuccess();

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