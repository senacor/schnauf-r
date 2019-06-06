const createSubscription = (flowable, {onNext, onError, onLimitReached}, requestSize) => {
  let subscription = null;
  let subscriptionCounter = 0;

  flowable.subscribe({
    onNext: (data) => {
      subscriptionCounter++;
      if (subscriptionCounter >= requestSize) {
        subscriptionCounter = 0;
        onLimitReached(() => subscription.request(requestSize));
      }
      onNext(data);
    },
    // TODO: Error Handling
    onError: onError,
    onSubscribe: (sub) => {
      subscription = sub;
      sub.request(requestSize)
    }
  });

  return  subscription.cancel;
};

const createSchnaufClient = (requestSize) => (socket) => {

  const subscribe = (callbacks) => {
    const flowable = socket.requestStream({ data: {}});
    return createSubscription(flowable, callbacks, requestSize);
  };

  return subscribe
}

export default createSchnaufClient