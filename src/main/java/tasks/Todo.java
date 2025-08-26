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
        return String.format("[%s] [%s] %s",
                this.getTaskTypeIcon(),
                this.getStatusIcon(), 
                this.getDescription());
    }

    @Override
    public String getTaskTypeIcon() {
        return "T";
    }

    public static Todo fromStorageLine(String storageLine) {
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

        // Create Todo Object
        Todo todo = new Todo(description);
        if (taskIsDone) {
            todo.markAsDone();
        }

        return todo;
    }

    @Override
    public String toStorageLine() {
        return String.format("%s | %d | %s",
                this.getTaskTypeIcon(),
                this.isDone ? 1 : 0, 
                this.getDescription());
    }
}
