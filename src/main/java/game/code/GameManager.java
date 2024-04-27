package game.code;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameManager extends Thread {
    private final Server server;
    private final Game game;
    public GameManager(Game game, Server server) {
        this.game = game;
        this.server = server;
    }

    public void run() {
        List<Socket> playersSockets = new ArrayList<>();
        for (Player player : server.getPlayers()) {
            playersSockets.add(player.getPlayerSocket());
        }
        System.out.println("Starting the game with " + server.getPlayers().size() + " players");
        List<GameHandler> gameHandlers = new ArrayList<>();
        while (true) {
            try {
                for (Player player : server.getPlayers()) {
                    GameHandler handler = new GameHandler(player.getPlayerSocket(), game, server);
                    gameHandlers.add(handler);
                }
        
                for (GameHandler handler : gameHandlers) {
                    handler.start();
                }

                for (GameHandler handler : gameHandlers) {
                    try {
                        handler.join();
                    } catch (InterruptedException e) {
                        System.out.println("Error: " + e);
                    }
                }
                List<Integer> selectedNumbers = game.getSelectedNumbers();
                System.out.println("Selected Numbers: " + selectedNumbers.toString());
                double average = game.calculateAverage(selectedNumbers);
                List<Player> winners = game.determineRoundWinners(selectedNumbers, average);
                game.deductPointsFromLosers(winners); 
                game.eliminatePlayersWithNoPoints();
                notifyRoundOutcome(selectedNumbers, winners, playersSockets);
                game.setRoundNumber(game.getRoundNumber() + 1);
                System.out.println("Number of players left: " + game.getPlayers().size());
                if (game.getPlayers().size() == 1) {
                    game.endGame();
                    broadcastMessage("end", playersSockets);
                    break;
                } else {
                    broadcastMessage("continue", playersSockets);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
            game.clearSelectedNumbers();
            gameHandlers.clear();
        }
    } 

    // This method broadcasts a message to all players
    public void broadcastMessage(String message, List<Socket> playersSockets) {
        for (Socket playerSocket : playersSockets) {
            server.sendMessage(message, playerSocket);
        }
    }
    // This method to notify players about round outcome
    public void notifyRoundOutcome(List<Integer> selectedNumbers, List<Player> winners, List<Socket> playerSockets) {
        // Create one string message to broadcast to all players
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
    
}
