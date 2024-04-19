package game.views.games;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
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
        // Here a list of games will be shown as a grid of cards

        Div gamesContainer = new Div();
        gamesContainer.addClassName("games-container");

        // Create a game card for each game
        Game game1 = new Game("Game 1");
        Div gameCard1 = createGameCard();
        gamesContainer.add(gameCard1);

        add(gamesContainer);

    }

    public Div createGameCard() {
        Div gameCard = new Div();

        // Here we will create a card for the game
        gameCard.addClassName("game-card");

        H3 gameTitle = new H3("Game Name");
        gameCard.add(gameTitle);

        Button joinButton = new Button("Join Game");
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
