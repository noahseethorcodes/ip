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
    public TaskType getTaskType() {
        return TaskType.EVENT;
    }

    @Override
    public String getAsListItem() {
        return String.format("[%s] [%s] %s (from: %s, to: %s)",
                this.getTaskTypeIcon(),
                this.getStatusIcon(),
                this.getDescription(),
                this.getStartDateTime(),
                this.getEndDateTime());
    }

    @Override
    public String getTaskTypeIcon() {
        return "E";
    }

    public static Event fromStorageLine(String storageLine) {
        // Parse Storage Line
        String[] parts = storageLine.split(" \\| ");

        boolean taskIsDone;
        try {
            taskIsDone = Integer.parseInt(parts[1]) == 1;
        } catch (NumberFormatException e) {
            System.out.println("INVALID STORAGE FORMAT");
            return null;
        }
        
        String description = parts[2];
        String startDateTime = parts[3];
        String endDateTime = parts[4];

        // Create Event Object
        Event event = new Event(description, startDateTime, endDateTime);
        if (taskIsDone) {
            event.markAsDone();
        }

        return event;
    }

    @Override
    public String toStorageLine() {
        return String.format("%s | %d | %s | %s | %s",
                this.getTaskTypeIcon(),
                this.isDone ? 1 : 0, 
                this.getDescription(),
                this.getStartDateTime(),
                this.getEndDateTime());
    }
}
