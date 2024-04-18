package game.views.home;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import game.code.Player;
import game.views.LeaderboardView;
import game.views.MainLayout;

@PageTitle("Home")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class HomeView extends HorizontalLayout {


    public HomeView(){
        // MAIN LAYOUT
        addClassName("home-view");

        // hero div to display title and start game button
        Div hero = new Div();
        hero.addClassName("hero");

        H1 title = new H1("Welcome to the Guess 2/3 Game!");
        Button startGame = new Button("Start Game");
        startGame.addClassName("start-game-btn");
        startGame.setThemeName("primary");

        startGame.addClickListener(e -> {
            Notification.show("Game started!");
            // Go to the join game view
            getUI().ifPresent(ui -> ui.navigate("join"));


        });

        hero.add(title, startGame);


        Div leaderboardContainer = new Div();
        leaderboardContainer.addClassName("leaderboard-container");

        H4 leaderboardTitle = new H4("Leaderboard");
        leaderboardTitle.addClassName("leaderboard-title");

        // Create a Grid with Player objects
        LeaderboardView leaderboardView = new LeaderboardView();
        leaderboardView.addClassName("leaderboard-view");
        leaderboardContainer.add(leaderboardTitle, leaderboardView);


        add(hero, leaderboardContainer);


    }





}
