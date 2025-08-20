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
        return String.format("[%s] %s (by: %s)",
                this.getStatusIcon(),
                this.getDescription(),
                this.getDeadline());
    }
}
