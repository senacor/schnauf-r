import React from 'react';
import {Navbar, Nav} from 'react-bootstrap';
import PropTypes from 'prop-types';

const Navigation = ({loggedIn}) =>
  <Navbar bg="dark" variant="dark" sticky="top">
    <Navbar.Brand href="/">SchnaufR</Navbar.Brand>
    <Navbar.Collapse id="basic-navbar-nav">
      <Nav className="mr-auto">
        {loggedIn && <Nav.Link href="/feed">Feed</Nav.Link>}
      </Nav>
    </Navbar.Collapse>
  </Navbar>

Navigation.propTypes = {
  loggedIn: PropTypes.bool
};

export default Navigation;
