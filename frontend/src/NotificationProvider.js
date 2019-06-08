import React, {Component} from 'react';
import {Alert} from 'react-bootstrap';
import PropTypes from 'prop-types';

const NotifiationContext = React.createContext('schnaufr-notification');

export const withNotification = (Component) => {
  const NotificationWrapper = (props) =>  (
    <NotifiationContext.Consumer>
      { (addNotification) => <Component addNotification={addNotification} {... props} />}
    </NotifiationContext.Consumer>
  );

  return NotificationWrapper;
}

class NotificationProvider extends Component {
  state = {
    notification: null
  }

  removeNotification = () => {
    this.setState((prevState) => ({
      ...prevState,
      notification: null,
    }));
  }

  addNotification = (notifiactionMessage) => {
    this.setState((prevState) => ({
      ... prevState,
      notification: notifiactionMessage,
    }));

    setTimeout(this.removeNotification, 5000);
  }

  renderNotification = () => {
    if (!this.state.notification) {
      return <div/>;
    }
    return (
      <Alert variant={'danger'}>
        {this.state.notification}
      </Alert>
    );
  }

  render() {
    return (
      <NotifiationContext.Provider value={this.addNotification}>
        {this.props.children(this.renderNotification())}
      </NotifiationContext.Provider>
    );
  }
}

NotificationProvider.propTypes = {
  children: PropTypes.func.isRequired,
}

export default NotificationProvider;