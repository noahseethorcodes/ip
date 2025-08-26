package tasks;

public abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public void markAsDone() {
        this.isDone = true;
    }

    public void markAsNotDone() {
        this.isDone = false;
    }

    public boolean isDone() {
        return this.isDone;
    }

    public String getStatusIcon() {
        return (this.isDone ? "X" : " "); // Completed Tasks are marked with an 'X'
    }

    public String getDescription() {
        return this.description;
    }

    public abstract TaskType getTaskType();

    public abstract String getAsListItem();

    public abstract String getTaskTypeIcon();
}
