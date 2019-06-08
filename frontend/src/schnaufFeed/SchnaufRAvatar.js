import React, {Component} from 'react'
import binaryRSocketClient from '../rsocket/binaryRSocketClient'
import requestStreamSubscriber from '../rsocket/requestStreamSubscriber'
import PropTypes from 'prop-types'

const WS_SOCKET_URL = `ws://schnau.fr:8080`

const createRequestStreamBinary = () => {
  return new Promise((resolve, reject) => {
    binaryRSocketClient(WS_SOCKET_URL, {
      onComplete: resolve,
      onError: reject,
    } )
  }).then(requestStreamSubscriber)
}

class SchnaufRAvatar extends  Component {

  componentDidMount() {
    const {avatarId }=  this.props

    const requestData = {data: {id : avatarId}, metadata: {operation: 'findAvatar'}}
    createRequestStreamBinary().then((subscribe) => {
      subscribe(requestData, 100, {
        onNext: (data) => console.log('DATA', DATA),
        onError: console.error,
        onLimitReached: () => console.log('Limit reached')
      })
    })

  }

  render() {
    return <div>Foo</div>
  }
}

SchnaufRAvatar.propTypes = {
  avatarId: PropTypes.string.isRequired
}

export default SchnaufRAvatar