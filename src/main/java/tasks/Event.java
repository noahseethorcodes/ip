package tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event extends Task {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    public Event(String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(description);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public String getStartDateTimeString() {
        return this.startDateTime.format(DISPLAY_FORMAT);
    }

    public String getEndDateTimeString() {
        return this.endDateTime.format(DISPLAY_FORMAT);
    }

    public LocalDateTime getStartDateTime() {
        return this.startDateTime;
    }

    public LocalDateTime getEndDateTime() {
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
                this.getStartDateTimeString(),
                this.getEndDateTimeString());
    }

    @Override
    public String getTaskTypeIcon() {
        return "E";
    }

    public static Event fromStorageLine(String storageLine) {
        // Parse Storage Line
        String[] parts = storageLine.split(" \\| ");

        boolean isMarkedDone;
        try {
            isMarkedDone = Integer.parseInt(parts[1]) == 1;
        } catch (NumberFormatException e) {
            System.out.println("INVALID STORAGE FORMAT");
            return null;
        }
        
        String description = parts[2];
        LocalDateTime startDateTime = LocalDateTime.parse(parts[3]);
        LocalDateTime endDateTime = LocalDateTime.parse(parts[4]);

        // Create Event Object
        Event event = new Event(description, startDateTime, endDateTime);
        if (isMarkedDone) {
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
                this.startDateTime.toString(),
                this.endDateTime.toString());
    }
}
