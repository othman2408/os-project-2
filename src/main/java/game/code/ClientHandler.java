package game.code;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/*
 * This class is responsible for handling the connection with the player.
 */

public class ClientHandler extends Thread {
    private final Server server;
    private final Socket playerSocket;
    public ClientHandler(Socket playerSocket, Server server) {
        this.playerSocket = playerSocket;
        this.server = server;
    }

    public void run() {
        try {
            System.out.println(
                    "Connection established with " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            server.sendMessage("Welcome to the famous 2/3 Game!", playerSocket);
            // Read the nickname from the player
            String nickname = server.readMessage(playerSocket);
            // Create a ticket for the player
            if (nickname != null) {
                Ticket ticket = server.createTicket(nickname);
                server.sendMessage(ticket.toString(), playerSocket);
            }
            // Read and store the player
            Player player = (Player) server.readObject(playerSocket);
            player.setPlayerSocket(playerSocket);
            server.addPlayer(player);
            String gameName = "";
            while (true) {
                // Send available games to the player
                server.sendAvailableGames(playerSocket);
                // Read the game name from the player
                gameName = server.readMessage(playerSocket);
                // Add the player to the game
                server.joinGame(gameName, player);
                Game currentGame = null;
                for (Game game : server.getGames()) {
                    if (game.getName().equals(gameName)) {
                        currentGame = game;
                        break;
                    }
                }
                // Send a message to the player that he joined the game
                server.sendMessage("You now joined " + gameName + "!", playerSocket);
                // Send to the player the list of players in the game
                server.sendMessage(server.getGamePlayers(currentGame), playerSocket);
                // Start the game when 2 player are in the lobby
                while (currentGame.getPlayers().size() < 2) {
                    server.sendMessage("Waiting for more players to join...", playerSocket);
                    // sleep for 2 seconds
                    Thread.sleep(2000);
                }
                // Minimum number of players satisfied
                server.sendMessage("Match start", playerSocket);
                // Prompt the players to ready up
                server.sendMessage("Ready? (`yes` to start)", playerSocket);
                do {
                    String ready = server.readMessage(playerSocket);
                    if (ready.equals("yes")) {
                        player.setReady(true);
                        break;
                    }
                } while (true);      
                // Wait for players to ready up
                while (!currentGame.allPlayersReady()) {
                    System.out.println("Waiting for players to ready up...");
                    Thread.sleep(2000);
                }
                server.lockGame(currentGame);
                // Game is handled by other threads (GameHandler & GameManager)
                server.sendMessage("Game started!", playerSocket);
                server.startGame(currentGame, server);
                // Player decision to play again or not
                GameManager currentGameManager = server.getGameManagerForAGame(currentGame);
                currentGameManager.join();
                String choice = server.readMessage(playerSocket);
                if (choice.equals("no")) {
                    break;
                }
                // Remove recently finished game
                server.removeGame(currentGame);
                server.removeGameManager(currentGame);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        finally {
            try {
//                 playerSocket.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }
}