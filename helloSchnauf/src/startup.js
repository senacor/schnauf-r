const config = require('config')
const http = require('http')
const { promisify } = require('util')
const app = require('./app')
const managementApp = require('./managementApp')
const port = config.get('Server.port')
const managementPort = config.get('Server.managementPort')

const startServer = async () => {
  const server = http.createServer(app)
  await promisify(server.listen.bind(server))(port)
  console.info(`app server running on port ${port}`)
  return server
}

const startManagementServer = async () => {
  const managementServer = http.createServer(managementApp)
  await promisify(managementServer.listen.bind(managementServer))(managementPort)
  console.info(`management server running on port ${managementPort}`)
  return managementServer
}

const startup = async () => {
  const managementServer = await startManagementServer()
  const server = await startServer()
  return { server, managementServer }
}

module.exports = startup
