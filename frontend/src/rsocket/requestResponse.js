
const requestResponse = (socket) => (requestData) => {
  console.log(socket)
  const single = socket.requestResponse(requestData);

  return new Promise((resolve, reject) => {
    single.subscribe({
      onComplete: resolve,
      onError: reject
    })
  })
}

export default requestResponse
