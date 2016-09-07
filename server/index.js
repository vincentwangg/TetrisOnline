var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var port = process.env.PORT || 8080;

var maxRooms = 10;
var roomLength = 0;
var rooms = {};

// Initialize rooms
for (var i = 0; i < maxRooms; i++) {
    rooms[i] = false;
}
roomLength = maxRooms;

// Server start
server.listen(port, function () {
    console.log("Server is now running on port " + port);
});

setInterval(function () {
    console.log(rooms);
}, 1000);

// Individual player connection
io.on('connection', function (socket) {
    var roomID = -1;
    var isInRoom = false;

    /*******************************************/
    /************   CONNECTION   ***************/
    /*******************************************/

    console.log("Player Connected!");

    socket.on('disconnect', function () {
        console.log("Player Disconnected");
        // If client is in a room, remove them from the room
        if (isInRoom) {
            // Find room and remove player from the list of socket id's
            rooms[roomID].players.splice(rooms[roomID].players.indexOf(socket.id), 1);
            rooms[roomID].ready--;

            // If room is now empty, declare it in the list of rooms
            if (rooms[roomID].players.length == 0) {
                rooms[roomID] = false;
            }

            console.log("Someone left room " + roomID);

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
                for (var i = length; i < length + maxRooms; i++) {
                    rooms[i] = false;
                }
            }

            // Create random room number
            roomNum = getRandomIntExclusive(0, roomLength - 1) + 1;
        } while (rooms[roomNum]);

        // Persist player in room's json file
        rooms[roomNum] = {password: "", players: [socket.id], ready: 0};

        // Set socket instance variables
        roomID = roomNum;
        isInRoom = true;

        console.log("Someone created and joined room " + roomID);
    });

    socket.on("joinRoom", function (data, ack) {
        // data format:
        //  { roomID : 3 }

        // Make sure room is open
        if (rooms[data.roomID]) {
            // Check to see if room is full or not
            if (rooms[data.roomID].players.length < 2) {
                // Let client know they joined the room successfully
                ack(true);

                // Add player to room json and write to file
                rooms[data.roomID].players.push(socket.id);

                // Set socket instance variables
                roomID = data.roomID;
                isInRoom = true;

                console.log("Someone joined room " + roomID);
            } else {
                // If not, emit room full event
                socket.emit("roomFull");
            }
        } else {
            // If not, emit roomNull event
            socket.emit("roomNull");
        }
    });

    socket.on("ready", function () {
        // Tell client what room number they are joining
        socket.emit("roomID", {roomID: roomID + 1});

        rooms[roomID].ready++;

        // Start game if both players have successfully connected
        if (rooms[roomID].ready == 2) {
            setTimeout(function () {
                rooms[roomID].players.forEach(function (socketID) {
                    // Emit block position to other player
                    socket.broadcast.to(socketID).emit("start");
                });
                socket.emit("start");
            }, 3000);
        }
    });

    /*******************************************/
    /**********   IN-GAME EVENTS   *************/
    /*******************************************/

    socket.on("moveBlock", function (data) {
        rooms[roomID].players.forEach(function (socketID) {
            // Emit block position to other player
            socket.broadcast.to(socketID).emit("blockMoved", data);
        });
    });

    socket.on("newBlock", function (data) {
        rooms[roomID].players.forEach(function (socketID) {
            // Emit block position to other player
            socket.broadcast.to(socketID).emit("newBlock", data);
        });
    });

    socket.on("blockLanded", function (data) {
        rooms[roomID].players.forEach(function (socketID) {
            // Emit block position to other player
            socket.broadcast.to(socketID).emit("blockLanded", data);
        });
    });

    socket.on("gameOver", function (data) {
        rooms[roomID].players.forEach(function (socketID) {
            // Emit to room that you died
            socket.broadcast.to(socketID).emit("playerDied");
        });
    });
});

function getRandomIntExclusive(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function areAllRoomsOccupied() {
    for (var key in rooms) {
        if (rooms.hasOwnProperty(key)) {
            if (!rooms[i]) {
                return false;
            }
        }
    }
    return true;
}