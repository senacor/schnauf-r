const request = require('supertest')

describe('the management app', () => {
  let managementApp

  beforeEach(() => {
    managementApp = require('./managementApp')
  })

  it('should be an express app', () => {
    expect(managementApp.use).toBeDefined()
  })

  it('should disable the X-powered-by HTTP header', async () => {
    const resp = await request(managementApp).get('/healthz')
    expect(resp.header['x-powered-by']).toBeUndefined()
  })

  it('should define the healthz route', async () => {
    await request(managementApp)
      .get('/healthz')
      .expect(200)
  })

})
