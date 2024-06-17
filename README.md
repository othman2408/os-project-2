# OS - CMPS 405 - Project 2
# 2/3 Game

This project is a socket-based multiplayer game where players aim to select a number closest to two-thirds of the average of all players' choices in each round. The last remaining player wins the game.

## Game Rules

1. Each player starts with **5 points**.
2. In each round:
   - Players choose an integer between **0 and 100**.
   - Choices are submitted to the game server within a given time frame.
3. The winner(s) of the round is the player(s) whose choice is closest to **two-thirds of the average** of all submitted numbers.
4. Players **lose 1 point** for each losing round.
5. A player is **eliminated** when they run out of points.
6. After each round, the following is announced:
   - **Round number**
   - **Players** still in the game
   - **Numbers chosen**
   - **Remaining points** for each player
   - **Round outcome** (winners/losers)
   - **Eliminated players**, if any
7. The game is won by the **last remaining player**.
8. **Special Rule**: In the final round with two players, if one chooses **0**, the other player wins if their guess is **greater than 0**.

## How to Run

1. Compile the Java files:
   ```sh
   javac *.java
   ```
2. Start the server:
   ```sh
   java Server
   ```
3. Start the client(s) in separate terminals:
   ```sh
   java Client <Server IP Address> <Provide Port Here>
   ```
4. Follow the instructions in the client terminal to join the game.


> **NOTE:** You can run the code on a server or an online VM and join the game using its IP address. Ensure that the necessary ports are open for inbound/outbound traffic.

## Classes

### Server
- Manages the game server, handles client connections, manages games and players, and updates the leaderboard.
  
### Player
- Represents a player, storing the player's name, points, and number selection.

### Game
- Manages game logic: adding/removing players, determining winners, deducting points, and eliminating players.

### GameManager
- Executes the game, broadcasts messages, determines outcomes, and manages game flow.

### ClientHandler
- Handles client connections and client-server communication.

### GameHandler
- Manages player input for each round, ensuring a timely number of submissions.

### TimeoutHandler
- Manages the timeout for player input during each round, closing connections for players who fail to submit in time.

### Client
- The client application that connects to the server, allowing players to interact with the game.

### Ticket
- Generates and manages unique tickets for players to join the game.

## Documentation

The code includes thorough comments detailing the purpose and functionality of each class and method. It adheres to best practices in object-oriented programming and employs synchronization techniques to ensure thread safety.
