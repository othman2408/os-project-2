package game.views.join;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
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
        // MAIN LAYOUT
        addClassName("join-view");

        Div mainContainer = new Div();
        mainContainer.addClassName("main-container");

        // Create a title for the view
        H2 title = new H2("Join Game");
        title.addClassName("join-title");


        // Create a text field for the player's name
        TextField nameField = new TextField("Name");
        nameField.setPlaceholder("Enter your name");
        nameField.setClassName("name-field");
        nameField.setClearButtonVisible(true);
        nameField.focus();

        // Create a button to join the game
        Div buttonContainer = new Div();
        buttonContainer.addClassName("button-container");
        Button joinButton = new Button("Join");
        joinButton.setThemeName("primary");
        joinButton.setClassName("join-btn");
        joinButton.addClickShortcut(Key.ENTER);
        joinButton.addClickListener(e -> {
            Notification.show("Game joined!");
            // Go to the join game view
            Notification.show("Game joined!");
        });
        Button createGameButton = new Button("Create Game");
        createGameButton.setThemeName("primary");
        createGameButton.setClassName("create-btn");
        createGameButton.addClickShortcut(Key.ENTER);
        createGameButton.addClickListener(e -> {
            Notification.show("Game created!");
            // Go to the join game view
            Notification.show("Game created!");
        });

        buttonContainer.add(joinButton, createGameButton);


        mainContainer.add(title, nameField, buttonContainer);
        // Add the components to the layout
        add(mainContainer);
    }

}
