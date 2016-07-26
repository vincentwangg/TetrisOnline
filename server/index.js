var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var jsonfile = require('jsonfile');
var rooms = [0, 0, 0, 0, 0];

// Server start
server.listen(8080, function () {
    console.log("Server is now running...");
});

// Individual player connection
io.on('connection', function (socket) {
    var roomID = -1;

    // Indicates Player connected to server
    console.log("Player Connected!");

    // For now, wait for signal to join room, then join room 0 todo join requested room
    socket.on("createRoom", function () {
        // Create room data
        var roomData = {password: "", players: [socket.id], ready: 0};
        writeFile(0, roomData);
        rooms[0] = 1;
        roomID = 0;
        console.log("createRoom")
    });

    socket.on("joinRoom", function (data, ack) {
        // data format:
        //  { roomID : 3 }

        // Make sure room is open
        ack(true);
        //if (rooms[data.roomID] == 1) {
        // Add socket id to players and push
        jsonfile.readFile(getRoomFileName(data.roomID), function (err, roomData) {
            // Check to see if room is full or not
            if (roomData.players.length < 2) {
                roomData.players.push(socket.id);
                roomID = data.roomID;
                ack(true);
                writeFile(data.roomID, roomData);
            } else {
                // If not, emit room full event
                socket.emit("roomFull");
            }
        });
        //} else {
        //    // If not, emit failedJoinRoom event
        //    socket.emit("failedJoinRoom");
        //}
    });

    socket.on("ready", function () {
        jsonfile.readFile(getRoomFileName(roomID), function (err, roomData) {
            roomData.ready++;
            writeFile(roomID, roomData);

            // Start game if both players have successfully connected
            if (roomData.ready == 2) {
                setTimeout(function () {
                    roomData.players.forEach(function (socketID) {
                        // Emit block position to other player
                        socket.broadcast.to(socketID).emit("start");
                    });
                    socket.emit("start")
                }, 3000);
            }
        });
    });

    // Broadcast to every in room that player joined TODO make rooms, not broadcast
    socket.broadcast.emit("playerJoinedRoom");

    // On block moved
    socket.on("moveBlock", function (data) {
        sockets.forEach(function (socketID) {
            // Emit block position to other player
            socket.broadcast.to(socketID).emit("blockMoved", data);
        });
    });

    // On block moved
    socket.on("newBlock", function (data) {
        sockets.forEach(function (socketID) {
            // Emit block position to other player
            socket.broadcast.to(socketID).emit("newBlock", data);
        });
    });

    // On block moved
    socket.on("blockLanded", function (data) {
        sockets.forEach(function (socketID) {
            // Emit block position to other player
            socket.broadcast.to(socketID).emit("blockLanded", data);
        });
    });

    // Player disconnect event
    socket.on('disconnect', function () {
        console.log("Player Disconnected");
        // todo figure out how to remove the player from the room
    });
});

function createRoom(roomID) {
    // Generate random int

}

function getRoomFileName(roomID) {
    return "./server/rooms/" + roomID.toString() + ".json";
}

function writeFile(roomID, data) {
    jsonfile.writeFile(getRoomFileName(roomID), data, function (err) {
        console.log(err);
    });
}
