package game.views;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import game.code.Player;

import java.util.Arrays;
import java.util.List;

public class LeaderboardView extends VerticalLayout {

    private List<Player> players;



    public LeaderboardView() {
        // Remove the default margin and padding
        setPadding(false);
        setMargin(false);
        setHeight("15rem");


        // Create a Grid with Player objects
        Grid<Player> leaderboardGrid = new Grid<>(Player.class);
        leaderboardGrid.setColumns("name", "points");

        // Sample data
        List<Player> players;
        Player p1 = new Player("John", "1234");
        p1.setPoints(10);

        Player p2 = new Player("Jane", "5678");
        p2.setPoints(8);

        Player p3 = new Player("Doe", "9101");
        p3.setPoints(6);

        Player p4 = new Player("Smith", "1121");
        p4.setPoints(4);

        Player p5 = new Player("Doe", "3141");
        p5.setPoints(2);

        players = Arrays.asList(p1, p2, p3, p4, p5);

        leaderboardGrid.setItems(players);


        // Add data to the Grid
        leaderboardGrid.setItems(players);

        // Add the Grid to the layout
        add(leaderboardGrid);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void updatePlayer(Player player) {
        for (Player p : players) {
            if (p.getName().equals(player.getName())) {
                p.setPoints(player.getPoints());
            }
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void clearPlayers() {
        players.clear();
    }

    public void sortPlayers() {
        players.sort((p1, p2) -> p2.getPoints() - p1.getPoints());
    }

    public void updateLeaderboard() {
        sortPlayers();
        setPlayers(players);
    }

    public void resetLeaderboard() {
        clearPlayers();
    }

}
