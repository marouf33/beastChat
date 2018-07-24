var app = require('express')();

var http = require('http').Server(app);

var io = require('socket.io')(http);
var admin = require("firebase-admin");

var userFriendRequests = (io) =>{
  io.on('connection', (socket)=>{
    console.log(`Client ${socket.id} is connected to friend services!`);
    detectDisconnection(socket,io);
    sendOrDeleteFriendRequest(socket,io);
  });
};

function sendOrDeleteFriendRequest(socket,io){
    socket.on('friendRequest',(data)=>{
        var friendEmail = data.email;
        var userEmail = data.userEmail;
        var requestCode = data.requestCode;

        var db = admin.database();
        var friendRef = db.ref('friendRequestsReceived').child(encodeEmail(friendEmail))
        .child(encodeEmail(userEmail));

        if(requestCode == 0){

            var db= admin.database();
            var ref = db.ref('users');
            var userRef = ref.child(encodeEmail(data.userEmail));

            userRef.once('value', (snapshot)=>{
                friendRef.set({
                    email:snapshot.val().email,
                    userName:snapshot.val().userName,
                    userPicture:snapshot.val().userPicture,
                    dateJoined:snapshot.val().dateJoined,
                    hasLoggedIn:snapshot.val().hasLoggedIn
                });
            });


        }else{
            friendRef.remove();
        }
    });
}


function detectDisconnection(socket,io){
    socket.on('disconnect',()=>{
      console.log('A client has disconnected from friend services.');
    });
  }
  
  function escapeRegExp(str) {
    return str.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
  }
  
  function replaceAll(str, find, replace) {
    return str.replace(new RegExp(escapeRegExp(find), 'g'), replace);
  }
  
  function encodeEmail(email){
    return replaceAll(email,'.',',');
  }
  module.exports = {
    userFriendRequests
  }