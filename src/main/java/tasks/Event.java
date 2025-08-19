package tasks;

public class Event extends Task {
    private String startDateTime;
    private String endDateTime;

    public Event(String description, String startDateTime, String endDateTime) {
        super(description);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public String getStartDateTime() {
        return this.startDateTime;
    }

    public String getEndDateTime() {
        return this.endDateTime;
    }

    @Override
    public String getAsListItem() {
        return String.format("[%s] %s (from: %s, to: %s)",
                this.getStatusIcon(),
                this.getDescription(),
                this.getStartDateTime(),
                this.getEndDateTime());
    }
}
