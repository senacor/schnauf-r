import React from 'react'
import PropTypes from 'prop-types'
import Schnauf from './Schnauf';

const SchnaufList = ({schnaufs}) => {
  const schnaufEntries = schnaufs.map((schnauf) => <li className="mdl-list__item mdl-list__item--two-line" key={schnauf.id}><Schnauf schnauf={schnauf}/></li>)
  return <ul className="demo-list-two mdl-list">{schnaufEntries}</ul>
}

SchnaufList.propTypes = {
  schnaufs: PropTypes.string.isRequired
};

export default SchnaufList;