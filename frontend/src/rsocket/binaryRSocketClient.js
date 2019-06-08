import {RSocketClient} from 'rsocket-core'
import RSocketWebSocketClient from 'rsocket-websocket-client'

export const Binary = {
  deserialize: (data) => {
    console.log('deserialize', data)
    return data
  },
  serialize: (data) => {
    console.log('serialize', data)
    return JSON.stringify(data)
  },
}

const createRSocket = (url, {onComplete, onError}) => {
  const rSocketClient = new RSocketClient({
    serializers: {data: Binary, metadata: Binary},
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
  })

}

export default createRSocket