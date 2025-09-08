package commands;

import java.io.IOException;
import java.util.List;

import errors.LogosException;
import tasklist.TaskList;
import ui.Ui;

public class ListCommand implements Command {
    @Override
    public String execute(TaskList taskList, Ui ui) throws LogosException, IOException {
        List<String> list = taskList.listTasks();
        if (list.isEmpty()) {
            return(ui.respond("There are no tasks in your task list currently."));
        }
        return(ui.showList(list, "Here's your current tasks, in order of when they were added:"));
    }
}
