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
    private Server server;
    private Socket playerSocket;
    private BufferedReader strInput;
    private PrintWriter strOutput;
    private ObjectOutputStream objOutput;
    private ObjectInputStream objInput;
    public ClientHandler(Socket playerSocket, Server server) {
        this.playerSocket = playerSocket;
        try {
            this.strInput = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
            this.strOutput = new PrintWriter(playerSocket.getOutputStream(), true);
            this.objOutput = new ObjectOutputStream(playerSocket.getOutputStream());
            this.objInput = new ObjectInputStream(playerSocket.getInputStream());
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        this.server = server;
    }

    public void run() {
        try {
            System.out.println(
                    "Connection established with " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            sendMessage("Welcome to the famous 2/3 Game!", playerSocket);
            // Read the nickname from the player
            String nickname = readMessage(playerSocket);
            // Create a ticket for the player
            if (nickname != null) {
                Ticket ticket = createTicket(nickname);
                sendMessage(ticket.toString(), playerSocket);
            }
            // Read and store the player
            Player player = (Player) readObject();
            player.setPlayerSocket(playerSocket);
            String gameName = "";
            addPlayer(player);
            // Send available games to the player
            List<Game> games = getAvailableGames();
            sendAvailableGames(games);
            // Read the game name from the player
            gameName = readMessage(playerSocket);
            // Add the player to the game
            joinGame(gameName, nickname);
            Game currentGame = null;
            for (Game game : games) {
                if (game.getName().equals(gameName)) {
                    currentGame = game;
                    break;
                }
            }
            // Send a message to the player that he joined the game
            sendMessage("You now joined " + gameName + "!", playerSocket);
            // Send to the player the list of players in the game
            sendMessage(getGamePlayers(currentGame), playerSocket);
            // Start the game when 2 player are in the lobby
            while (currentGame.getPlayers().size() < 2) {
                sendMessage("Waiting for more players to join...", playerSocket);
                // sleep for 2 seconds
                Thread.sleep(2000);
            }
            // Minimum number of players satisfied
            sendMessage("Match start", playerSocket);
            // Prompt the players to ready up
            sendMessage("Ready? (`yes` to start)", playerSocket);
            do {
                String ready = readMessage(playerSocket);
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
            // Game is handled by other threads (GameHandler & GameManager)
            sendMessage("Game started!", playerSocket);
            server.startGame(currentGame);
            // sendMessage("Would you like to play again? (y/n)", playerSocket);
            // choice = readMessage(playerSocket);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        finally {
            try {
                // objOutput.close();
                // objInput.close();
                // strOutput.close();
                // strInput.close();
                // playerSocket.close();            
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }

    // This method gets all player names in a specific game
    public String getGamePlayers(Game game) {
        String ingamePlayers = "Players: ";
        for (Player player : game.getPlayers()) {
            ingamePlayers += player.getName() + " ";
        }

        return ingamePlayers;
    }
    
    // This method adds a player to the players list
    public void addPlayer(Player player) {
        synchronized (server) {
            // Check if player exists
            for (Player p : server.getPlayers()) {
                if (p.getName().equals(player.getName())) {
                    System.out.println("Player " + player.getName() + " already exists.");
                    return;
                }
            }
            // Add the player to the player list
            server.getPlayers().add(player);
        }
        System.out.println("Player " + player.getName() + " added to the player list.");
    }

    // This method returns available games
    public List<Game> getAvailableGames() {
        List<Game> games = null;
        synchronized (server) {
            // Get the game list from the server
            games = server.getGames();
            if (games.size() == 0) {
                // generate a new game
                Game game = new Game("Default lobby");
                games.add(game);
            }
        }
        return games;
    }

    // This method sends available games to the client
    public void sendAvailableGames(List<Game> games) {
        try {
            // Send the available games to the player
            System.out.println("Sending available games to " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            String message = "Available games: ";
            for (Game game : games) {
                message = message + game.getName() + " ";
            }
            strOutput.println(message);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // This method allows a player to join a game
    public void joinGame(String gameName, String playerName) {
        List<Game> games = null;
        boolean gameExists = false;
        synchronized (server) {
            // Get the game list from the server
            games = server.getGames();
            for (Game game : games) {
                if (game.getName().equals(gameName)) {
                    // find the player in the player list
                    gameExists = true;
                    Player player = null;
                    for (Player p : server.getPlayers()) {
                        if (p.getName().equals(playerName) && p.getCurrentGame() == null) {
                            player = p;
                            break;
                        }
                    }
                    player.setCurrentGame(game);
                    game.addPlayer(player);
                    break;
                }
            }
            if (!gameExists) {
                // Create a new game
                Game game = new Game(gameName);
                Player player = null;
                for (Player p : server.getPlayers()) {
                    if (p.getName().equals(playerName)) {
                        player = p;
                        break;
                    }
                }
                game.addPlayer(player);
                games.add(game);
                server.setGames(games);
                System.out.println("Game " + gameName + " created.");
            }
        }
    }

    // This method creates a ticket for the player
    public Ticket createTicket(String nickname) {
        Ticket ticket = null;
        synchronized (server) {
            // Get the ticket list from the server
            Map<String, String> ticketList = server.getTicketList();
            System.out.println("Creating ticket for " + nickname);
            // Create a ticket for the player if he does not exist in the ticket list
            if (!ticketList.containsKey(nickname)) {
                ticket = new Ticket();
                ticketList.put(nickname, ticket.toString());
                // Display to terminal the ticket created for the player
                System.out.println("Ticket created for " + nickname + ": " + ticket);
            } else {
                // Ticket already exists for the player
                System.out.println("Ticket already exists for " + nickname + ": " + ticketList.get(nickname));
                ticket = new Ticket();
                ticket.setTicket(ticketList.get(nickname));
                sendMessage("Welcome back " + nickname + "!", playerSocket);
            }
        }
        return ticket;
    }

    // This method sends a message to the player
    public void sendMessage(String message, Socket player) {
        try {
            // Send the message to the player
            System.out.println("Sending message to " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            strOutput.println(message);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // This method reads a message from the player
    public String readMessage(Socket player) {
        try {
            // Read the message from the player
            System.out.println("Reading message from " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            String message = strInput.readLine();
            return message;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

    // This method sends an object to the server
    public void sendObject(Serializable object) {
        try {
            System.out.println("Sending object to " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            objOutput.writeObject(object);
            objOutput.flush();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // This method reads an object from the server
    public Object readObject() {
        try {
            System.out.println("Reading object from " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            Object object = objInput.readObject();
            return object;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }
}