const express = require('express');
const bodyParser = require('body-parser');
var cors = require('cors')

const app = express();

<<<<<<< HEAD
//app.use(bodyParser.json());
app.use(bodyParser.json({ limit: '50mb' }));
app.use(bodyParser.urlencoded({ limit: '50mb', extended: true, parameterLimit: 50000 }));
//app.use(bodyParser.urlencoded({extended:false}));
=======
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:false}));
app.use(bodyParser.json({ limit: '50mb' }));
app.use(bodyParser.urlencoded({ limit: '50mb', extended: true, parameterLimit: 50000 }));

>>>>>>> 28e8c50ce52a586df1a2b30e0f30301f740f455b
app.use(cors())
const router = require('./router')
router(app)

app.listen(3001, () => { console.log( 'Server started at port 3001' )})