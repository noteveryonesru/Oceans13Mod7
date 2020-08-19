const mongoose = require( 'mongoose' );
mongoose.connect( 'mongodb://localhost:27017/Logs' , { useNewUrlParser :
true , useUnifiedTopology : true });

//Define the Schema of the Database
var Catch = new mongoose.Schema({
	TimeStamp: Date,
	Latitude: Number,
	Longitude: Number,
	RelatedPhoto: String
})

var Record = new mongoose.Schema({
	TimeStamp: Date,
	Latitude: Number,
	Longitude: Number,
	Bearing: Number,
	Acceleration: Number
})

var Session = new mongoose.Schema({
	StartTime:Date,
	EndTime: Date,
	ArrayOfRecords: [Record],
	ArrayOfCatches: [Catch]
})


var Boat = mongoose.model('Boat',{
	MacAddress: String,
	ArrayOfSessions: [Session]
})

exports.homepage = (req, res) => {
res.send( 'Welcome to the Homepage' )
}

//Endpoints for receiving JSON file from the phone
exports.save = (req,res) =>{
	const newBoat = new Boat({
	MacAddress : req.body.MacAddress,
	ArrayOfSessions : req.body.ArrayOfSessions
	})

	//Saving on the database
	newBoat.save((err) => {
		if (!err) {
		console.log( 'Saved!' )
		}else{
			console.log('Not Saved!')
		}
		})
	res.send(true)
}

//endpints for displaying the values from the database
exports.display = (req,res) =>{
	Boat.find({}, (err, boats) => {
		console.log(boats.length)
		for(var i =0; i< boats.length;i++){
			console.log("Boat "+i+"=======")
			console.log(boats[i].MacAddress)
			//ArrayOfRecords
			var ArrayOfSessions = boats[i].ArrayOfSessions
			console.log("Sessions")
			for(var j = 0; j<ArrayOfSessions.length;j++){
				console.log(ArrayOfSessions[j].StartTime)
				console.log(ArrayOfSessions[j].EndTime)
				//ArrayOfRecords
				var ArrayOfRecords = boats[i].ArrayOfSessions[j].ArrayOfRecords
				console.log("Records")
				for(var k = 0; k<ArrayOfRecords.length;k++){
					console.log(ArrayOfRecords[k].TimeStamp)
					console.log(ArrayOfRecords[k].Latitude)
					console.log(ArrayOfRecords[k].Longitude)
					console.log(ArrayOfRecords[k].Bearing)
					console.log(ArrayOfRecords[k].Acceleration)
				}
				//ArrayOfCatches
				var ArrayOfCatches = boats[i].ArrayOfSessions[j].ArrayOfCatches
				console.log("Catches")
				for(var l = 0; l<ArrayOfCatches.length;l++){
					console.log(ArrayOfCatches[l].TimeStamp)
					console.log(ArrayOfCatches[l].Latitude)
					console.log(ArrayOfCatches[l].Longitude)
					console.log(ArrayOfCatches[l].RelatedPhoto)

				}
			}
		}

		});
	res.send(true)
	}

//Getting the MacAddresses
exports.findAll = (req,res) =>{
	Boat.find({}, (err,boats)=>{
		var boat = []
		for(var i =0; i< boats.length;i++){
			boat.push(boats[i].MacAddress)
		}
		res.send(boat);
	})
}
