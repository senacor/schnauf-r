import React from 'react';
import ReactDOM from 'react-dom';
import './index.scss'
import Schnaufs from './SchnaufList';
import createRSocket from './schnaufSocket'

createRSocket({url: 'ws://127.0.0.1:8080'}).then((socket) => {
  console.log(socket.requestStream)
  const flowable = socket.requestStream({ data: {}});

  let subscription = null;
  let subscriptionCounter = 0;
  let requestSize = 1;

  flowable.subscribe({
    onNext: (data) => {
      subscriptionCounter++;
      if (subscriptionCounter >=requestSize ) {
        subscriptionCounter = 0;
        subscription.request(requestSize);
      }
      console.log(data);
    },
    onError: console.log,
    onSubscribe: (sub) => {
      subscription = sub;
      sub.request(requestSize)
    }
  });

}).catch(console.log)

ReactDOM.render(<Schnaufs schnaufs={[{ id: '1', title: 'ABC', author: { displayName: 'Michael Ohmann' }}, { id: '2', title: 'DEF', author: { displayName: 'Matthias Peters' } }]}/>, document.getElementById('app'));