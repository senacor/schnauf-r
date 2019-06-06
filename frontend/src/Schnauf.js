import React from 'react'
import PropTypes from 'prop-types'
import { Card, Image, Row , Col} from 'react-bootstrap'
import picture from '../public/momann.jpeg'

const Schnauf = ({schnauf}) =>
  <Card>
    <Row>
      <Col md={2}>
        <Image src={picture} roundedCircle />
      </Col>
      <Col md={{ span: 9, offset: 1 }}>
        <Card.Body>
          <Card.Title>{schnauf.title}</Card.Title>
          <Card.Text>
            {schnauf.author.displayName}
          </Card.Text>
        </Card.Body>
      </Col>

    </Row>
  </Card>

Schnauf.propTypes = {
  schnauf: PropTypes.object.isRequired
};

export default Schnauf;