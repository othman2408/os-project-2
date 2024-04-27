package game.code;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class GameHandler extends Thread {
    private final Socket playerSocket;
    private final Game game;
    private final Server server;

    public GameHandler(Socket playerSocket, Game game, Server server) {
        this.playerSocket = playerSocket;
        this.game = game;
        this.server = server;

    }

    public void run() {
            int selectedNumber = Integer.parseInt(server.readMessage(playerSocket));
            game.getSelectedNumbers().add(selectedNumber);
                
    }
}
