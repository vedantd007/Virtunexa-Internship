public class Movie {
    private int id;
    private String name;
    private int totalSeats;

    public Movie(int id, String name, int totalSeats) {
        this.id = id;
        this.name = name;
        this.totalSeats = totalSeats;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getTotalSeats() { return totalSeats; }
}
