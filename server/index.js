var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

// Server start
server.listen(8080, function() {
    console.log("Server is now running...");
});

// Individual player connection
io.on('connection', function(socket) {
    // Indicates Player connected to server
    console.log("Player Connected!");

    // Player disconnect event
    socket.on('disconnect', function() {
        console.log("Player Disconnected")
    });
});