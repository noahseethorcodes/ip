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

    public String getStartDateTime() {
        return this.startDateTime.format(DISPLAY_FORMAT);
    }

    public String getEndDateTime() {
        return this.endDateTime.format(DISPLAY_FORMAT);
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
        LocalDateTime startDateTime = LocalDateTime.parse(parts[3]);
        LocalDateTime endDateTime = LocalDateTime.parse(parts[4]);

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
                this.startDateTime.toString(),
                this.endDateTime.toString());
    }
}
