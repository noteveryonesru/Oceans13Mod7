import React from 'react';
import 'ol/ol.css';
import {Map, View} from 'ol';
import TileLayer from 'ol/layer/Tile';
import OSM from 'ol/source/OSM';
import {fromLonLat} from 'ol/proj';
import SourceVector from 'ol/source/Vector';
import LayerVector from 'ol/layer/Vector'
import Feature from 'ol/Feature';
import Point from 'ol/geom/Point';
import Style from 'ol/style/Style';
import Circle from 'ol/style/Circle';
import Fill from 'ol/style/Fill';

class Home extends React.Component{
    constructor(props) {
        super(props)
        this.updateDimensions = this.updateDimensions.bind(this);
        this.state = {
            boat: [],
        }
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
    	//Fetching the MacAddresses
    	fetch('http://localhost:3001/findAll')
			.then(response => response.json())
			.then(data => this.setState({boat:data.boat}));

        //Creating the Source Vector and Layer Vector for Plotting
        const features = [];
        const vectorsource = new SourceVector({
            features
        })
        const vectorLayer = new LayerVector({
            source: vectorsource,
            style: new Style({
              image: new Circle({
                radius: 2,
                fill: new Fill({color: 'red'})
              })
            })
          });

        //Get the center at the Philippines
    	const PhilippinesLonLat = fromLonLat([121.7740, 12.8797]);
        const map = new Map({
          layers: [
            new TileLayer({
              source: new OSM()
            }),
            vectorLayer
          ],
          target: 'map',
          view: new View({
            center: PhilippinesLonLat,
            zoom: 5
          })
        });

        //Update the state variables
        this.setState({ 
            map: map,
            vectorLayer: vectorLayer
        });
    	
    }
     componentWillUnmount(){
        window.removeEventListener('resize', this.updateDimensions)
    }

    plot(boat) {
        //Getting all the location visited by the certain boat
        const features = [];
        for(var i =0 ; i<boat.ArrayOfSessions.length ; i++){
            var Records = boat.ArrayOfSessions[i].ArrayOfRecords;
            for(var j = 0; j < Records.length ; j++){
                console.log(Records[j].Latitude)
                console.log(Records[j].Longitude)
                
                //Putting the coordinates vidited by the boat in an array
                features.push(new Feature({
                  geometry: new Point(fromLonLat([
                    Records[j].Longitude, Records[j].Latitude
                  ]))
                }));
            }
        }

        //removing the previous Layer inputted in the map
        this.state.map.removeLayer(this.state.vectorLayer)
          const vectorSource = new SourceVector({
            features
          });

        //create the vector Layer for the new points
        const vectorLayer = new LayerVector({
            source: vectorSource,
            style: new Style({
              image: new Circle({
                radius: 2,
                fill: new Fill({color: 'red'})
              })
            })
          });
       
        //put the vectorLayer in the map and update the value
        this.state.map.addLayer(vectorLayer)
        this.setState({ 
              vectorLayer: vectorLayer
            });
    }

     render(){
        const style = {
            width: '100%',
            height:this.state.height,
            backgroundColor: '#cccccc',
        }
        const { boat } = this.state;
        return (
        	//Rendering the Map and Boat
         		<div>
                    
                    <h2 className = "header"> Map</h2>
                
                    <div id='map' style={style}>
                    </div>

                    <br/>
                    <h2 className = "header"> Boats</h2>
                    <ul>
                      {boat.map(boat =>
                          <li key={boat._id} onClick={() => this.plot(boat)}>
                                {boat.MacAddress}
                          </li>
                        )}
                      </ul>
                
                </div>
        )
    }
}
export default Home;