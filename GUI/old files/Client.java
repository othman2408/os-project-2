import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    // Server address & port
    private static final String SERVER_ADDRESS = "guess.queue.qa";
    private static final int PORT = 13337;

    public static void main(String[] args) {
        try {

            // Connect to the server
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Connected to the server.");

            // Start a new thread to listen for server
            ClientListener listener = new ClientListener(input);
            Thread listenerThread = new Thread(listener);

            // Start the listener thread
            listenerThread.start();

            // Keep listing for user input and send it to the server
            String userInputLine;
            while ((userInputLine = userInput.readLine()) != null) {
                output.println(userInputLine);
            }

        } catch (IOException ignored) {
        }
    }

    // Thread to listen for server messages
    public static class ClientListener implements Runnable {
        private final BufferedReader in;

        public ClientListener(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                String serverResponse;
                while ((serverResponse = in.readLine()) != null) {
                    System.out.println("Server response: " + serverResponse);
                }
            } catch (IOException ignored) {
            }
        }
    }
}
