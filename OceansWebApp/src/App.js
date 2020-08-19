import React from 'react';
import {BrowserRouter, Route} from 'react-router-dom'

import Home from './pages/Home'

function App() {
  return (
    <div className ="content">
    <BrowserRouter>
    <div>
    <Route exact={true} path="/" component={Home}/>
    </div> 
    </BrowserRouter>
    </div>
  );
}

export default App;
