import React from 'react'
import PropTypes from 'prop-types'
import Button from '@material-ui/core/Button'

const Schnauf = ({reason}) => <h1>{reason + '-schnauf'} <Button> Hello World</Button></h1>;

Schnauf.propTypes = {
  reason: PropTypes.string.isRequired
};

export default Schnauf;