import React from 'react'
import PropTypes from 'prop-types'
import {Card} from 'react-bootstrap'

const Schnauf = ({schnauf}) =>
<Card>
  <Card.Body>
    <Card.Title>{schnauf.title}</Card.Title>
    <Card.Text>
      {schnauf.author.displayName}
    </Card.Text>
  </Card.Body>
</Card>

Schnauf.propTypes = {
  schnauf: PropTypes.object.isRequired
};

export default Schnauf;