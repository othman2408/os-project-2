package game.code;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.Scanner;

/*
 * This class Represents the client connecting to the server, which is the "Player" in our case.
 */
public class Client {

    private Socket server;
    private BufferedReader strInput;
    private BufferedReader userInput;
    private PrintWriter strOutput;
    private ObjectOutputStream objOutput;
    private ObjectInputStream objInput;
    private Scanner scanner;
    public Client() {
        try {
            // Connect to the server
            this.server = new Socket("localhost", 44900);
            // Reader and Writer initialization
            this.strInput = new BufferedReader(new InputStreamReader(server.getInputStream()));
            this.userInput = new BufferedReader(new InputStreamReader(System.in));
            this.strOutput = new PrintWriter(server.getOutputStream(), true);
            this.objOutput = new ObjectOutputStream(server.getOutputStream());
            this.objInput = new ObjectInputStream(server.getInputStream());
            this.scanner = new Scanner(System.in);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            
        }

    }

    // getters
    public Socket getServer() {
        return server;
    }


    public BufferedReader getInput() {
        return strInput;
    }

    public BufferedReader getUserInput() {
        return userInput;
    }

    public PrintWriter getOutput() {
        return strOutput;
    }

    public ObjectOutputStream getOutputStream() {
        return objOutput;
    }

    public ObjectInputStream getInputStream() {
        return objInput;
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

                // Game round loop
                while (true) {
                    Scanner scanner = client.getScanner();
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
                    String roundResult = client.readMessage();
                    System.out.println(roundResult);
                    // Check if game ended
                    String gameStatus = client.readMessage();
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
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        } finally {
            System.out.println("Client closed.");
            try {
                client.getInput().close();
                client.getUserInput().close();
                client.getOutput().close();
                client.getServer().close();
                client.getOutputStream().close();
                client.getInputStream().close();
                client.getScanner().close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }
    
    // This method reads a message from the server
    public void displayMessage() {
        try {
            // Recieve the message from the server
            String message = strInput.readLine();
            System.out.println(message);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // This methods reads and returns a message from the server
    public String readMessage() {
        try {
            // Recieve the message from the server
            String message = strInput.readLine();
            return message;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }
    // This method reads the user input
    public String readUserInput() {
        try {
            String data = userInput.readLine();
            return data;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }   

    // This method sends the message to the server
    public void sendMessage(String data) {
        try {
            strOutput.println(data);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // This method sends an object to the server
    public void sendObject(Serializable object) {
        try {
            objOutput.writeObject(object);
            objOutput.flush();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // This method reads an object from the server
    public Object readObject() {
        try {
            Object object = objInput.readObject();
            return object;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }
}
