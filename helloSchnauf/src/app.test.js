const request = require('supertest')

describe('the app', () => {
  let app

  beforeEach(() => {
    app = require('./app')
  })

  it('should be express app', () => {
    expect(app.use).toBeDefined()
  })

  it('should disable the X-powered-by HTTP header', async () => {
    const resp = await request(app).get('/schnauf/test')
    expect(resp.header['x-powered-by']).toBeUndefined()
  })

  it('should define the route for hello schnauf', async () => {
    await request(app)
      .get('/schnauf/test')
      .expect(200, 'test-schnauf')
  })
})
