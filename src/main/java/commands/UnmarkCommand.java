package commands;

import java.io.IOException;

import errors.LogosException;
import tasklist.TaskList;
import tasks.Task;
import ui.Ui;

public class UnmarkCommand implements Command {
    private final int taskIndex;

    public UnmarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public String execute(TaskList taskList, Ui ui) throws LogosException, IOException {
        Task selectedTask = taskList.unmarkTask(taskIndex);
        return(ui.respond("Task marked as not done yet:", selectedTask.getAsListItem()));
    }
}
