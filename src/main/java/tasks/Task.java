package tasks;

import errors.LogosException;

public abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public static Task fromStorageLine(String storageLine) throws LogosException {
        // Check the first character of the line to get the task type
        char taskType = storageLine.charAt(0);

        return switch (taskType) {
            case 'T' -> Todo.fromStorageLine(storageLine);
            case 'D' -> Deadline.fromStorageLine(storageLine);
            case 'E' -> Event.fromStorageLine(storageLine);
            default  -> throw new LogosException("Unknown task type: " + taskType);
        };
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

    public abstract String toStorageLine();
}
