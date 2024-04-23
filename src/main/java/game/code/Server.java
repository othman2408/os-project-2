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
        return this.ticketList;
    }

    // set ticketList
    public void setTicketList(Map<String, String> ticketList) {
        this.ticketList = ticketList;
    }

    // set players
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    // get players
    public List<Player> getPlayers() {
        return this.players;
    }
    // get games
    public List<Game> getGames() {
        return this.games;
    }

    // set games
    public void setGames(List<Game> games) {
        this.games = games;
    }

    // get gameManager
    public Map<Game, GameManager> getGameManager() {
        return this.gameManager;
    }

    // set gameManager
    public void setGameManager(Map<Game, GameManager> gameManager) {
        this.gameManager = gameManager;
    }

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Server server = new Server();
        int port = 44900;
        try {
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
    public synchronized void startGame(Game game) {
        if (!gameManager.containsKey(game)) {
            System.out.println("Thread: " + Thread.currentThread().getName() + " Starting game: " + game.getName());
            GameManager gameManagerThread = new GameManager(game);
            gameManager.put(game, gameManagerThread);
            gameManagerThread.start();    
        }
    }

    // This method removes the game
    public synchronized void removeGame(Game game) {
        gameManager.remove(game);
        games.remove(game);
    }
}
