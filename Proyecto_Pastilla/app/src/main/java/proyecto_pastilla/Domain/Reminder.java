package proyecto_pastilla.Domain;

public class Reminder {
    private final String title;
    private final String description;
    private final String date;
    private final String time;
    private boolean isTaken;

    public Reminder(String title, String description, String date, String time, boolean isTaken) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.isTaken = isTaken;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }
}
