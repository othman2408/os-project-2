# 2/3 Game

This project is a socket-based multiplayer game where players try to select a number closest to two-thirds of the average of all players' choices in each round. The game follows a set of rules, and the last remaining player wins the game.

## Game Rules

1. Each player starts with 5 points.
2. In each round, every player selects an integer between 0 and 100 (inclusive) within a given time frame and shares it with the game server.
3. The winner(s) of a round is the player(s) whose selection is closest to two-thirds of the average of all numbers chosen by all players for that round.
4. Players lose 1 point for each losing round.
5. A player is eliminated from the game when they run out of points.
6. After each round, the following information is announced to all players: the round number, the players in the game, the numbers chosen, the remaining points for each player, the outcome (winner or loser) of that round, and which players have been eliminated, if any.
7. The game is won by the last remaining player.
8. In the last round, when there are two players left in the game, and one chooses 0, the other player will win the game (if their guess > 0).

## How to Run

1. Compile the Java files using the following command: `javac *.java`
2. Start the server by running the Server class: `java Server`
3. Start the client(s) by running the Client class in separate terminals: `java Client`
4. Follow the instructions in the client terminal to join the game.

## Classes

### Server

The Server class is responsible for managing the game server. It handles client connections, distributes tickets, manages games and players, and updates the leaderboard.

### Player

The Player class represents a player in the game. It stores the player's name, points, number selection, and other relevant information.

### Game

The Game class manages the game logic, including adding/removing players, determining round winners, deducting points, and eliminating players.

### GameManager

The GameManager class handles the execution of a game. It broadcasts messages to players, determines round outcomes, and manages the game flow.

### ClientHandler

The ClientHandler class is a thread responsible for handling client connections and managing client-server communication.

### GameHandler

The GameHandler class is a thread that handles player input for each round, ensuring that players submit their number selections within the given time frame.

### TimeoutHandler

The TimeoutHandler class is a thread that manages the timeout for player input during each round. If a player fails to submit their number selection within the time limit, their connection is closed.

### Client

The Client class represents the client application that connects to the server and allows players to interact with the game.

### Ticket

The Ticket class generates and manages unique tickets for players to join the game.

## Documentation

The code is well-documented with comments explaining the purpose and functionality of each class and method. Additionally, the project follows best practices for object-oriented programming and uses synchronization techniques to ensure thread safety.
