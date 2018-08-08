var app = require('express')();

var http = require('http').Server(app);

var io = require('socket.io')(http);
var admin = require("firebase-admin");

var firebaseCredential = require(__dirname + '/private/serviceCredentials.json')

admin.initializeApp({
  credential: admin.credential.cert(firebaseCredential),
  databaseURL: "https://beastchat-a6336.firebaseio.com"
});

io.on('connection', (socket)=>{
  console.log(`Client ${socket.id} is connected!`);

  socket.on('disconnect',()=>{
    console.log('A client has disconnected.');
  });
});

var accountRequests = require('./firebase/account-services');
var friendRequests = require('./firebase/friend-services');

accountRequests.userAccountRequests(io);
friendRequests.userFriendRequests(io);

http.listen(process.env.PORT || 4000, process.env.IP || "0.0.0.0", function(){
 
 var addr = http.address();

 console.log("Chat server listening at", addr.address + ":" + addr.port);

});
