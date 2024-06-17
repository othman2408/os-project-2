import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {

        // Check if the user provided the port number
        if (args.length != 2) {
            System.out.println("Usage: java Client <IP Address> <Provide Port Here>");
            return;
        }

        final String SERVER_ADDRESS = args[0];
        //final String SERVER_ADDRESS = "localhost";
        final int PORT = Integer.parseInt(args[1]);
        //final int PORT = 19400;
        Socket client = null;
        PrintWriter toServer = null;
        BufferedReader fromServer = null;
        BufferedReader fromUser = null;

        try {
            client = new Socket(SERVER_ADDRESS, PORT);
            toServer = new PrintWriter(client.getOutputStream(), true);
            fromServer = new BufferedReader(new InputStreamReader(client.getInputStream()));
            fromUser = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Connected to server " + SERVER_ADDRESS + ":" + PORT);

            // Thread for reading and printing messages from the server
            final BufferedReader finalFromServer = fromServer;
            Thread serverThread = new Thread(() -> {
                try {
                    String serverInput;
                    while ((serverInput = finalFromServer.readLine()) != null) {
                        System.out.println(serverInput);
                        if (serverInput.equals("Goodbye!")) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            serverThread.start();

            // Read user input from the console and send it to the server
            String userInput;
            while ((userInput = fromUser.readLine()) != null) {
                toServer.println(userInput);
                if (userInput.equals("exit")) {
                    break;
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
