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
        System.out.println("Starting the game with " + players.size() + " players");
        List<GameHandler> gameHandlers = new ArrayList<>();
        while (players.size() > 1) {
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
                notifyRoundOutcome(selectedNumbers, winners);
                game.setRoundNumber(game.getRoundNumber() + 1);
                if (players.size() != 1) {
                    for (Player player : game.getPlayers()) {
                        sendMessage("start", player.getPlayerSocket());
                    }
                }

                if (players.size() == 1) {
                    game.endGame();
                    for (Player player : game.getPlayers()) {
                        System.out.println("sending ");
                        GameCleanUp cleanUp = new GameCleanUp(player.getPlayerSocket());
                        cleanUp.start();
                    }

                }
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
            game.clearSelectedNumbers();
            gameHandlers.clear();
        }
    } 

    // This method to notify players about round outcome
    public void notifyRoundOutcome(List<Integer> selectedNumbers, List<Player> winners) {
        System.out.println("Round " + game.getRoundNumber() + " Outcome:");
        System.out.println("Selected Numbers: " + selectedNumbers);
        for (Player player : game.getPlayers()) {
            String outcome;
            if (winners.contains(player)) {
                outcome = "Winner";
            } else {
                outcome = "Loser";
            }
            System.out.println(
                    "Player: " + player.getName() + ", Outcome: " + outcome + ", Points: " + game.getPoints().get(player));
            sendMessage( "Player: " + player.getName() + ", Outcome: " + outcome + ", Points: " + game.getPoints().get(player), player.getPlayerSocket());
        }
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
