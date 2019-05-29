const express = require('express')
const helmet = require('helmet')

const app = express()

app.use(helmet())
app.get('/schnauf/:subject', (req, res) => res.send(`${req.params.subject}-schnauf`))
module.exports = app
