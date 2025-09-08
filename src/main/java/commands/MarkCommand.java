package commands;

import java.io.IOException;

import errors.LogosException;
import tasklist.TaskList;
import tasks.Task;
import ui.Ui;

public class MarkCommand implements Command {
    private final int taskIndex;

    public MarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public String execute(TaskList taskList, Ui ui) throws LogosException, IOException {
        Task selectedTask = taskList.markTask(taskIndex);
        return(ui.respond("Task marked as done:", selectedTask.getAsListItem()));
    }
}
