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
        broadcastMessage(game.getGamePlayers(), playersSockets);
        List<GameHandler> gameHandlers = new ArrayList<>();
        while (true) {
            try {
                for (Player player : game.getPlayers()) {
                    GameHandler handler = new GameHandler(player, game, server);
                    Thread.currentThread().setName(player.getName());
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
                if (selectedNumbers.size() == 0) {
                    System.out.println("No numbers selected. Ending game...");
                    break;
                }
                System.out.println("Selected Numbers: " + selectedNumbers.toString());
                // Check if there are only two players left and it is the last round
                if (game.getPlayers().size() == 2 && game.isLastRound()) {
                    Player player1 = game.getPlayers().get(0);
                    Player player2 = game.getPlayers().get(1);
                    if (selectedNumbers.contains(0)) {
                        int indexOfZero = selectedNumbers.indexOf(0);
                        if (indexOfZero == 0 && selectedNumbers.get(1) > 0) {
                            List<Player> winners = new ArrayList<>();
                            winners.add(player2);
                            game.deductPointsFromLosers(winners); // Deduct points from losing player
                            game.eliminatePlayersWithNoPoints(); // Eliminate player with no points
                            notifyRoundOutcome(selectedNumbers, winners, playersSockets);
                            game.endGame(); // End the game
                            incrementWinnerWins(player2); // Increment winner's wins
                            server.updateLeaderboard(); // Update leaderboard
                            break;
                        } else if (indexOfZero == 1 && selectedNumbers.get(0) > 0) {
                            List<Player> winners = new ArrayList<>();
                            winners.add(player1);
                            game.deductPointsFromLosers(winners); // Deduct points from losing player
                            game.eliminatePlayersWithNoPoints(); // Eliminate player with no points
                            notifyRoundOutcome(selectedNumbers, winners, playersSockets);
                            game.endGame(); // End the game
                            incrementWinnerWins(player1); // Increment winner's wins
                            server.updateLeaderboard(); // Update leaderboard
                            break;
                        }
                    }
                }    
                double average = game.calculateAverage(selectedNumbers);
                List<Player> winners = game.determineRoundWinners(selectedNumbers, game.getPlayers(), average);
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
    private final Player player;
    private final Game game;
    private final Server server;
    private TimeoutHandler timeoutHandler;

    public GameHandler(Player player, Game game, Server server) {
        this.player = player;
        this.game = game;
        this.server = server;
        timeoutHandler = new TimeoutHandler(player.getPlayerSocket(), server);
    }

    public void run() {
        int selectedNumber = 0;
        boolean validInput = false;
        timeoutHandler.start();
        server.sendMessage("Enter a number between 0 and 100: ", player.getPlayerSocket());
        do {
            try {
                String input = server.readMessage(player.getPlayerSocket());
                selectedNumber = Integer.parseInt(input);
                timeoutHandler.restartTime();
                if (selectedNumber >= 0 && selectedNumber <= 100) {
                    validInput = true;
                } else {
                    server.sendMessage("Invalid input. Please enter a number between 0 and 100.", player.getPlayerSocket());
                }
            } catch (NumberFormatException e) {
                server.sendMessage("Invalid input. Please enter a valid integer.", player.getPlayerSocket());
            }
        } while (!validInput);
        System.out.println(Thread.currentThread().getName() + "thread selected a number and exited the loop...");
        player.setNumberSelection(selectedNumber);
        game.getSelectedNumbers().add(selectedNumber);
        timeoutHandler.interrupt();
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
            server.removePlayer(playerSocket);
            // End connection
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