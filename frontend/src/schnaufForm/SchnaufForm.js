import React, {useState} from 'react';
import {Button, Form} from 'react-bootstrap';
import PropTypes from 'prop-types'

const SchnaufForm = ({onSubmit}) => {
  const [schnaufText, setSchnaufText] = useState('');

  const submit = (event) => {
    event.preventDefault();
    onSubmit(schnaufText);
  }

  return (
    <Form onSubmit={submit}>
      <Form.Group controlId="formLogin">
        <Form.Label>Schnauf-Text</Form.Label>
        <Form.Control type="text" placeholder="Dein Schnauf" onChange={setSchnaufText} value={schnaufText}/>
      </Form.Group>
      <Button variant="primary" type="submit"  disabled={!schnaufText}>
            Schnauf!
      </Button>
    </Form>
  );

}

SchnaufForm.propTypes = {
  onSubmit: PropTypes.func.isRequired,
};

export default SchnaufForm;