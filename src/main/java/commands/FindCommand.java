package commands;

import java.io.IOException;
import java.util.List;

import errors.LogosException;
import tasklist.TaskList;
import ui.Ui;

public class FindCommand implements Command {
    private final String searchWord;

    public FindCommand(String searchWord) {
        this.searchWord = searchWord;
    }

    @Override
    public String execute(TaskList taskList, Ui ui) throws LogosException, IOException {
        List<Integer> indexes = taskList.findIndexes(searchWord);
        if (indexes.isEmpty()) {
            return(ui.respond(
                    String.format("There are no matching tasks in your task list for '%s'.", searchWord)));
        }
        List<String> tasks = taskList.filterByIndexes(indexes);
        return(ui.showTasksWithOriginalIndexes(
                indexes, 
                tasks,
                String.format("Here are the matching tasks in your list for '%s':", searchWord)));
    }
}
