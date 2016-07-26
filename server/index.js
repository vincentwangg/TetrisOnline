var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var jsonfile = require('jsonfile');
var rooms = [];

for (var i = 0; i < 1; i++) {
    rooms.push(false);
}

// Server start
server.listen(8080, function () {
    console.log("Server is now running...");
});

// Individual player connection
io.on('connection', function (socket) {
    var roomID = -1;
    var players = [];

    // Indicates Player connected to server
    console.log("Player Connected!");

    // For now, wait for signal to join room, then join room 0 todo join requested room
    socket.on("createRoom", function () {
        // Create room data
        var roomData = {password: "", players: [socket.id], ready: 0};
        players.push(socket.id);

        var roomNum;
        do {
            console.log("damn");
            // If rooms all get occupied, generate 100 more rooms
            if (areAllRoomsOccupied()) {
                for (var i = 0; i < 10; i++) {
                    rooms.push(false);
                }
            }

            // Create random room number
            roomNum = getRandomInt(0, rooms.length - 1);
        } while (rooms[roomNum]);

        writeFile(roomNum, roomData);
        rooms[roomNum] = true;
        roomID = roomNum;
        console.log(rooms);
    });

    socket.on("joinRoom", function (data, ack) {
        // data format:
        //  { roomID : 3 }

        // Make sure room is open
        if (rooms[data.roomID]) {
            // Add socket id to players and push
            jsonfile.readFile(getRoomFileName(data.roomID), function (err, roomData) {
                // Check to see if room is full or not
                if (roomData.players.length < 2) {
                    // Set socket variables
                    roomID = data.roomID;

                    // Let client know they joined the room successfully
                    ack(true);

                    // Broadcast to everyone that a player joined the room
                    roomData.players.forEach(function (player) {
                        socket.broadcast.to(player).emit("playerJoinedRoom", {socketID: socket.id});
                    });

                    // Add player to room json and write to file
                    roomData.players.push(socket.id);

                    players = roomData.players;

                    writeFile(data.roomID, roomData);
                } else {
                    // If not, emit room full event
                    socket.emit("roomFull");
                }
            });
        } else {
            // If not, emit roomNull event
            socket.emit("roomNull");
        }
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
                    socket.emit("start");
                }, 3000);
            }
        });
    });

    socket.on("playerJoinedRoom", function (data) {
        players.push(data.socketID);
    });

    // On block moved
    socket.on("moveBlock", function (data) {
        players.forEach(function (socketID) {
            // Emit block position to other player
            socket.broadcast.to(socketID).emit("blockMoved", data);
        });
    });

    // On new block
    socket.on("newBlock", function (data) {
        players.forEach(function (socketID) {
            // Emit block position to other player
            socket.broadcast.to(socketID).emit("newBlock", data);
        });
    });

    // On block landed
    socket.on("blockLanded", function (data) {
        players.forEach(function (socketID) {
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

function getRoomFileName(roomID) {
    return "./server/rooms/" + roomID.toString() + ".json";
}

function writeFile(roomID, data) {
    jsonfile.writeFile(getRoomFileName(roomID), data, function (err) {
    });
}

function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function areAllRoomsOccupied() {
    for (var i = 0; i < rooms.length; i++) {
        if (!rooms[i]) {
            return false;
        }
    }
    return true;
}