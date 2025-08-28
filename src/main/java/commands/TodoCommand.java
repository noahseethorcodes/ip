package commands;

import java.io.IOException;

import errors.LogosException;
import tasklist.TaskList;
import tasks.Todo;
import ui.Ui;

public class TodoCommand implements Command {
    private final String taskName;

    public TodoCommand(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public void execute(TaskList taskList, Ui ui) throws LogosException, IOException {
        Todo newTodo = taskList.addTodo(taskName);
        ui.respond("Todo added: \"" + newTodo.getDescription() + "\"",
                String.format("Now you have %d tasks in the list~", taskList.size()),
                "Use the command 'list' to view your current task list");
    }
}
