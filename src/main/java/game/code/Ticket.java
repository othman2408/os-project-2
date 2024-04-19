package game.code;

public class Ticket {

    private static int idCounter = 0;
    private final int id;
    private final String nickname;

    public Ticket(String nickname) {
        this.id = ++idCounter;
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    // Method for ticket generation
    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                '}';
    }

    public static void main(String[] args) {
        Ticket ticket1 = new Ticket("Alice");
        Ticket ticket2 = new Ticket("Bob");
        System.out.println(ticket1);
        System.out.println(ticket2);
    }
    
}
