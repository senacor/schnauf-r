import React from 'react'
import {shallow} from 'enzyme'
import Schnauf from './Schnauf'

describe('Schnauf', () => {
  const app = shallow(<Schnauf reason={'react'}/>)

  it('renders the title', () => {
    expect(app.find('h1').exists()).toBe(true)
  })
})