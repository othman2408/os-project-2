package game.code;

import java.io.PrintWriter;
import java.net.Socket;

public class GameCleanUp extends Thread {
    private Socket playerSocket;
    private PrintWriter strOutput;

    public GameCleanUp(Socket playerSocket) {
        this.playerSocket = playerSocket;
        try {
            this.strOutput = new PrintWriter(playerSocket.getOutputStream(), true);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void run() {
        sendMessage("end");
    }
    // This method sends a message to the player
    public void sendMessage(String message) {
        try {
            // Send the message to the player
            strOutput = new PrintWriter(playerSocket.getOutputStream(), true);
            System.out.println("Sending message to " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            strOutput.println(message);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
    
}
