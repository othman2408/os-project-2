package game.views.games;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import game.code.Game;
import game.views.MainLayout;
import com.vaadin.flow.component.button.Button;

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


        Div gameCard1 = createGameCard();
        Div gameCard2 = createGameCard();
        Div gameCard3 = createGameCard();
        Div gameCard4 = createGameCard();
        Div gameCard5 = createGameCard();

        gameCardsContainer.add(gameCard1, gameCard2, gameCard3, gameCard4, gameCard5);

        add(gamesContainer);

    }

    public Div createGameCard() {
        Div gameCard = new Div();

        // Here we will create a card for the game
        gameCard.addClassName("game-card");

        H3 gameTitle = new H3("Game Name");
        gameCard.add(gameTitle);

        Html joinButton = new Html("<button class=\"button\">Join</button>");
        gameCard.add(joinButton);

        // // Set the game name as the card title
        // Html gameTitle = new Html("<h3>" + "Game Name" + "</h3>");  
        // gameCard.add(gameTitle);

        // // Add a button to join the game
        // Html joinButton = new Html("<button>Join Game</button>");
        // gameCard.add(joinButton);
        

        return gameCard;
    }

}
