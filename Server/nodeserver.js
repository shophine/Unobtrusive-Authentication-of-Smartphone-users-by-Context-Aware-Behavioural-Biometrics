const express = require('express')
const app = express()
var sys = require('sys')
var exec = require('child_process').exec;
const port = 3030;
var morgan = require('morgan');
const fs = require('fs');
const bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(morgan('dev'))
//support parsing of application/x-www-form-urlencoded post data
app.use(bodyParser.json({limit: '50mb'}));
app.use(bodyParser.urlencoded({limit: '50mb', extended: true}));
app.use(bodyParser.urlencoded({ extended: true }));
app.get('/',function(req,res){
	res.send("Server Running");
});

app.post('/upload',function(req,res){
	console.log("Logging the request");
	if(req.body.logs){
		fs.writeFileSync("logs.csv",req.body.logs); 
		res.status(200).json({"statusCode":"200"});
	}
	});

app.post('/post',function (req, res) {
	var obj = req.body;
	console.log(obj);
	exec("echo "+obj.userID+","+obj.segmentNo+">to_be_predicted.txt",function (error,stdout,stderr) {
		console.log(error,stdout,stderr);
		
	});
	res.send({statusCode:200, Message:"Script Invoked"},200)
});

app.get('/getoutput',function(req,res){
	fs.readFile("predicted_label.txt", "utf8", function(err, data) {
		console.log("Data is "+data);
		var output=data.split(",");
		var response={
			"output":output[0],
			"constant":output[1]			
		};	
		res.status(200).json(response);
	});
});

app.listen(port, () => console.log(`Example app listening on port ${port}!`))
