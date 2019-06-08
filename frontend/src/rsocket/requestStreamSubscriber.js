const createSubscription = (flowable, {onNext, onError, onLimitReached}, requestSize) => {
  let subscription = null;
  let subscriptionCounter = 0;

  flowable.subscribe({
    onNext: (response) => {
      subscriptionCounter++;
      if (subscriptionCounter >= requestSize) {
        subscriptionCounter = 0;
        onLimitReached(() => subscription.request(requestSize));
      }
      onNext(response.data);
    },
    onError: onError,
    onSubscribe: (sub) => {
      subscription = sub;
      sub.request(requestSize)
    }
  });

  return  subscription.cancel;
};

const createRequestStreamSubscriber =  (socket) => {
  return (requestData, requestSize, callbacks) => {
    const flowable = socket.requestStream(requestData);
    return createSubscription(flowable, callbacks, requestSize);
  };
}

export default createRequestStreamSubscriber
