import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class GameHandler extends Thread {
    private Socket playerSocket;
    private Game game;
    private BufferedReader strInput;

    public GameHandler(Socket playerSocket, Game game) {
        this.playerSocket = playerSocket;
        this.game = game;
        try {
            this.strInput = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void run() {
            int selectedNumber = Integer.parseInt(readMessage(playerSocket));
            game.getSelectedNumbers().add(selectedNumber);
            System.out.println("Selected Numbers: " + game.getSelectedNumbers().toString());    
                
    }

    // This method reads a message from the player
    public String readMessage(Socket player) {
        try {
            // Read the message from the player
            System.out.println("Reading message from " + playerSocket.getInetAddress() + ":" + playerSocket.getPort());
            String message = strInput.readLine();
            return message;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }    
}
