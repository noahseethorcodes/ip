package commands;

import java.io.IOException;

import errors.LogosException;
import tasklist.TaskList;
import ui.Ui;

public class ByeCommand implements Command {
    @Override
    public void execute(TaskList taskList, Ui ui) throws LogosException, IOException {
        ui.showExit();
    }
}
