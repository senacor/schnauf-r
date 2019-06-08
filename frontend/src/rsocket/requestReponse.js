
const requestReponse = (socket) => (requestData) => {
  const single = socket.requestReponse(requestData);

  return new Promise((resolve, reject) => {
    single.subscribe({
      onComplete: resolve,
      onError: reject
    })
  })
}

export default requestReponse
