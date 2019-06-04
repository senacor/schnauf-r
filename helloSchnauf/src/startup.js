const config = require('config')
const { promisify } = require('util')
const app = require('./app')
const managementApp = require('./managementApp')
const port = config.get('server.port')
const managementPort = config.get('server.managementPort')

const startServer = async () => {
  await promisify(app.listen.bind(app))(port)
  console.info(`app server running on port ${port}`)
  return app
}

const startManagementServer = async () => {
  await promisify(managementApp.listen.bind(managementApp))(managementPort)
  console.info(`management server running on port ${managementPort}`)
  return managementApp
}

const startup = async () => {
  const managementServer = await startManagementServer()
  const server = await startServer()
  return { server, managementServer }
}

module.exports = startup
