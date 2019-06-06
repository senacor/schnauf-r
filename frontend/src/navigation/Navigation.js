import React, {Component} from 'react';
import {Navbar, Nav} from 'react-bootstrap';
import PropTypes from 'prop-types';
import SchnaufFeedEntry from '../schnaufFeed/SchnaufFeedEntry';
import SchnaufFeed from '../schnaufFeed/SchnaufFeed';

const Navigation = ({isLoggedIn}) =>
  <Navbar bg="dark" variant="dark" sticky="top">
    <Navbar.Brand href="/">SchnaufR</Navbar.Brand>
    <Navbar.Collapse id="basic-navbar-nav">
      <Nav className="mr-auto">
        {isLoggedIn && <Nav.Link href="/feed">Feed</Nav.Link>}
      </Nav>
    </Navbar.Collapse>
  </Navbar>

Navigation.propTypes = {
  isLoggedIn: PropTypes.bool
};

export default Navigation;
