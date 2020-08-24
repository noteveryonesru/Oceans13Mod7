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
            singleboat: []
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
			.then(data => this.setState({boat:data.boat}))
            .catch(function(error) {
                console.log(error);
              }); 

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
    	const PhilippinesLonLat = fromLonLat([121.7740, 12.8797]);//Zoom:5
        //const LBLonLat = fromLonLat([121.238603, 14.164747]); // UP Los Banos
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

        var vectorLayers = [];
        vectorLayers.push(vectorLayer);

        //Update the state variables
        this.setState({ 
            map: map,
            vectorLayers: vectorLayers
        });
    	
    }
     componentWillUnmount(){
        window.removeEventListener('resize', this.updateDimensions)
    }

    plot(boats,macAddress) {
        //removing the previous Layer inputted in the map
        for(var b =0;b<this.state.vectorLayers.length;b++){
            this.state.map.removeLayer(this.state.vectorLayers[b])
        }
         
        var vectorLayers = []      //variable that will hold all the vector Layers
        //Getting all the location visited by the certain boat
        for(var a =0; a< boats.length;a++){
            if(boats[a].MacAddress == macAddress){

                const features = [];
                var boat=boats[a];

                for(var i =0 ; i<boat.ArrayOfSessions.length ; i++){
                    var Records = boat.ArrayOfSessions[i].ArrayOfRecords;
                    for(var j = 0; j < Records.length ; j++){
                       /* console.log(Records[j].Latitude)
                        console.log(Records[j].Longitude)*/
                        
                        //Putting the coordinates vidited by the boat in an array
                        features.push(new Feature({
                          geometry: new Point(fromLonLat([
                            Records[j].Longitude, Records[j].Latitude
                          ]))
                        }));
                    }
                }
            
            const vectorSource = new SourceVector({     //put all the coordinates
                features
             });

            //create the vector Layer for the new points
            const vectorLayer = new LayerVector({
                source: vectorSource,
                style: new Style({
                  image: new Circle({
                    radius: 7,
                    fill: new Fill({color: '#'+(Math.random() * 0xFFFFFF << 0).toString(16).padStart(6, '0')})
                  })
                })
              });
          
          //add the vector Layer to the map and put in array to remove later on.
          this.state.map.addLayer(vectorLayer) 
          vectorLayers.push(vectorLayer);
            }
        }
            //update the vector layer to have reference in deleting later on.
            this.setState({ 
              vectorLayers: vectorLayers
            });
    }

    onlyUnique(value, index, self) { 
        return self.indexOf(value) === index;
    }

     render(){
        const style = {
            width: '100%',
            height:this.state.height,
            backgroundColor: '#cccccc',
        }
        
        const { boat } = this.state;
        
        //get all the unique MacAddress before putting in the display
        var mac = []
        for(var i =0; i< boat.length;i++){
          mac.push(boat[i].MacAddress)
         }
        var uniqueMacAddress = mac.filter(this.onlyUnique)
     
        return (
        	//Rendering the Map and Boat
         		<div>
                    
                    <h2 className = "header"> Map</h2>
                
                    <div id='map' style={style}>
                    </div>

                    <br/>
                    <h2 className = "header"> Boats</h2>
                    <ul>
                      {uniqueMacAddress.map(macAdd =>
                          <li key ={macAdd} onClick={() => this.plot(boat,macAdd)}>
                                {macAdd}
                          </li>
                        )}
                      </ul>
                
                </div>
        )
    }
}
export default Home;