import createRSocket from './rSocket';
import requestResponse from './requestResponse';
import requestStreamSubscriber from './requestStreamSubscriber';
const createRSocketClient = async (url) => {
  const socket = await createRSocket(url);

  return {
    requestResponse : requestResponse(socket),
    subscribeRequestStream: requestStreamSubscriber(socket),
  };
}

export default createRSocketClient;