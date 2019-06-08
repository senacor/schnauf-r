import React from 'react'
import {shallow} from 'enzyme'
import { Card} from 'react-bootstrap'
import SchnaufFeedEntry from './SchnaufFeedEntry'

describe('Schnauf', () => {

  const schnauf = {
    title: 'schnaufTitle',
    author:{
      displayName: 'schnaufAuthorDisplayName'
    }
  }

  const app = shallow(<SchnaufFeedEntry schnauf={schnauf}/>)

  it('renders the schnauf title', () => {
    const cardTitle = app.find(Card.Title)
    expect(cardTitle.exists()).toBe(true)
    expect(cardTitle.text()).toBe(schnauf.title)
  })

  it('renders the schnauf author displayName', () => {
    const cardText = app.find(Card.Text)
    expect(cardText.exists()).toBe(true)
    expect(cardText.text()).toBe(schnauf.author.displayName)
  })
})