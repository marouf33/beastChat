var app = require('express')();

var http = require('http').Server(app);

var io = require('socket.io')(http);
var admin = require("firebase-admin");

var userAccountRequests = (io) =>{
  io.on('connection', (socket)=>{
    console.log(`Client ${socket.id} is connected!`);
    detectDisconnection(socket,io);
    registerUser(socket,io);
    logUserIn(socket,io);
  });
};


function logUserIn(socket,io){
  socket.on('userInfo',(data)=>{
    admin.auth().getUserByEmail(data.email)
    .then((userRecord)=>{
      var db= admin.database();
      var ref = db.ref('users');
      var userRef = ref.child(encodeEmail(data.email));
      userRef.once('value',(snapshot)=>{
            var additionalClaims = {
              email:data.email
            };
            admin.auth().createCustomToken(userRecord.uid,additionalClaims)
            .then((customToken) =>{

              Object.keys(io.sockets.sockets).forEach((id)=>{
                if(id = socket.id){
                  var token ;
                  var userName;
                  var userPic;
                  if(!snapshot.val() && data.name){
                    userName = data.name;
                    userPic = data.profilePicURL;
                    var date = {
                      data:admin.database.ServerValue.TIMESTAMP
                    };
                    userRef.set({
                      email:data.email,
                      userName:data.name,
                      userPicture:userPic,
                      dateJoined:date,
                      hasLoggedIn:false
                    });
                  }else{
                    userName = snapshot.val().userName
                    userPic = snapshot.val().userPicture;
                  }

                  token = {
                    authToken:customToken,
                    email:data.email,
                    photo:userPic,
                    displayName:userName
                  };
                  io.to(id).emit('token',{token});
                }
              });

            }).catch((error)=>{
              Object.keys(io.sockets.sockets).forEach((id)=>{
                console.log(error.message);
                if(id = socket.id){
                  var token = {
                    authToken:error.message,
                    email:'error',
                    photo:'error',
                    displayName:'error'
                  }
                  io.to(id).emit('token',{token});
                }
              });
            });
        });
      
    });
  });
}


function registerUser(socket,io){
  socket.on('userData',(data)=>{
    admin.auth().createUser({
      email:data.email,
      displayName:data.userName,
      password:data.password
    })
    .then((userRecord)=>{
        console.log('User was registered successfully');

        var db= admin.database();
        var ref = db.ref('users');
        var userRef = ref.child(encodeEmail(data.email));
        var date = {
          data:admin.database.ServerValue.TIMESTAMP
        };

        userRef.set({
          email:data.email,
          userName:data.userName,
          userPicture:'https://dl.dropboxusercontent.com/s/sdmw0p5avpvh41g/635319915.jpg?dl=0',
          dateJoined:date,
          hasLoggedIn:false
        });

        Object.keys(io.sockets.sockets).forEach((id)=>{
          if(id = socket.id){
            var message = {
              text:'Success'
            }
            io.to(id).emit('message',{message});
          }
        });


    }).catch((error)=>{
      Object.keys(io.sockets.sockets).forEach((id)=>{
        console.log(error.message);
        if(id = socket.id){
          var message = {
            text:error.message
          }
          io.to(id).emit('message',{message});
        }
      });
    });
  });
}

function detectDisconnection(socket,io){
  socket.on('disconnect',()=>{
    console.log('A client has disconnected.');
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
  userAccountRequests
}
