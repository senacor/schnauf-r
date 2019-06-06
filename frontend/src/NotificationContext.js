import React from 'react';

const NotifiationContext = React.createContext('schnaufr-notification');

export const withNotification = (Component) => (props) =>  (
  <NotifiationContext.Consumer>
    { (addNotification) => <Component addNotification={addNotification} {... props} />}
  </NotifiationContext.Consumer>
);

export default NotifiationContext;