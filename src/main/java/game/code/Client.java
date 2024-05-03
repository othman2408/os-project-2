package game.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {
        final String SERVER_ADDRESS = "localhost";
        final int PORT = 19400;
        Socket client = null;
        PrintWriter toServer = null;
        BufferedReader fromServer = null;
        BufferedReader fromUser = null;
        String serverInput, userInput;

        try {
            client = new Socket(SERVER_ADDRESS, PORT);
            toServer = new PrintWriter(client.getOutputStream(), true);
            fromServer = new BufferedReader(new InputStreamReader(client.getInputStream()));
            fromUser = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Connected to server " + SERVER_ADDRESS + ":" + PORT);

            while (true) {
                // Read and print messages from the server
                while (fromServer.ready()) {
                    serverInput = fromServer.readLine();
                    System.out.println(serverInput);
                    if (serverInput.equals("Goodbye!")) {
                        return;
                    }
                }

                // Read user input from the console and send it to the server
                if (fromUser.ready()) {
                    userInput = fromUser.readLine();
                    toServer.println(userInput);
                    // Player joined a game
                    if (userInput.equalsIgnoreCase("ready")) {
                        while (!fromServer.readLine().equals("Game started!")) {
                            System.out.println("Waiting for all players to ready up...");
                            try {
                                Thread.sleep(2000);
                            } catch (Exception e) {
                                System.out.println("Error: " + e);
                            }
                        }
                        // Game started
                        System.out.println("Game started");
                        // List of players in game
                        System.out.println(fromServer.readLine());
                        do {
                            System.out.println("Enter a number between 1 and 100: ");
                            userInput = fromUser.readLine();
                            toServer.println(userInput);
                            // Round outcome
                            String roundOutcome = fromServer.readLine();
                            System.out.println(roundOutcome);
                            // Check if game ended
                            String isFinished = fromServer.readLine();
                            if (isFinished.equals("end")) {
                                break;
                            }
                        } while (true);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
                if (toServer != null) {
                    toServer.close();
                }
                if (fromServer != null) {
                    fromServer.close();
                }
                if (fromUser != null) {
                    fromUser.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}