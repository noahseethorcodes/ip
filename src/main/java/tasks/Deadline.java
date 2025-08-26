package tasks;

public class Deadline extends Task {
    private String deadline;

    public Deadline(String description, String deadline) {
        super(description);
        this.deadline = deadline;
    }

    public String getDeadline() {
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
                this.getDeadline());
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
        String by = parts[3];

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
                this.getDeadline());
    }
}
