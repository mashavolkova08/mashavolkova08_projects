//SERVER

// include the express module
var express = require("express");

// create an express application
var app = express();
const url = require('url');

// helps in extracting the body portion of an incoming request stream
var bodyparser = require('body-parser');

// fs module - provides an API for interacting with the file system
var fs = require("fs");

// helps in managing user sessions
var session = require('express-session');

// include the mysql module
var mysql = require("mysql");

// helpful for reading, compiling, rendering pug templates
const pug = require("pug");  

app.set('view engine', 'pug')

// Bcrypt library for comparing password hashes
const bcrypt = require('bcrypt');
const e = require("express");


const connection = mysql.createConnection({
  host: "cse-mysql-classes-01.cse.umn.edu",
  user: "C4131DF23U100",               
  password: "10050",                  
  database: "C4131DF23U100",           
  port: 3306
});

connection.connect(function(err) {
  if (err) {
    throw err;
  };
  console.log("Connected to MYSQL database!");
});



// apply the body-parser middleware to all incoming requests
app.use(bodyparser());

// use express-session
app.use(session({
  secret: "csci4131secretkey",
  saveUninitialized: true,
  resave: false
}
));

// server listens on port 9007 for incoming connections
app.listen(9007, () => console.log('Listening on port 9007!'));


// function to return the welcome page
app.get('/',function(req, res) {
  //res.sendFile(__dirname + '/client/welcome.html');
  res.render('welcome');
});

// function to return the add event page
app.get('/login',function(req, res) {
  if (req.session.user){
    res.redirect('/schedule');
  }
  else{
    //res.sendFile(__dirname + '/client/login.html');
    res.render('login');
  }

 });


// Validate login credentials 
app.post('/loginValidation',function(req, res) {
  console.log("schedule!")
  console.log("user: " + req.body.user)
  console.log("password 1: " + req.body.password)
  var username = req.body.user
  var password = req.body.password

  //var filter = [username, password];
  var filter = username;

  connection.query('SELECT * FROM tbl_accounts WHERE acc_login = ?', filter, function(err,rows,fields) {
    if (err) throw err;
    if (rows.length >= 1){
        console.log("Success! Result: " + rows[0].acc_login + " " + rows[0].acc_password);

        if (bcrypt.compareSync(password,rows[0].acc_password ) == true){
          req.session.user = username;
          return res.json({status:"success"})
        }
        else{
          return res.json({status:"fail"})
        }
    }
    else {
      console.log("No entries in tbl_accounts table");
      return res.json({status:"fail"})
    }
  });
});

app.get('/schedule',function(req, res) {
  if (!req.session.user){
    res.redirect(302, '/login');
  }
  else{
    //res.sendFile(__dirname + '/client/schedule.html');
    res.render('schedule');
  }

});



app.get('/getSchedule', function(req,res){
  console.log("Get schedule test")
  console.log("Day: ", req.query.day);

  var filter = req.query.day.toUpperCase();

  connection.query('SELECT * FROM tbl_events WHERE event_day = ? ORDER BY event_start', filter, function(err,rows,fields) {
    if (err) throw err;
    if (rows.length >= 1){
        //console.log("Success! Result: " + JSON.stringify(rows));

        return res.json(rows);
    }
    else {
      console.log("No entries in tbl_events table");
      
    }
  });

});


// POST event
app.post('/postEventEntry',function(req, res) {

  console.log("POST event test")
  console.log(req.body)
  console.log(req.body["name"])

  var rowToBeInserted = {
    event_day: req.body["day"], 
    event_event: req.body["name"], 
    event_start: req.body["start"],
    event_end : req.body["end"],
    event_location : req.body["location"],
    event_phone : req.body["phone"],
    event_info : req.body["info"],
    event_url : req.body["url"]

  };

  connection.query('INSERT tbl_events SET ?', rowToBeInserted, function(err, result) {  //Parameterized insert
    if(err) throw err;
    console.log("Values inserted");
    res.redirect(302, '/schedule');
  });

});




// function to return the add event page
app.get('/addEvent',function(req, res) {
  if (!req.session.user){
    res.redirect(302, '/login');
  }
  else{
    //res.sendFile(__dirname + '/client/addEvent.html');
    res.render('addEvent');
  }
});

// function to return the edit event page
app.get('/editEvent',function(req, res) {
  if (!req.session.user){
    res.redirect(302, '/login');
  }
  else{

    console.log("Edit event: " + req.query.id)

    connection.query('SELECT * FROM tbl_events WHERE event_id = ?', [req.query.id], function(err,rows,fields) {
      if (err) throw err;
      if (rows.length == 1){
        eventToEdit = {
          id: rows[0].event_id,
          day: rows[0].event_day,
          name: rows[0].event_event,
          start: rows[0].event_start,
          end: rows[0].event_end,
          phone: rows[0].event_phone,
          location: rows[0].event_location,
          info: rows[0].event_info, 
          url: rows[0].event_url
          }
          res.render("updateEvent", {record: eventToEdit} );

      }
      else {
        console.log("No entries in tbl_events table");
        res.sendStatus(404);
      }
    });
  }
});

app.post('/updateEventEntry',function(req, res) {
  if (!req.session.user){
    res.redirect(302, '/login');
  }
  else{
    console.log("update event test")
    console.log("id param: " + req.query.id)

    var rowToBeUpdated = {
      event_day: req.body["day"], 
      event_event: req.body["name"], 
      event_start: req.body["start"],
      event_end : req.body["end"],
      event_location : req.body["location"],
      event_phone : req.body["phone"],
      event_info : req.body["info"],
      event_url : req.body["url"]
  
    };

    filter= [rowToBeUpdated, req.query.id]

    connection.query('UPDATE tbl_events SET ? WHERE event_id =?', filter, function(err, result) {  //Parameterized update
      if(err) throw err;
      console.log("Row" + req.query.id + "updated!");
      res.redirect(302, '/schedule');
    });

  }

});


// function to delete event
app.get('/deleteEvent',function(req, res) {

  if (!req.session.user){
    res.redirect(302, '/login');
  }
  else{

    console.log("Delete event!")
    console.log(req.query.id)
    var eventId =  req.query.id; 
  
    connection.query('SELECT * FROM tbl_events WHERE event_id = ?', eventId, function(err,rows,fields) {
      if (err) throw err;
  
      if (rows.length == 1){
        connection.query('DELETE FROM tbl_events WHERE event_id = ?',[eventId], function(err,result){ 
        if (err) throw err;
        console.log("sucessfully deleted: " + req.query.id)
        res.sendStatus(200);
  
      }); 
      }
      else {
        console.log("No entries in tbl_events table");
        res.sendStatus(404);
      }
  
    })

  }
});

app.get('/logout',function(req, res) {

  if (req.session.user)
    req.session.destroy((err) => {
    if (err) 
      throw err;
    else
      console.log ("Successfully Destroyed Session!");
      res.redirect(302, '/login');
    });
  else
    console.log("Not logged in: req.session.value not set!");
    //res.redirect(302, '/login');

});


// middle ware to serve static files
app.use('/client', express.static(__dirname + '/client'));


// function to return the 404 message and error to client
app.get('*', function(req, res) {
  // add details
  res.sendStatus(404);
});
