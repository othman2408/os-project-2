package game.views.games;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import game.views.MainLayout;

import java.util.Arrays;

@PageTitle("Games")
@Route(value = "games", layout = MainLayout.class)
public class GamesView extends VerticalLayout {

    public GamesView() {
        // Center the content
        setAlignItems(Alignment.CENTER);

        // Create a container for the games and add a header
        Div gamesContainer = new Div();
        gamesContainer.addClassName("games-container");

        // Add a header to the games container
        H1 gamesHeader = new H1("Games List");
        gamesHeader.addClassName("games-header");
        gamesContainer.add(gamesHeader);

        // Create a game card for each game
        Div gameCardsContainer = new Div();
        gameCardsContainer.addClassName("game-cards-container");
        gamesContainer.add(gameCardsContainer);

        for (int i = 1; i <= 5; i++) {
            Div gameCard = createGameCard("Game " + i);
            gameCardsContainer.add(gameCard);
        }

        add(gamesContainer);
    }

    public Div createGameCard(String gameName) {
        Div gameCard = new Div();

        // Here we will create a card for the game
        gameCard.addClassName("game-card");

        H1 gameTitle = new H1(gameName);
        gameCard.add(gameTitle);

        Button joinButton = new Button("Join");
        joinButton.addClickListener(e -> {
            Notification.show("Joined the game " + gameName);
            UI.getCurrent().navigate("games/" + gameName.replaceAll(" ", "%20"));
        });
        gameCard.add(joinButton);

        return gameCard;
    }

    @Route(value = "games/:gameName", layout = MainLayout.class)
    public static class GameView extends VerticalLayout {

        public GameView() {
            // Get the game ID from the URL
            String gameName = this.getGameName();
            Notification.show("Game Name: " + gameName);

            // Center the content
            setAlignItems(Alignment.CENTER);

            // Create a container for the game and add a header
            Div gameContainer = new Div();

            // Add a header to the game container
            H1 gameHeader = new H1("Game " + gameName);

            // Add the game header to the game container
            gameContainer.add(gameHeader);

            // Add the game container to the view
            add(gameContainer);

            // Add a button to start the game
            Button startGameButton = new Button("Start Game");
            startGameButton.addClickListener(e -> {
                // Redirect to the game view
                Notification.show("Game started!");
            });

            // Add the start game button to the view
            add(startGameButton);

            // Add a button to go back to the games list
            Button backButton = new Button("Back to Games List");
            backButton.addClickListener(e -> {
                // Redirect to the games view
                getUI().ifPresent(ui -> ui.navigate("games"));
            });

            // Add the back button to the view
            add(backButton);
        }

        private String getGameName() {
            String pathInfo = VaadinService.getCurrentRequest().getPathInfo();
            String[] parts = pathInfo.split("/");
            return Arrays.stream(parts).reduce((first, second) -> second).orElse("");

        }
    }
}
