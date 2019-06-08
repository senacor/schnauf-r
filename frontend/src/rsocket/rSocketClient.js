import requestResponse from './requestResponse'
import requestStreamSubscriber from './requestStreamSubscriber'

const createRSocketClient =  (rSocket) => {
  return {
    requestResponse : requestResponse(rSocket),
    subscribeRequestStream: requestStreamSubscriber(rSocket),
  }
}

export default createRSocketClient