package tasks;

public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.TODO;
    }

    @Override
    public String getAsListItem() {
        return String.format("[%s] %s", this.getStatusIcon(), this.getDescription());
    }
}
