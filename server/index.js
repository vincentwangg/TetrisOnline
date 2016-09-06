var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var jsonfile = require('jsonfile');
var rooms = [];
var port = process.env.PORT || 8080;
// todo variable to denote room is in game

// Initialize rooms
for (var i = 0; i < 10; i++) {
    rooms.push(false);
}

// Server start
server.listen(port, function () {
    console.log("Server is now running on port " + port);
});

// Individual player connection
io.on('connection', function (socket) {
    var roomID = -1;
    var players = [];
    var isInRoom = false;
    var isRoomHost;
    var roomTimer;

    /*******************************************/
    /************   CONNECTION   ***************/
    /*******************************************/

    console.log("Player Connected!");

    socket.on('disconnect', function () {
        console.log("Player Disconnected");
        // If client is in a room, remove them from the room
        if (isInRoom) {
            // Find room and remove player from the list of socket id's
            jsonfile.readFile(getRoomFileName(roomID), function (err, roomData) {
                roomData.players.splice(roomData.players.indexOf(socket.id), 1);
                roomData.ready = roomData.players.length;

                // If room is now empty, declare it in the list of rooms
                if (roomData.players.length == 0) {
                    rooms[roomID] = false;
                }

                console.log("Someone left room " + roomID);

                writeFile(roomID, roomData);
            });
            // Todo if player leaves deal with the number of people that are readied. do you roomData.ready--?
        }
    });

    /*******************************************/
    /************   ROOM EVENTS   **************/
    /*******************************************/

    socket.on("createRoom", function () {
        var roomNum;

        do {
            // If rooms all get occupied, generate 10 more rooms
            if (areAllRoomsOccupied()) {
                for (var i = 0; i < 10; i++) {
                    rooms.push(false);
                }
            }

            // Create random room number
            roomNum = getRandomInt(0, rooms.length - 1);
        } while (rooms[roomNum]);

        // Persist player in room's json file
        writeFile(roomNum, { password: "", players: [socket.id], ready: 0 });

        // Mark room as occupied
        rooms[roomNum] = true;

        // Set socket instance variables
        roomID = roomNum;
        players.push(socket.id);
        isInRoom = true;
        isRoomHost = true;

        console.log("Someone created and joined room " + roomID);
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
                    // Let client know they joined the room successfully
                    ack(true);

                    // Broadcast to everyone that a player joined the room
                    roomData.players.forEach(function (player) {
                        socket.broadcast.to(player).emit("playerJoinedRoom", {socketID: socket.id});
                    });

                    // Add player to room json and write to file
                    roomData.players.push(socket.id);

                    // Set socket instance variables
                    roomID = data.roomID;
                    players = roomData.players;
                    isInRoom = true;
                    isRoomHost = false;

                    console.log("Someone joined room " + roomID);

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
        // Tell client what room number they are joining
        socket.emit("roomID", { roomID : roomID + 1 });

        jsonfile.readFile(getRoomFileName(roomID), function (err, roomData) {
            roomData.ready++;
            writeFile(roomID, roomData);

            // Start game if both players have successfully connected
            if (roomData.ready == 2) {
                setTimeout(function () {
                    players.forEach(function (socketID) {
                        // Emit block position to other player
                        socket.broadcast.to(socketID).emit("start");
                    });
                    socket.emit("start");

                    // Start room timer if you are room host
                    console.log(isRoomHost);
                    // TODO GET THE ROOM HOST TO START THE TIMER
                    if (isRoomHost) {
                        var time = 120;
                        roomTimer = setInterval(function () {
                            // After 2 minutes, stop emitting and clearInterval
                            console.log(time);
                            if (time >= 0) {
                                // TODO LEFT OFF TIMECHANGED NOT WORKING
                                players.forEach(function (socketID) {
                                    socket.broadcast.to(socketID).emit("timeChanged", { time : time });
                                });
                                socket.emit("timeChanged", { time : time });
                                time--;
                            } else {
                                clearInterval(roomTimer);
                            }
                        }, 1000);
                    }
                }, 3000);
            }
        });
    });

    socket.on("playerJoinedRoom", function (data) {
        players.push(data.socketID);
    });

    /*******************************************/
    /**********   IN-GAME EVENTS   *************/
    /*******************************************/

    socket.on("moveBlock", function (data) {
        players.forEach(function (socketID) {
            // Emit block position to other player
            socket.broadcast.to(socketID).emit("blockMoved", data);
        });
    });

    socket.on("newBlock", function (data) {
        players.forEach(function (socketID) {
            // Emit block position to other player
            socket.broadcast.to(socketID).emit("newBlock", data);
        });
    });

    socket.on("blockLanded", function (data) {
        players.forEach(function (socketID) {
            // Emit block position to other player
            socket.broadcast.to(socketID).emit("blockLanded", data);
        });
    });

    socket.on("gameOver", function(data) {
        players.forEach(function (socketID) {
            // Emit to room that you died
            socket.broadcast.to(socketID).emit("playerDied");
        });
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