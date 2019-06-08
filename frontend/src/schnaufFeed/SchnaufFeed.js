import React from 'react'
import { ListGroup, ListGroupItem } from 'react-bootstrap'
import PropTypes from 'prop-types'
import SchnaufFeedEntry from './SchnaufFeedEntry';

const SchnaufFeed = ({schnaufs}) => {
  const schnaufEntries = schnaufs.map((schnauf) =>
    <ListGroupItem key={schnauf.id}>
      <SchnaufFeedEntry schnauf={schnauf}/>
    </ListGroupItem>
  )
  return <ListGroup>{schnaufEntries}</ListGroup>
}

SchnaufFeed.propTypes = {
  schnaufs: PropTypes.arrayOf(SchnaufFeedEntry).isRequired
};

export default SchnaufFeed;