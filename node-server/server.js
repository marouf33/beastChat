var app = require('express')();

var http = require('http').Server(app);

var io = require('socket.io')(http);

io.on('connection', (socket)=>{
  console.log(`Client ${socket.id} is connected!`);

  socket.on('disconnect',()=>{
    console.log('A client has disconnected.');
  })
});

http.listen(4000,()=>{
  console.log('Server is listening on port 4000');
});
