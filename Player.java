public class Player {

    private String name;
    private int score;

    public Player(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        this.score++;
    }

    public void decrementScore() {
        this.score--;
    }

    public static void main(String[] args) {

        Player player = new Player("othman", 5);
        System.out.println(player.getName());
    }

}
