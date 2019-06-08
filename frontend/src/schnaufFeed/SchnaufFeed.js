import React from 'react'
import { ListGroup, ListGroupItem } from 'react-bootstrap'
import PropTypes from 'prop-types'
import SchnaufFeedEntry from './SchnaufFeedEntry';

const SchnaufFeed = ({schnaufs}) => {

  if (!schnaufs.length) {
    return (<div>Keine Schnaufs</div>);
  }

  const schnaufEntries = schnaufs.map((schnauf) =>
    <ListGroupItem key={schnauf.id}>
      <SchnaufFeedEntry schnauf={schnauf}/>
    </ListGroupItem>
  )
  return <ListGroup>{schnaufEntries}</ListGroup>
}

SchnaufFeed.propTypes = {
  schnaufs: PropTypes.array.isRequired
};

export default SchnaufFeed;