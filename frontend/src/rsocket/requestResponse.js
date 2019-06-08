
const requestResponse = (socket) => (requestData) => {
  const single = socket.requestResponse(requestData);

  return new Promise((resolve, reject) => {
    single.subscribe({
      onComplete: resolve,
      onError: reject
    })
  })
}

export default requestResponse
