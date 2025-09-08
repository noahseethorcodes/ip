package tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Deadline extends Task {
    private LocalDateTime deadline;
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    public Deadline(String description, LocalDateTime deadline) {
        super(description);
        this.deadline = deadline;
    }

    public String getDeadlineString() {
        return this.deadline.format(DISPLAY_FORMAT);
    }

    public LocalDateTime getDeadline() {
        return this.deadline;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    @Override
    public String getAsListItem() {
        return String.format("[%s] [%s] %s (by: %s)",
                this.getTaskTypeIcon(),
                this.getStatusIcon(),
                this.getDescription(),
                this.getDeadlineString());
    }

    @Override
    public String getTaskTypeIcon() {
        return "D";
    }

    public static Deadline fromStorageLine(String storageLine) {
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
        LocalDateTime by = LocalDateTime.parse(parts[3]);

        // Create Deadline Object
        Deadline deadline = new Deadline(description, by);
        if (taskIsDone) {
            deadline.markAsDone();
        }

        return deadline;
    }

    @Override
    public String toStorageLine() {
        return String.format("%s | %d | %s | %s",
                this.getTaskTypeIcon(),
                this.isDone ? 1 : 0, 
                this.getDescription(),
                this.deadline.toString());
    }
}
