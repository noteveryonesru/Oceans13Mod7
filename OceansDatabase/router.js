const controller = require( './controller' )
module.exports = (app) => {
	app.get( '/' , controller.homepage)
	app.post( '/save' , controller.save)	//saving the data from JSON file
	app.get( '/findAll' , controller.findAll)  //accessing the values
}