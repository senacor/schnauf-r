import {JsonSerializers, RSocketClient} from 'rsocket-core';
import RSocketWebSocketClient from 'rsocket-websocket-client';

const createRSocket = (url, {onComplete, onError}) => {
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

  rSocketClient.connect().subscribe({
    onComplete: onComplete,
    onError: onError,
    onSubscribe: () => console.log(`subscribed to ${url}`),
  });

}

export default createRSocket;