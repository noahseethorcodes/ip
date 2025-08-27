package commands;

import java.io.IOException;

import errors.LogosException;
import tasklist.TaskList;
import tasks.Task;
import ui.Ui;

public class DeleteCommand implements Command {
    private final int taskIndex;

    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList taskList, Ui ui) throws LogosException, IOException {
        Task selectedTask = taskList.deleteTask(taskIndex);
        ui.respond("Todo removed: \"" + selectedTask.getDescription() + "\"",
                String.format("Now you have %d tasks in the list~", taskList.size()),
                "Use the command 'list' to view your current task list");
    }
}
