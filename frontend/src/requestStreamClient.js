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

const createRequestStreamClient = (requestSize) => (socket) => {

  const subscribe = (callbacks) => {
    const flowable = socket.requestStream({ data: {}});
    return createSubscription(flowable, callbacks, requestSize);
  };

  return subscribe
}

export default createRequestStreamClient