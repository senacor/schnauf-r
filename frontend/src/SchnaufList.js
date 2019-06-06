import React from 'react'
import { ListGroup, ListGroupItem } from 'react-bootstrap'
import PropTypes from 'prop-types'
import Schnauf from './Schnauf';

const SchnaufList = ({schnaufs}) => {
  const schnaufEntries = schnaufs.map((schnauf) => <ListGroupItem key={schnauf.id}><Schnauf schnauf={schnauf}/></ListGroupItem>)
  return <ListGroup>{schnaufEntries}</ListGroup>
}

SchnaufList.propTypes = {
  schnaufs: PropTypes.string.isRequired
};

export default SchnaufList;