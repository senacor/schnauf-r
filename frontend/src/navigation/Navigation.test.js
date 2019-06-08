import React from 'react'
import {shallow} from 'enzyme'

import { Nav} from 'react-bootstrap'
import Navigation from './Navigation'

describe('Navigation', () => {

  const renderNavigation = ({loggedIn}) => shallow(<Navigation loggedIn={loggedIn}/>)

  it('renders Feed NavLink if loggedIn is true', () => {
    const navigation = renderNavigation({loggedIn: true})

    const navLink = navigation.find(Nav.Link)
    expect(navLink.exists()).toBe(true)
  })

  it('does not render Feed NavLink if loggedIn is false', () => {
    const navigation = renderNavigation({loggedIn: false})

    const navLink = navigation.find(Nav.Link)
    expect(navLink.exists()).toBe(false)
  })
})