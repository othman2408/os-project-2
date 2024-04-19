package game.views.home;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
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

import javax.lang.model.element.Element;

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

        H1 title = new H1("Welcome to, 2/3 Guess Game!");
        title.addClassName("home-title");
        Html startGame = new Html("<button class=\"ui-btn\"> <span> Let's Start </span> </button>");
        startGame.addClassName("start-game-btn");

        Component startGameButton = startGame;
        startGameButton.getElement().addEventListener("click", e -> {
            // Navigate to the game view
            getUI().ifPresent(ui -> ui.navigate("join"));
        });


        // Add title and start game button to hero div
        hero.add(title, startGame);

        // leaderboard div to display leaderboard
        Div leaderboardContainer = new Div();
        leaderboardContainer.addClassName("leaderboard-container");

        // Add a header to the leaderboard container
        H4 leaderboardTitle = new H4("Leaderboard");
        leaderboardTitle.addClassName("leaderboard-title");

        // Create a Grid with Player objects
        LeaderboardView leaderboardView = new LeaderboardView();
        leaderboardView.addClassName("leaderboard-view");
        leaderboardContainer.add(leaderboardTitle, leaderboardView);


        // Add hero and leaderboard to main layout
        add(hero, leaderboardContainer);
    }





}
