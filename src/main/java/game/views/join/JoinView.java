package game.views.join;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import game.views.MainLayout;

@PageTitle("Join")
@Route(value = "join", layout = MainLayout.class)
public class JoinView extends HorizontalLayout {



    public JoinView() {
            // Center the content
            setAlignItems(Alignment.CENTER);
            addClassName("join-view");

            // Main container for the join view
            Div joinContainer = new Div();
            joinContainer.addClassName("join-container");

            // Header Icon for the join container
            Image headerIcon = new Image("https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Smilies/Neutral%20Face.png", "Join Icon");
            headerIcon.addClassName("header-icon");
            joinContainer.add(headerIcon);

            // Add a header to the join container
            H2 joinHeader = new H2("Join a Game");
            joinHeader.addClassName("join-header");
            joinContainer.add(joinHeader);

            // Create a text field for the game code
            TextField gameCodeField = new TextField();
            gameCodeField.setPlaceholder("Enter Game Name");
            gameCodeField.addClassName("game-name-field");
            joinContainer.add(gameCodeField);

            // Buttons container
            HorizontalLayout buttonsContainer = new HorizontalLayout();
            buttonsContainer.addClassName("buttons-container");

            // Create a buttons to join or create a game
            Button joinButton = new Button("Join Game");
            joinButton.addClassName("join-button");
            // add icon to the button
            Icon arrowRightIcon = new Icon("lumo", "arrow-right");
            arrowRightIcon.addClassName("arrow-icon");
            joinButton.setIcon(arrowRightIcon);
            joinButton.setIconAfterText(true);
            joinButton.addClickShortcut(Key.ENTER);
            joinButton.addClickListener(e -> {
                Notification.show("Joining game with code: " + gameCodeField.getValue());
            });

            Button createButton = new Button("Create Game");
            createButton.addClassName("create-button");
            // add icon to the button
            Icon plusIcon = new Icon("lumo", "plus");
            plusIcon.addClassName("plus-icon");
            createButton.setIcon(plusIcon);
            createButton.setIconAfterText(true);

            createButton.addClickListener(e -> {
                Notification.show("Creating game with name: " + gameCodeField.getValue());
            });

            // Add the buttons to the buttons container
            buttonsContainer.add(joinButton, createButton);

            // Add the buttons container to the join container
            joinContainer.add(buttonsContainer);


            // Add the join container to the view
            add(joinContainer);


    }

}
