import React from 'react'
import PropTypes from 'prop-types'

const Schnauf = ({schnauf}) => <div>
  <span className="mdl-list__item-primary-content">
    <span>{schnauf.title}</span>
  </span>
  <span className="mdl-list__item-secondary-content">
    <span className="mdl-list__item-secondary-info">{schnauf.author.displayName}</span>
  </span>
</div>;

Schnauf.propTypes = {
  schnauf: PropTypes.object.isRequired
};

export default Schnauf;