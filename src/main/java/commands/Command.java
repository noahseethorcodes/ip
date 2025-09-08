package commands;

import java.io.IOException;

import errors.LogosException;
import tasklist.TaskList;
import ui.Ui;

@FunctionalInterface
public interface Command {
    String execute(TaskList tasks, Ui ui) throws LogosException, IOException;
}
