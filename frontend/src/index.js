import React from 'react';
import ReactDOM from 'react-dom';
import './index.scss'
import SchnaufContainer from './SchnaufContainer';
import createRSocket from './rSocketClient'
import createSchnaufClient from './schnaufClient'


createRSocket({url: 'ws://127.0.0.1:8080'}).then(createSchnaufClient(1)).then((subscribe) => {
  ReactDOM.render(<SchnaufContainer schnaufClient={subscribe}/>, document.getElementById('app'));
}).catch(console.log)
