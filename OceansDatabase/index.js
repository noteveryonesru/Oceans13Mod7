const express = require('express');
const bodyParser = require('body-parser');
var cors = require('cors')

const app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:false}));
app.use(cors())
const router = require('./router')
router(app)

app.listen(3001)