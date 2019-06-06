import React from 'react'
import PropTypes from 'prop-types'

const Schnauf = ({schnauf}) => <div>
  <span>
    <span>{schnauf.title}</span>
  </span>
  <span>
    <span>{schnauf.author.displayName}</span>
  </span>
</div>;

Schnauf.propTypes = {
  schnauf: PropTypes.object.isRequired
};

export default Schnauf;