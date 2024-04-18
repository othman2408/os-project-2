package game.views.home;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import game.code.Player;
import game.views.MainLayout;

@PageTitle("Home")
@Route(value = "home", layout = MainLayout.class)
public class HomeView extends HorizontalLayout {

    public HomeView(){
        // add class to the main layout
        addClassName("home-view");
        H1 title = new H1("Welcome to the Guess 2/3 Game!");
        Button startGame = new Button("Start Game");
        startGame.setThemeName("primary");

        startGame.addClickListener(e -> {
            Notification.show("Game started!");

        });

        // leaderboard div to display players
        Div leaderboard = new Div();
        H2 leaderboardTitle = new H2("Leaderboard");
        Grid<Player> playerGrid = new Grid<>(Player.class, true);
        playerGrid.addClassName("player-grid");
        playerGrid.setColumns( "name", "points", "ticket");


        // add players to the grid
        Player player1 = new Player("Player 1", "1234");
        Player player2 = new Player("Player 2", "5678");
        Player player3 = new Player("Player 3", "9101");
        playerGrid.setItems(player1, player2, player3);

        playerGrid.setPageSize(3);

        leaderboard.add(leaderboardTitle, playerGrid);

        add(title, startGame, leaderboard);


    }


}
