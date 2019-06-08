import React from 'react'
import {shallow} from 'enzyme'
import SchnaufFeedEntry from './SchnaufFeedEntry'

describe('Schnauf', () => {

  const app = shallow(<SchnaufFeedEntry reason={'react'}/>)

  it('renders the title', () => {
    expect(app.find('h1').exists()).toBe(true)
  })
})