import React from 'react';
import 'ol/ol.css';
import {Map, View} from 'ol';
import TileLayer from 'ol/layer/Tile';
import OSM from 'ol/source/OSM';

class Home extends React.Component{
	constructor(props) {
        super(props)
        this.updateDimensions = this.updateDimensions.bind(this)
    }
    updateDimensions(){
        const h = window.innerWidth >= 992 ? window.innerHeight : 400
        this.setState({height: h})
    }
    componentWillMount(){
        window.addEventListener('resize', this.updateDimensions)
        this.updateDimensions()
    }
    componentDidMount(){
    	//Festching the macaddresses
    	fetch('http://localhost:3001/findAll')
			.then(response => response.json())
			.then(body =>{
			this.setState({boat:body})
			});

    	const map = new Map({
			  target: 'map',
			  layers: [
			    new TileLayer({
			      source: new OSM()
			    })
			  ],
			  view: new View({
			    center: [0, 0],
			    zoom: 0
			  })
			});
    	
    }
     componentWillUnmount(){
        window.removeEventListener('resize', this.updateDimensions)
    }
     render(){
        const style = {
            width: '100%',
            height:this.state.height,
            backgroundColor: '#cccccc',
        }
        return (
        	//Rendering the map and Mac Address
         		<div>
                    <div id='map' style={style}>
                    </div>
					<h2> Boats</h2>
						MacAddress: {this.state.boat}
                </div>
            
        )
    }
}
export default Home;