import java.io.IOException;
import java.net.Socket;
import java.sql.Time;
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
                    incrementWinnerWins(game.getPlayers().get(0));
                    server.updateLeaderboard();
                    break;
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
        message.append("╔═══════════════════════════════════════════════════════════════╗\n");
        message.append("║                       Round Outcome ").append(game.getRoundNumber()).append(" \t\t\t║\n");
        message.append("╠═══════════════════════════════════════════════════════════════╣\n");
        message.append("║ Player Name       | Selected Number | Outcome | Points        ║\n");
        message.append("╠═══════════════════════════════════════════════════════════════╣\n");
        for (Player player : game.getPlayers()) {
            String outcome = "Lost";
            if (winners.contains(player)) {
                outcome = "Won";
            }
            String playerName = String.format("%-18s", player.getName());
            int selectedNumber = selectedNumbers.get(game.getPlayers().indexOf(player));
            String selectedNumberFormatted = String.format("%-16d", selectedNumber);
            String outcomeFormatted = String.format("%-7s", outcome);
            int points = player.getPoints();
            String pointsFormatted = String.format("%-7d", points);
            message.append("║ ").append(playerName).append("| ").append(selectedNumberFormatted).append("| ")
                    .append(outcomeFormatted).append(" | ").append(pointsFormatted).append("       ║ \n");
        }
        message.append("╚═══════════════════════════════════════════════════════════════╝\n");
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
    private TimeoutHandler timeoutHandler;

    public GameHandler(Socket playerSocket, Game game, Server server) {
        this.playerSocket = playerSocket;
        this.game = game;
        this.server = server;
        timeoutHandler = new TimeoutHandler(playerSocket, server);
    }

    public void run() {
        int selectedNumber = 0;
        boolean validInput = false;
        do {
            try {
                timeoutHandler.start();
                server.sendMessage("Enter a number between 1 and 100: ", playerSocket);
                String input = server.readMessage(playerSocket);
                selectedNumber = Integer.parseInt(input);
                timeoutHandler.restartTime();
                if (selectedNumber >= 1 && selectedNumber <= 100) {
                    validInput = true;
                } else {
                    server.sendMessage("Invalid input. Please enter a number between 1 and 100.", playerSocket);
                }
            } catch (NumberFormatException e) {
                server.sendMessage("Invalid input. Please enter a valid integer.", playerSocket);
            }
        } while (!validInput);
        game.getSelectedNumbers().add(selectedNumber);
    }
}

// ============================================================================================
// =================================| TimeoutHandler |=========================================
// ============================================================================================
class TimeoutHandler extends Thread {
    private final Socket playerSocket;
    private final Server server;
    private long endTime;

    public TimeoutHandler(Socket playerSocket, Server server) {
        this.playerSocket = playerSocket;
        this.server = server;
    }

    public void run() {
        try {
            endTime = System.currentTimeMillis() + 60000;
            do {
                Thread.sleep(3000);
            } while (System.currentTimeMillis() < endTime);
            server.sendMessage("Time's up! You did not enter a number, connection closed.", playerSocket);
            playerSocket.close();
        } catch (InterruptedException | IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public void restartTime() {
        endTime = System.currentTimeMillis() + 60000;
        System.out.println("Time restarted...");
    }
}
