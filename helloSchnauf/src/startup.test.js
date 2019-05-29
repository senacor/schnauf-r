const config = require('config')

const { any } = expect
const port = config.get('Server.port')
const managementPort = config.get('Server.managementPort')

describe('starting a server', () => {
  let mockServer
  let startup

  beforeEach(() => {
    mockServer = {
      listen: jest.fn().mockImplementation((_port, cb) => {
        cb()
      }),
      close: jest.fn().mockImplementation((cb) => {
        cb()
      })
    }
    jest.mock('http')
    require('http').createServer.mockReturnValue(mockServer)

    jest.mock('./app', () => ({}))

    jest.mock('./managementApp', () => ({}))

    startup = require('./startup')
  })

  it('should start the main server', async () => {
    await startup()

    expect(mockServer.listen).toHaveBeenCalledWith(port, any(Function))
  })

  it('should start the management server', async () => {
    await startup()

    expect(mockServer.listen).toHaveBeenCalledWith(managementPort, any(Function))
  })

})
