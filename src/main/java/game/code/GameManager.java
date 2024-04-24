package game.code;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameManager extends Thread {
    private Game game;
    private BufferedReader strInput;
    private PrintWriter strOutput;

    public GameManager(Game game) {
        this.game = game;
    }

    public void run() {
        List<Player> players = game.getPlayers();
        List<Socket> playerSockets = new ArrayList<>();
        boolean isFinished = false;
        for (Player player : players) {
            playerSockets.add(player.getPlayerSocket());
        }
        System.out.println("Starting the game with " + players.size() + " players");
        List<GameHandler> gameHandlers = new ArrayList<>();
        while (!isFinished) {
            try {
                for (Player player : players) {
                    GameHandler handler = new GameHandler(player.getPlayerSocket(), game);
                    gameHandlers.add(handler);
                }
        
                for (GameHandler handler : gameHandlers) {
                    handler.start();
                }

                for (GameHandler handler : gameHandlers) {
                    try {
                        handler.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                List<Integer> selectedNumbers = game.getSelectedNumbers();
                System.out.println("Selected Numbers: " + selectedNumbers.toString());
                double average = game.calculateAverage(selectedNumbers);
                List<Player> winners = game.determineRoundWinners(selectedNumbers, average);
                game.deductPointsFromLosers(winners);
                game.eliminatePlayersWithNoPoints();
                notifyRoundOutcome(selectedNumbers, winners, playerSockets);
                game.setRoundNumber(game.getRoundNumber() + 1);
                System.out.println("Number of players left: " + players.size());
                if (players.size() == 1) {
                    isFinished = true;
                    game.endGame();
                    broadcastMessage("end", playerSockets);
                } else {
                    broadcastMessage("continue", playerSockets);
                }

            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
            game.clearSelectedNumbers();
            gameHandlers.clear();
        }
    } 

    // This method broadcastsa a message to all players
    public void broadcastMessage(String message, List<Socket> playerSockets) {
        for (Socket playerSocket : playerSockets) {
            sendMessage(message, playerSocket);
        }
    }
    // This method to notify players about round outcome
    public void notifyRoundOutcome(List<Integer> selectedNumbers, List<Player> winners, List<Socket> playerSockets) {
        // Create one string message to be broadcasted to all players
        String message = "Round " + game.getRoundNumber() + " Outcome: ";
        message += "Selected Numbers: " + selectedNumbers.toString() + " ";
        for (Player player : game.getPlayers()) {
            String outcome;
            if (winners.contains(player)) {
                outcome = "Winner";
            } else {
                outcome = "Loser";
            }
            message += "Player: " + player.getName() + ", Outcome: " + outcome + ", Points: " + player.getPoints() + " ";
        }
        broadcastMessage(message, playerSockets);
    }
    
    // This method sends a message to the player
    public void sendMessage(String message, Socket playerSocket) {
        try {
            // Send the message to the player
            strOutput = new PrintWriter(playerSocket.getOutputStream(), true);
            System.out.println("Sending message to " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            strOutput.println(message);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // This method reads a message from the player
    public String readMessage(Socket playerSocket) {
        try {
            // Read the message from the player
            strInput = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
            System.out.println("Reading message from " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            String message = strInput.readLine();
            return message;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }    
}
