package game.code;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private Map<String, String> ticketList; // Map to store ticket-to-nickname mappings
    private List<Player> players; // List to store connected players
    private List<Game> games; // List to store active games
    private Map<Game, GameManager> gameManager; // Map to store each gameManager with its game
    public Server() {
        ticketList = Collections.synchronizedMap(new HashMap<>());
        players = Collections.synchronizedList(new ArrayList<>());
        games = Collections.synchronizedList(new ArrayList<>());
        gameManager = Collections.synchronizedMap(new HashMap<>());
    }

    // get ticketList
    public Map<String, String> getTicketList() {
        return ticketList;
    }

    // get players
    public List<Player> getPlayers() {
        return players;
    }

    // get games
    public List<Game> getGames() {
        return games;
    }

    // get gameManager
    public Map<Game, GameManager> getGameManager() {
        return gameManager;
    }

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        int port = 44900;
        try {
            Server server = new Server();
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
            while (true) {
                Socket player = serverSocket.accept();
                ClientHandler handler = new ClientHandler(player, server);
                handler.start();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        finally {
            try {
                serverSocket.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }

    // This method starts the game
    public synchronized void startGame(Game game, Server server) {
        if (!gameManager.containsKey(game)) {
            GameManager gameManagerThread = new GameManager(game, server);
            gameManager.put(game, gameManagerThread);
            gameManagerThread.start();    
        }
    }

    // This method adds a player to a game
    public synchronized void joinGame(String gameName, Player player) {
        boolean gameExists = false;
        for (Game game : games) {
            if (game.getName().equals(gameName) && player.getCurrentGame() == null && game.isLocked() == false) {
                // Add player to existing game
                game.addPlayer(player);
                setPlayerCurrentGame(player, game);
                gameExists = true;
            } 

        }
        if (!gameExists) {
            // Create a new game
            Game game = new Game(gameName);
            player.setCurrentGame(game);
            game.addPlayer(player);
            games.add(game);
            System.out.println("Game " + gameName + " created.");
        }
    }

    // This method sends the available games to players
    public synchronized void sendAvailableGames(Socket playerSocket) {
        try {
            // Send the available games to the player
            System.out.println("Sending available games to " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            String message = "Available games: ";
            if (games.isEmpty()) {
                Game game = new Game("Default lobby");
                games.add(game);
            }
            for (Game game : games) {
                message = message + game.getName() + " ";
            }
            sendMessage(message, playerSocket);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // This method returns a GameManager based on a given game
    public GameManager getGameManagerForAGame(Game game) {
        return gameManager.get(game);
    }

    // This method returns in game players
    public String getGamePlayers(Game game) {
        String ingamePlayers = "Players: ";
        for (Player player : game.getPlayers()) {
            ingamePlayers += player.getName() + " ";
        }

        return ingamePlayers;
    }

    // This method removes the game
    public synchronized void removeGame(Game game) {
        gameManager.remove(game);
        games.remove(game);
    }
    // This method adds a player to the players list
    public synchronized void addPlayer(Player player) {
        if (!players.contains(player)) {
            System.out.println("Adding player " + player.getName() + " to the server");
            players.add(player);
        }
        else {
            System.out.println("Player " + player.getName() + " already exists in the server");
        }
    }

    // This method locks a game
    public synchronized void lockGame(Game game) {
        for (Game g : games) {
            if (g.getName().equals(game.getName())) {
                g.lockGame();
            }
        }
    }

    // This method sets the player current game
    public synchronized void setPlayerCurrentGame(Player player, Game game) {
        // find the player
        for (Player p : players) {
            if (p.getName().equals(player.getName())) {
                p.setCurrentGame(game);
            }
        }
    }
    // This method removes a player from the players list
    public synchronized void removePlayer(Player player) {
        if (players.contains(player)) {
            System.out.println("Removing player " + player.getName() + " from the server");
            players.remove(player);
        }
        else {
            System.out.println("Player " + player.getName() + " does not exist in the server");
        }
    }

    // This method creates a ticket for the player
    public synchronized Ticket createTicket(String nickname) {
        Ticket ticket;
        // Get the ticket list from the server
        System.out.println("Creating ticket for " + nickname);
        // Create a ticket for the player if he does not exist in the ticket list
        if (!hasTicket(nickname)) {
            ticket = new Ticket();
            ticketList.put(nickname, ticket.toString());
            // Display to terminal the ticket created for the player
            System.out.println("Ticket created for " + nickname + ": " + ticket);
        } else {
            // Ticket already exists for the player
            System.out.println("Ticket already exists for " + nickname + ": " + ticketList.get(nickname));
            ticket = new Ticket();
            ticket.setTicket(ticketList.get(nickname));
        }
    return ticket;
    }

    // This method removes a GameManager of a specific game
    public synchronized void removeGameManager(Game game) {
        gameManager.remove(game);
    }

    // This method checks if a player has a ticket
    public boolean hasTicket(String nickname) {
        return ticketList.containsKey(nickname);
    }

    // This method sends a message to the player
    public void sendMessage(String message, Socket playerSocket) {
        try {
            PrintWriter writeToClient = new PrintWriter(playerSocket.getOutputStream(), true);
            // Send the message to the player
            System.out.println("Sending message to " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            writeToClient.println(message);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // This method reads a message from the player
    public String readMessage(Socket playerSocket) {
        try {
            BufferedReader readFromClient = new BufferedReader((new InputStreamReader(playerSocket.getInputStream())));
            // Read the message from the player
            System.out.println("Reading message from " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            return readFromClient.readLine();
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

    // This method reads an object from the server
    public Object readObject(Socket playerSocket) {
        try {
            ObjectInputStream objInput = new ObjectInputStream(playerSocket.getInputStream());
            // Read object from client
            System.out.println("Reading object from " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            return objInput.readObject();
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

}
