package game.code;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Server {
    // Instantiate a signleton instance of the server
    private static Server server = null;
    private Map<String, String> ticketList; // Map to store ticket-to-nickname mappings
    private Map<Game, GameManager> gameManager; // Map to store each gameManager with its game
    private Map<Player, Game> playerMap; // Map to store each player with its gamea
    private List<Player> players; // List to store connected players
    private List<Game> games; // List to store active games

    public Server() {
        ticketList = Collections.synchronizedMap(new HashMap<>());
        gameManager = Collections.synchronizedMap(new HashMap<>());
        playerMap = Collections.synchronizedMap(new HashMap<>());
        players = Collections.synchronizedList(new ArrayList<>());
        games = Collections.synchronizedList(new ArrayList<>());
        if (server == null) {
            server = this;
        }
    }

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        final int PORT = 19400;
        try {
            Server server = new Server();
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on PORT: " + PORT);
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

    // ==================== Getters ====================
    public Map<String, String> getTicketList() {
        return ticketList;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Game> getGames() {
        return games;
    }

    public Map<Game, GameManager> getGameManager() {
        return gameManager;
    }

    // This method handles the generation of a ticket for a player
    public String handleGetTicket(Socket playerSocket) {
        String username = "";
        try {
            while (username.equals("")) {
                sendMessage("Enter your username: ", playerSocket);
                username = readMessage(playerSocket);

            }

            // Check if the username is already taken
            if (isUsernameTaken(username)) {
                sendMessage("Username already taken. Please try again.", playerSocket);
                return null;
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        String ticket = getTicket(username);
        sendMessage("Your ticket is: " + ticket, playerSocket);
        return ticket;
    }

    // This method checks if a username is already taken
    private boolean isUsernameTaken(String username) {
        for (Player player : players) {
            if (player.getName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    // This method returns a ticket for a given username
    public String getTicket(String username) {
        if (ticketList.containsKey(username)) {
            return ticketList.get(username);
        } else {
            // generate a new ticket, add the player to the players list and return the
            // ticket
            Ticket ticket = new Ticket(username);
            ticketList.put(username, ticket.toString());
            Player player = new Player(username, ticket);
            players.add(player);
            return ticket.toString();
        }
    }

    // This method sends the available games to players
    public void getAvailableGames(Socket playerSocket) {
        try {
            // Send the available games to the player
            System.out.println(
                    "Sending available games to " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
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

    // This method sends the player list to the player.
    public void handleGetPlayerList(Socket playerSocket) {
        sendMessage("Player List: ", playerSocket);
        sendMessage("╔══════════════════════════════════════╗", playerSocket);
        sendMessage("║ Username          Ticket             ║", playerSocket);
        sendMessage("╠══════════════════════════════════════╣", playerSocket);

        // Loop over the ticket list and send the player list to the player
        for (Map.Entry<String, String> entry : ticketList.entrySet()) {
            String username = entry.getKey();
            String ticket = entry.getValue();
            sendMessage(String.format("║ %-16s %-12s   ║", username, ticket), playerSocket);
        }

        sendMessage("╚══════════════════════════════════════╝", playerSocket);
    }

    // =============================================================================
    // ===============================| Game Methods |==============================
    // =============================================================================

    // This method adds a player to a chosen game given the gameName and ticket
    public void joinGame(Socket playerSocket, String ticket) {
        Game selectedGame = null;
        Player player = null;
        try {
            // Get the game name from the player
            sendMessage("Enter the game name: ", playerSocket);
            String gameName = readMessage(playerSocket);
            // Check if the player has a ticket
            if (ticketList.containsValue(ticket)) {
                // Get the player's username
                String username = "";
                for (Map.Entry<String, String> entry : ticketList.entrySet()) {
                    if (entry.getValue().equals(ticket)) {
                        username = entry.getKey();
                    }
                }
                // Find the player in the players list
                for (Player p : players) {
                    if (p.getName().equals(username)) {
                        player = p;
                        player.setPlayerSocket(playerSocket);
                    }
                }
                // Check if the player is already in a game
                if (playerMap.containsKey(player)) {
                    sendMessage("You are already in a game", playerSocket);
                } else {
                    // Check if the game exists
                    boolean gameExists = false;
                    for (Game game : games) {
                        if (game.getName().equals(gameName)) {
                            gameExists = true;
                            selectedGame = game;
                        }
                    }
                    // Create a new game if it does not exist
                    if (!gameExists) {
                        selectedGame = new Game(gameName);
                        games.add(selectedGame);
                        System.out.println("Game " + gameName + " created.");
                    }
                    // Add the player to the game
                    if (!playerMap.containsKey(player)) {
                        playerMap.put(player, selectedGame);
                        selectedGame.addPlayer(player);
                        System.out.println("Player " + username + " joined " + selectedGame.getName());
                        sendMessage("You have joined " + selectedGame.getName(), playerSocket);
                    }
                }
            } else {
                sendMessage("Invalid ticket, try generating a new ticket to fix this issue.", playerSocket);
                return;
            }
            startGame(selectedGame, server, playerSocket, player);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // This method starts a game
    public void startGame(Game game, Server server, Socket playerSocket, Player player) {
        hasEnoughPlayers(game, playerSocket);
        askPlayersToReady(player, playerSocket);
        // Start the game if all players are ready
        while (!game.allPlayersReady()) {
            try {
                System.out.println("Waiting for other players to ready up...");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Error: " + e);
            }
        }
        synchronized (this) {
            if (!gameManager.containsKey(game)) {
                GameManager gameManagerThread = new GameManager(game, server);
                gameManager.put(game, gameManagerThread);
                gameManagerThread.start();
            }
        }
        try {
            gameManager.get(game).join();
            gameManager.remove(game);
            games.remove(game);
        } catch (InterruptedException e) {
            System.out.println("Error: " + e);
        }
    }

    // This method asks players to ready up
    public void askPlayersToReady(Player player, Socket playerSocket) {
        sendMessage("Ready up! Type `ready` to start.", playerSocket);
        do {
            String isReady = readMessage(playerSocket);
            if (isReady.equals("ready")) {
                player.setReady(true);
                break;
            }
        } while (true);
    }

    // This method check if a game has at least 2 players
    public void hasEnoughPlayers(Game game, Socket playerSocket) {
        while (game.getPlayers().size() < 2) {
            try {
                Thread.sleep(1500);
                sendMessage("Waiting for other players to join...", playerSocket);
            } catch (InterruptedException e) {
                System.out.println("Error: " + e);
            }
        }
    }

    // This method sends a message to the player
    public void sendMessage(String message, Socket playerSocket) {
        try {
            PrintWriter toClient = new PrintWriter(playerSocket.getOutputStream(), true);
            // Send the message to the player
            System.out.println("Sending message to " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            toClient.println(message);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // This method shows the menu to the player
    // This method generates the menu for the player
    public String getMenu() {
        return "╔══════════════════════╗\n" +
                "║      Game Menu       ║\n" +
                "╠══════════════════════╣\n" +
                "║ 1. Get a Ticket      ║\n" +
                "║ 2. Available Games   ║\n" +
                "║ 3. Join/Create a Game║\n" +
                "║ 4. Connected Players ║\n" +
                "║ 5. Exit              ║\n" +
                "╚══════════════════════╝";
    }

    // This method reads a message from the player
    public String readMessage(Socket playerSocket) {
        try {
            BufferedReader fromClient = new BufferedReader((new InputStreamReader(playerSocket.getInputStream())));
            // Read the message from the player
            System.out.println("Reading message from " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            return fromClient.readLine();
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

    // This method reads an object from the server
    public Object readObject(Socket playerSocket) {
        try {
            ObjectInputStream objFromClient = new ObjectInputStream(playerSocket.getInputStream());
            // Read object from client
            System.out.println("Reading object from " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            return objFromClient.readObject();
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

}

// =============================================================================
// ===========================| Client Handler Thread |=========================
// =============================================================================
class ClientHandler extends Thread {
    private final Server server;
    private final Socket playerSocket;
    private String ticket;

    public ClientHandler(Socket playerSocket, Server server) {
        this.playerSocket = playerSocket;
        this.server = server;
    }

    public void run() {
        try {
            System.out.println(
                    "Connection established with " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            server.sendMessage("Welcome to the famous 2/3 Game!", playerSocket);
            do {
                server.sendMessage(server.getMenu(), playerSocket);
                server.sendMessage("Enter your choice: ", playerSocket);
                String choice = server.readMessage(playerSocket);
                System.out.println("Client choice: " + choice);
                switch (choice) {
                    case "1":
                        ticket = server.handleGetTicket(playerSocket);
                        break;
                    case "2":
                        server.getAvailableGames(playerSocket);
                        break;
                    case "3":
                        server.joinGame(playerSocket, ticket);
                        break;
                    case "4":
                        server.handleGetPlayerList(playerSocket);
                        break;
                    case "5":
                        server.sendMessage("Goodbye!", playerSocket);
                        return;
                    default:
                        server.sendMessage("Invalid choice. Please try again.", playerSocket);
                }
            } while (true);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                playerSocket.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }

    }
}
