package commands;

import java.io.IOException;
import java.util.List;

import errors.LogosException;
import tasklist.TaskList;
import ui.Ui;

public class SortCommand implements Command {
    @Override
    public String execute(TaskList taskList, Ui ui) throws LogosException, IOException {
        if (taskList.size() == 0) {
            return(ui.respond("There are no tasks to sort in your task list currently!"));
        }
        taskList.sortTasks();
        List<String> list = taskList.listTasks();
        return(ui.showList(list, "Here's your freshly sorted tasks, in chronological order:"));
    }
}
