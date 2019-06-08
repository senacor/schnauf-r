import React from 'react'
import {shallow} from 'enzyme'

import { Card, Image, Row , Col} from 'react-bootstrap'
import SchnaufFeed from './SchnaufFeed'
import SchnaufFeedEntry from './SchnaufFeedEntry';

describe('SchnaufFeed', () => {

  const createSchnauf = (title) => ( {
    id: title,
    title: title,
    author:{
      displayName: 'schnaufAuthorDisplayName'
    }
  });

  const schnauf1 = createSchnauf('1');
  const schnauf2 = createSchnauf('2');

  const renderSchnaufFeed = ({schnaufs}) => shallow(<SchnaufFeed schnaufs={schnaufs}/>);

  it('renders message if schnaufs are empty', () => {
    const schnaufFeed = renderSchnaufFeed(({schnaufs : []}));
    expect(schnaufFeed.find('div').exists()).toBe(true);
  })

  it('renders each schnauf', () => {
    const schnaufFeed = renderSchnaufFeed(({schnaufs : [schnauf1, schnauf2]}));
    expect(schnaufFeed.find(SchnaufFeedEntry)).toHaveLength(2);
  })

})