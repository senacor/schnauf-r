import React from 'react';
import ReactDOM from 'react-dom';

import Schnauf from './Schnauf';

import createRSocket from './schnaufRSocket';

createRSocket({url: 'ws://127.0.0.1:8080'}).then((socket) => {
  console.log(socket);
}).catch(console.error)

ReactDOM.render(<Schnauf reason={'react'}/>, document.getElementById('app'));