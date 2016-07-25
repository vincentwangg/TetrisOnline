var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var sockets = [];

// Server start
server.listen(8080, function() {
    console.log("Server is now running...");
});

// Individual player connection
io.on('connection', function(socket) {
    // Indicates Player connected to server
    console.log("Player Connected!");

    // Broadcast to every in room that player joined TODO make rooms, not broadcast
    socket.broadcast.emit("playerJoinedRoom");

    // Add player to room
    sockets.push(socket.id);

    // Start game after 3 seconds if 2 players in room
    if (sockets.length == 2) {
        setTimeout(function() {
            sockets.forEach(function(socketID) {
                // Emit block position to other player
                socket.broadcast.to(socketID).emit("start");
            });
            socket.emit("start")
        }, 3000);
    }

    // On block moved
    socket.on("moveBlock", function(data) {
        console.log(data);
        sockets.forEach(function(socketID) {
            // Emit block position to other player
            socket.broadcast.to(socketID).emit("blockMoved", data);
        });
    });

    // On block moved
    socket.on("newBlock", function(data) {
        console.log(data);
        sockets.forEach(function(socketID) {
            // Emit block position to other player
            socket.broadcast.to(socketID).emit("newBlock", data);
        });
    });

    // On block moved
    socket.on("blockLanded", function(data) {
        console.log(data);
        sockets.forEach(function(socketID) {
            // Emit block position to other player
            socket.broadcast.to(socketID).emit("blockLanded", data);
        });
    });

    // Player disconnect event
    socket.on('disconnect', function() {
        console.log("Player Disconnected");
        sockets.splice(sockets.indexOf(socket.id), 1);
    });
});