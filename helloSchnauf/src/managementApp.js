const express = require('express')
const expressHealthcheck = require('express-healthcheck')

const managementApp = express()
managementApp.disable('x-powered-by')
managementApp.get('/healthz', expressHealthcheck())

module.exports = managementApp
