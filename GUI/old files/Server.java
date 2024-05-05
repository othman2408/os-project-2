import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    private static Map<String, Player> playersList;
    private static List<Game> gamesList;
    private static Map<String, Ticket> ticketsList;

    public Server() {
        playersList = new HashMap<>();
        gamesList = new ArrayList<>();
        ticketsList = new HashMap<>();
    }

    // Method: generateTicket
    private static String getTicket(String playerName) {
        // Check if the player already has a ticket
        if(ticketsList.containsKey(playerName)) {
            System.out.println("Player " + playerName + " already has a ticket");
            return ticketsList.get(playerName).toString();
        }

        // if not, generate a new ticket and add it to the tickets list
        Ticket.generateTicket(playerName);
        ticketsList.put(playerName, new Ticket());

        return ticketsList.get(playerName).toString();
    }


    // Method: addPlayer
    public static void addPlayer(String playerName, int score) {
        playersList.put(playerName, new Player(playerName, score));
    }

    // Method: removePlayer
    public static void removePlayer(String playerName) {
        playersList.remove(playerName);
    }

    // Method: addGame

    // Method: removeGame

    // Method: getPlayersList

    // Method: getGamesList

    // Method: getTicketsList

    


    

 


    

    public static void main(String[] args) {
        
        System.out.println("================================================" + "\n");

        Server server = new Server();

        System.out.println(getTicket("othman"));

        // Same player name, should return the same ticket
        System.out.println(getTicket("othman"));

        // Print all the tickets wityh their corresponding player names
        System.out.println("\n" + "Tickets List: ");
        for (Map.Entry<String, Ticket> entry : ticketsList.entrySet()) {
            System.out.println("Player Name: " + entry.getKey() + " Ticket: " + entry.getValue().toString());
        }



        System.out.println("\n" + "================================================" );
    }
}

