public class Booking {
    private int id;
    private int movieId;
    private String seatNumber;
    private String user;

    public Booking(int id, int movieId, String seatNumber, String user) {
        this.id = id;
        this.movieId = movieId;
        this.seatNumber = seatNumber;
        this.user = user;
    }

    public int getId() { return id; }
    public int getMovieId() { return movieId; }
    public String getSeatNumber() { return seatNumber; }
    public String getUser() { return user; }
}
