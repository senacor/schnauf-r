import {JsonSerializers, RSocketClient} from 'rsocket-core';
import RSocketWebSocketClient from 'rsocket-websocket-client';

const createRSocket = ({url}) => {
  const rSocketClient = new RSocketClient({
    serializers: JsonSerializers,
    setup: {
      keepAlive: 60000,
      lifetime: 180000,
      // format of `data`
      dataMimeType: 'binary',
      // format of `metadata`
      metadataMimeType: 'binary',
    },
    transport: new RSocketWebSocketClient({url, debug: true })
  })

  return new Promise((resolve, reject) => {
    rSocketClient.connect().subscribe({
      onComplete: resolve,
      onError: reject,
      onSubscribe: () => console.log(`subscribed to ${url}`),
    });
  })
}

export default createRSocket;