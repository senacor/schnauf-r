import createRSocket from './rSocket';
import requestResponse from './requestReponse';
import requestStreamSubscriber from './requestStreamSubscriber';
const createRSocketClient = async (url) => {
  const socket = await createRSocket(url);

  return {
    requestResponse : requestResponse(socket),
    subscribeRequestStream: requestStreamSubscriber(socket),
  };
}

export default createRSocketClient;