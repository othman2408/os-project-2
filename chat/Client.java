import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 42900;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT)) {
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            System.out.print("Enter your name: ");
            String name = userInput.readLine();
            output.println(name);

            System.out.println("Connected to server. Type your message:");

            Thread receiveThread = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = input.readLine()) != null) {
                        if (serverResponse.startsWith("[" + name + "]")) {
                            // Skip printing the message if it's from the same client
                            continue;
                        }
                        System.out.println(serverResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();

            String message;
            while ((message = userInput.readLine()) != null) {
                output.println(message);
                if (message.equals("exit")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
