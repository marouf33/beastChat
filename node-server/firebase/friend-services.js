var app = require('express')();

var http = require('http').Server(app);

var io = require('socket.io')(http);
var admin = require("firebase-admin");

var FCM = require('fcm-push');
var serverKey = 'AAAAbIUvy1o:APA91bGHEYxhcIMnyOqvlLNuDUSwjdtdp0zSbkTmib0NCTtb_gvJDE0vdHoTIK3lhTqU1u7zEA1J8tgrE8vYwqoh3MGiTyVRV_mK6lS5DnQOmmWXDQ_c4YUyzvNVKgOsfSSDOOMq6y3022jHaNtjHqsJaIgLGm51Xw';
var fcm = new FCM(serverKey);

var userFriendRequests = (io) =>{
  io.on('connection', (socket)=>{
    console.log(`Client ${socket.id} is connected to friend services!`);

    sendMessage(socket,io);
    detectDisconnection(socket,io);
    sendOrDeleteFriendRequest(socket,io);
    approveOrDeclineFriendRequest(socket,io);
  });
};

function sendMessage(socket,io){
  socket.on('details',(data)=>{
    var db = admin.database();
    var friendMessageRef = db.ref('userMessages').child(encodeEmail(data.friendEmail))
    .child(encodeEmail(data.senderEmail)).push();

    var newFriendMessagesRef = db.ref('newUserMessages').child(encodeEmail(data.friendEmail))
    .child(friendMessageRef.key);

    var message = {
      messageId: friendMessageRef.key,
      messageText: data.messageText,
      messageSenderEmail: data.senderEmail,
      messageSenderPicture: data.senderPicture
    };

    friendMessageRef.set(message);
    newFriendMessagesRef.set(message);

  });
}


function approveOrDeclineFriendRequest(socket,io){
  socket.on('friendRequestResponse',(data)=>{
    var db = admin.database();
        var friendRequest = db.ref('friendRequestsSent').child(encodeEmail(data.friendEmail))
        .child(encodeEmail(data.userEmail));
        friendRequest.remove();
        if(data.requestCode == 0){

          var db= admin.database();
          var ref = db.ref('users');
          var userRef = ref.child(encodeEmail(data.userEmail));
          var userFriendsReference = db.ref('userFriends');
          var friendFriendRef = userFriendsReference.child(encodeEmail(data.friendEmail))
          .child(encodeEmail(data.userEmail));

          userRef.once('value', (snapshot)=>{
            friendFriendRef.set({
                  email:snapshot.val().email,
                  userName:snapshot.val().userName,
                  userPicture:snapshot.val().userPicture,
                  dateJoined:snapshot.val().dateJoined,
                  hasLoggedIn:snapshot.val().hasLoggedIn
              });
          });

        }

  });
}

function sendOrDeleteFriendRequest(socket,io){
    socket.on('friendRequest',(data)=>{
        var friendEmail = data.email;
        var userEmail = data.userEmail;
        var requestCode = data.requestCode;

        var db = admin.database();
        var friendRef = db.ref('friendRequestsReceived').child(encodeEmail(friendEmail))
        .child(encodeEmail(userEmail));
        friendRef.remove();

        if(data.requestCode)

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

            var tokenReference = db.ref('userToken');
            var friendToken = tokenReference.child(encodeEmail(friendEmail));

            friendToken.once('value',(snapshot)=>{
              var message = {
                to:snapshot.val().token,
                data:{
                  title: 'Beast Chat',
                  body: `Friend Request from ${userEmail}`
                }
              };
            fcm.send(message)
              .then((response)=>{
                console.log('Message sent!');
              }).catch((err)=>{
                console.log(err);
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