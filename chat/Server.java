import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT =     ;
    private static List<ClientHandler> clients = new ArrayList<>();


    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMessage(String message, String senderName) {
        String messageWithSender = "[" + senderName + "]: " + message;
        clients.forEach(client -> client.sendMessage(messageWithSender));
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;
    private String clientName;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            clientName = input.readLine();
            System.out.println("Client name: " + clientName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = input.readLine()) != null) {
                System.out.println("[" + clientName + "] : " + message);
                Server.broadcastMessage(message, clientName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
                output.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        output.println(message);
    }
}
