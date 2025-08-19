package tasks;

public class Todo extends Task{
    public Todo(String description) {
        super(description);
    }
    
    @Override
    public String getAsListItem() {
        return String.format("[%s] %s", this.getStatusIcon(), this.getDescription());
    }
}
