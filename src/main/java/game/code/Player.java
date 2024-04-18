package game.code;

import game.code.Game;

import java.util.Scanner;

public class Player {
    private String name;
    private String ticket;
    private int points;
    private boolean ready;
    private Game currentGame;
    private int numberSelection;

    public Player(String name, String ticket) {
        this.name = name;
        this.ticket = ticket;
        this.points = 5; // start with 5 points for each player
        this.ready = false;
    }

    // Method to get player's name
    public String getName() {
        return name;
    }

    // Method to get player's points
    public int getPoints() {
        return points;
    }

    // Method to set player's points
    public void setPoints(int points) {
        this.points = points;
    }

    // Method to check if player is ready
    public boolean isReady() {
        return ready;
    }

    // Method to set player's readiness
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    // Method to get player's selected number for the round
    public int getNumberSelection() {
        return numberSelection;
    }

    // Method to set player's selected number for the round
    public void setNumberSelection(int numberSelection) {
        this.numberSelection = numberSelection;
    }

    // Method for player to select a number for the round
    public int selectNumber() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select a number between 1 and 100:");
        int number = scanner.nextInt();
        while (number < 1 || number > 100) {
            System.out.println("Invalid number. Please select a number between 1 and 100:");
            number = scanner.nextInt();
        }
        scanner.close();
        return number;
    }

    // Method to get the current game the player is in
    public Game getCurrentGame() {
        return currentGame;
    }

    // Method to set the current game the player is in
    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }

    public String getTicket() {
        return ticket;
    }

}
