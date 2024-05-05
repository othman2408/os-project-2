package game.code;

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
        for (Player player : game.getPlayers()) {
            playersSockets.add(player.getPlayerSocket());
        }
        System.out.println("Starting the game with " + game.getPlayers().size() + " players");
        // Send the list of players in the game to all players
        System.out.println("Game: " + game.getName());
        broadcastMessage("Game started!", playersSockets);
        broadcastMessage(game.getPlayers().toString(), playersSockets);
        List<GameHandler> gameHandlers = new ArrayList<>();
        while (true) {
            try {
                for (Player player : game.getPlayers()) {
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
                    incrementWinnerWins(game.getPlayers().get(0));
                    server.updateLeaderboard();
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

    // Increment the number of wins for the winner
    private void incrementWinnerWins(Player winner) {
        winner.setNumOfWins(winner.getNumOfWins() + 1);
    }

    // This method to notify players about round outcome
    public void notifyRoundOutcome(List<Integer> selectedNumbers, List<Player> winners, List<Socket> playerSockets) {
        StringBuilder message = new StringBuilder();
        message.append("Round ").append(game.getRoundNumber()).append(" Outcome: \n");
        message.append("Selected Numbers: ").append(selectedNumbers.toString()).append("\n");
        message.append("╔════════════════════════════════════════════════════════╗\n");
        for (Player player : game.getPlayers()) {
            String outcome;
            if (winners.contains(player)) {
                outcome = "Winner";
            } else {
                outcome = "Loser";
            }
            message.append("║ Player: ").append(String.format("%-16s", player.getName()))
                    .append(" Outcome: ").append(String.format("%-7s", outcome))
                    .append(" Points: ").append(String.format("%-4d", player.getPoints())).append(" ║\n");
        }
        message.append("╚════════════════════════════════════════════════════════╝\n");
        broadcastMessage(message.toString(), playerSockets);
    }

}

// =========================================================================
// ========================| Game Handler |=================================
// =========================================================================
class GameHandler extends Thread {
    private final Socket playerSocket;
    private final Game game;
    private final Server server;

    public GameHandler(Socket playerSocket, Game game, Server server) {
        this.playerSocket = playerSocket;
        this.game = game;
        this.server = server;

    }

    public void run() {
        int selectedNumber = Integer.parseInt(server.readMessage(playerSocket));
        game.getSelectedNumbers().add(selectedNumber);
    }
}
