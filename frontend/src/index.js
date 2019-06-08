import React from 'react';
import ReactDOM from 'react-dom';
import './index.scss'
import Application from './App';

const renderApplication = () => {
  ReactDOM.render(<Application/>, document.getElementById('app'));
};

renderApplication();
