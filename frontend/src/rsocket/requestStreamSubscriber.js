const createSubscription = (flowable,requestSize, {onNext, onError, onLimitReached}) => {
  let subscription = null
  let subscriptionCounter = 0

  flowable.subscribe({
    onNext: (response) => {
      console.log('ON NEXT ', response)
      subscriptionCounter++
      if (subscriptionCounter >= requestSize) {
        subscriptionCounter = 0
        onLimitReached(() => subscription.request(requestSize))
      }
      onNext(response.data)
    },
    onError: onError,
    onSubscribe: (sub) => {
      subscription = sub
      console.log('ON SUBSCRIBE ')
      sub.request(requestSize)
    }
  })

  return subscription.cancel
}

const createRequestStreamSubscriber =  (socket) => {
  return (requestData, requestSize, callbacks) => {
    const flowable = socket.requestStream(requestData)
    return createSubscription(flowable,requestSize, callbacks)
  }
}

export default createRequestStreamSubscriber
