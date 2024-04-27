package game.code;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/*
 * This class Represents the client connecting to the server, which is the "Player" in our case.
 */
public class Client {

    private Socket server;
    private Scanner scanner;

    public Client() {
        try {
            // Connect to the server
            this.server = new Socket("localhost", 44900);
            this.scanner = new Scanner(System.in);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            
        }
    }

    // getters
    public Socket getServer() {
        return server;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            // Welcoming message from server
            client.displayMessage();
            System.out.print("Enter a nickname: ");
            String nickname = client.readUserInput();
            // Send the "nickname" to the server
            client.sendMessage(nickname);
            // Ticket received from the server
            String ticket = client.readMessage();
            System.out.println("Your ticket is: " + ticket);
            // Create a player
            Player player = new Player(nickname, ticket);
            // Send the player object to the server
            client.sendObject(player);

            // Game loop
            while (true) {
                // Receive available games from the server
                client.displayMessage();
                // Select a game to join or create a new game
                System.out.print("Select a game to join or create a new game: ");
                String selectedGame = client.readUserInput();
                // Send the selected game to the server
                client.sendMessage(selectedGame);

                // Success message from the server
                client.displayMessage();
                // Display list of players in joined game
                client.displayMessage();

                // Game status message
                String status = client.readMessage();
                while (!status.equals("Match start")) {
                    // Receive game status from the server
                    status = client.readMessage();
                    System.out.println("Waiting for other players to join...");
                }

                // Ready up message
                client.displayMessage();
                // Player ready to start the game
                String isReady = "";
                do {
                    isReady = client.readUserInput();
                } while (!isReady.equals("yes"));
                client.sendMessage(isReady);

                // Wait for all players to be ready and game to start
                boolean gameStarted = false;
                while (!gameStarted) {
                    String message = client.readMessage();
                    if (message.equals("Game started!")) {
                        gameStarted = true;
                    }
                }

                // Game started
                System.out.println("Game started!");

                Scanner scanner = client.getScanner(); // initialize scanner

                // Game round loop
                while (true) {
                    int selectedNumber;
                    System.out.println("Select a number between 1 and 100:");
                    selectedNumber = scanner.nextInt();
                    while (selectedNumber < 1 || selectedNumber > 100) {
                        System.out.println("Invalid number. Please select a number between 1 and 100:");
                        selectedNumber = scanner.nextInt();
                    }
                    
                    // Send the selected number to the server
                    client.sendMessage(Integer.toString(selectedNumber));
                    // Receive the result of the round from the server
                    client.displayMessage();
                    // Check if game ended
                    String gameStatus = client.readMessage();
                    System.out.println(gameStatus);
                    if (gameStatus.equals("end")) {
                        System.out.println("Game over!");
                        break; // Exit round loop as game is over
                    }
                }

                // Prompt for another game or exit
                System.out.println("Do you want to play another game? (yes/no)");
                String playAgain = client.readUserInput();
                if (!playAgain.equalsIgnoreCase("yes")) {
                    // If user doesn't want to play again, exit the game loop
                    break;
                }
                client.sendMessage(playAgain);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        } finally {
            System.out.println("Client closed.");
            try {
                client.getServer().close();
                client.getScanner().close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }
    
    // This method reads a message from the server
    public void displayMessage() {
        try {
            BufferedReader readFromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
            // Receive the message from the server
            String message = readFromServer.readLine();
            System.out.println(message);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // This method reads and returns a message from the server
    public String readMessage() {
        try {
            BufferedReader readFromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
            // Receive the message from the server
            return readFromServer.readLine();
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }
    // This method reads the user input
    public String readUserInput() {
        try {
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            return userInput.readLine();
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }   

    // This method sends the message to the server
    public void sendMessage(String data) {
        try {
            PrintWriter writeToClient = new PrintWriter(server.getOutputStream(), true);
            writeToClient.println(data);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // This method sends an object to the server
    public void sendObject(Serializable object) {
        try {
            ObjectOutputStream objOutput = new ObjectOutputStream(server.getOutputStream());
            objOutput.writeObject(object);
            objOutput.flush();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
