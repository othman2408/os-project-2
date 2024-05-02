package game.code;

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
 *
 * 
 */

public class ClientHandler extends Thread {
    private final Server server;
    private final Socket playerSocket;
    private String nickname;
    private String ticket;

    public ClientHandler(Socket playerSocket, Server server) {
        this.playerSocket = playerSocket;
        this.server = server;
    }

    public void run() {
        try {
            System.out.println(
                    "Connection established with " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            server.sendMessage("Welcome to the famous 2/3 Game!", playerSocket);
            do {
                server.sendMessage(server.getMenu(), playerSocket);
                server.sendMessage("Enter your choice: ", playerSocket);
                String choice = server.readMessage(playerSocket);
                System.out.println("Client choice: " + choice);
                switch (choice) {
                    case "1":
                        ticket = server.handleGetTicket(playerSocket);
                        break;
                    case "2":
                        server.getAvailableGames(playerSocket);
                        break;
                    case "3":
                        server.joinGame(playerSocket, ticket);
                        
                        break;
                    case "4":
                        //handleGetPlayerList();
                        break;
                    case "5":
                        //handleGetTicketList();
                        break;
                    case "6":
                        //handleGetGamePlayerList();
                        break;
                    case "7":
                    server.sendMessage("Goodbye!", playerSocket);
                        return;
                    default:
                    server.sendMessage("Invalid choice. Please try again.", playerSocket);
                }
            } while (true);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        finally {
            try {
//                 playerSocket.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }
}