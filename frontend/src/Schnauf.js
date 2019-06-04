import React from 'react'
import PropTypes from 'prop-types'

const Schnauf = ({reason}) => <h1>{reason + '-schnauf'}</h1>;

Schnauf.propTypes = {
  reason: PropTypes.string.isRequired
};

export default Schnauf;