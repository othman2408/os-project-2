import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game implements Serializable {
    private String name;
    private List<Player> players;
    private boolean locked;
    private int roundNumber;
    private Map<Player, Integer> points; // Store points for each player
    private List<Integer> selectedNumbers;

    // Constructor
    public Game(String name) {
        this.name = name;
        this.players = new ArrayList<>();
        this.selectedNumbers = new ArrayList<>();
        this.locked = false;
        this.roundNumber = 1;
        this.points = new HashMap<>();
    }

    // Method to get the current round number
    public int getRoundNumber() {
        return roundNumber;
    }

    // Method to get the game name
    public String getName() {
        return name;
    }

    // Method to set the game name
    public void setName(String name) {
        this.name = name;
    }

    // Method to get the selected numbers for the round
    public List<Integer> getSelectedNumbers() {
        return selectedNumbers;
    }

    // Method to set the selected numbers for the round
    public void setSelectedNumbers(List<Integer> selectedNumbers) {
        this.selectedNumbers = selectedNumbers;
    }

    // Method to set the current round number
    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    // Method to get the list of players in the game
    public List<Player> getPlayers() {
        return players;
    }

    // Method to get the points
    public Map<Player, Integer> getPoints() {
        return points;
    }

    // Method to set the points
    public void setPoints(Map<Player, Integer> points) {
        this.points = points;
    }

    // Method to clear selectedNumbers
    public void clearSelectedNumbers() {
        selectedNumbers.clear();
    }

    // Method to add a player to the game
    public void addPlayer(Player player) {
        if (players.size() < 6) { // Check if maximum players limit is reached
            players.add(player);
            points.put(player, 5); // Initialize points for the player
            notifyPlayers(); // Notify players about new player
        } else {
            System.out.println("Maximum players limit reached. Cannot add more players.");
        }
    }

    // Method to remove a player from the game
    public void removePlayer(Player player) {
        players.remove(player);
        points.remove(player);
        notifyPlayers(); // Notify players about player removal
    }

    // Method to lock the game
    public void lockGame() {
        locked = true;
    }

    // Method to start the game
    public void startGame() {
        // Check if minimum players requirement is met, then lock the game and start
        // playing rounds
        if (players.size() >= 2) {
            lockGame();
            playRounds();
        } else {
            System.out.println("2 or more players required to start the game.");
        }
    }

    // Method to play rounds until game ends
    public void playRounds() {
        // keep playing rounds until only one player remains
        while (players.size() > 1) {
            roundNumber++;
            List<Integer> selectedNumbers = new ArrayList<>();
            for (Player player : players) {
                int selectedNumber = player.selectNumber();
                selectedNumbers.add(selectedNumber);
            }
            double average = calculateAverage(selectedNumbers);
            // Determine round winners, deduct points from losers, and eliminate players
            // with no points
            List<Player> winners = determineRoundWinners(selectedNumbers, average);
            deductPointsFromLosers(winners);
            eliminatePlayersWithNoPoints();
            notifyRoundOutcome(selectedNumbers, winners);

            // Check for game end condition
            if (players.size() == 1) {
                endGame(); // End the game if only one player remains
                return;
            }
        }
    }

    public void endGame() {
        System.out.println("Game " + name + " has ended.");
        System.out.println("Winner: " + players.get(0).getName());
    }

    // Method to calculate the average of selected numbers
    public double calculateAverage(List<Integer> numbers) {
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        return (double) sum / numbers.size();
    }

    // Method to determine round winners
    public List<Player> determineRoundWinners(List<Integer> selectedNumbers, double average) {
        List<Player> winners = new ArrayList<>();
        double twoThirdsAverage = (2.0 / 3.0) * average;
        double minDifference = Double.MAX_VALUE;
        for (int i = 0; i < selectedNumbers.size(); i++) {
            double difference = Math.abs(selectedNumbers.get(i) - twoThirdsAverage);
            if (difference <= minDifference) {
                if (difference < minDifference) {
                    winners.clear();
                    minDifference = difference;
                }
                winners.add(players.get(i));
            }
        }
        return winners;
    }

    // Method to deduct points from losing players
    public void deductPointsFromLosers(List<Player> winners) {
        for (Player player : players) {
            if (!winners.contains(player)) {
                int playerPoints = player.getPoints();
                if (playerPoints > 0) {
                    player.setPoints(playerPoints - 1);

                }
            }
        }
    }

    // Method to eliminate players with no points left
    public void eliminatePlayersWithNoPoints() {
        List<Player> playersToRemove = new ArrayList<>();
        for (Player player : players) {
            if (player.getPoints() == 0) {
                playersToRemove.add(player);
            }
        }
        for (Player player : playersToRemove) {
            removePlayer(player);
        }
    }

    // This method to notify players about round outcome
    public void notifyRoundOutcome(List<Integer> selectedNumbers, List<Player> winners) {
        System.out.println("Round " + roundNumber + " Outcome:");
        System.out.println("Selected Numbers: " + selectedNumbers);
        for (Player player : players) {
            String outcome;
            if (winners.contains(player)) {
                outcome = "Winner";
            } else {
                outcome = "Loser";
            }
            System.out.println(
                    "Player: " + player.getName() + ", Outcome: " + outcome + ", Points: " + points.get(player));
        }
    }

    // Method to notify players about game status
    public void notifyPlayers() {
        // Get the list of players in the game
        String playersList = getGamePlayers();

        System.out.println("Notify: " + playersList);
    }

    // Method to handle a player confirming readiness to start the game
    public void playerReady(Player player) {
        if (players.contains(player)) {
            player.setReady(true);
            boolean allPlayersReady = true;
            for (Player p : players) {
                if (!p.isReady()) {
                    allPlayersReady = false;
                    break;
                }
            }
            if (allPlayersReady) {
                startGame(); // Start the game if all players are ready
            }
        }
    }

    // Method to handle a player selecting a number for the round
    public void playerSelectNumber(Player player, int number) {
        if (players.contains(player)) {
            // Assuming Player class has a setNumberSelection method
            player.setNumberSelection(number);
        }
    }

    // Method to get the points for a player
    public void makeGuess(Game game, Player player, int guess) {
        if (game.hasPlayerGuessed(player)) {
            System.out.println("Player has already guessed for this round.");
        } else {
            player.setNumberSelection(guess);
            System.out.println("Player " + player.getName() + " has guessed " + guess);
            if (game.allPlayersReady()) {
                game.lockGame();
            }
        }
    }

    // Method to check if all players are ready
    public boolean allPlayersReady() {
        for (Player player : players) {
            if (!player.isReady()) {
                return false;
            }
        }
        return true;
    }

    // Method to check if the game is locked
    public boolean isLocked() {
        return locked;
    }

    // Method to check if a player has guessed for the round
    public boolean hasPlayerGuessed(Player player) {
        return player.getNumberSelection() != -1;
    }

    // Method to get the list of players in the game
    public String getGamePlayers() {
        StringBuilder playersList = new StringBuilder();
        for (Player player : players) {
            playersList.append(player.getName()).append(", ");
        }
        return playersList.toString();
    }

}