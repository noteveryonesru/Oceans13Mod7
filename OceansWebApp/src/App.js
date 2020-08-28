import React from 'react';
import {BrowserRouter, Route} from 'react-router-dom'

import Home from './pages/Home'

import Navbar from 'react-bootstrap/Navbar';
/*import Nav from 'react-bootstrap/Nav';
import Form from 'react-bootstrap/Form'
import FormControl from 'react-bootstrap/FormControl';
import Button from 'react-bootstrap/Button';*/

function App() {
  return (
    <div className ="content">
    <BrowserRouter>
    <div id = "wholeContent">
    <>
        <Navbar bg="primary" variant="dark">
            <Navbar.Brand href="#home">BaTMon System (Banca Tracking and Monitoring System)</Navbar.Brand>
           
        	</Navbar>
        <br />
    </>

	    <div id ="bodyContent">
	    	<Route exact={true} path="/" component={Home}/>
	    </div>
	    </div> 
	    </BrowserRouter>
    </div>
  );
}

export default App;
