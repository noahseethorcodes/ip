package commands;

import java.io.IOException;
import java.time.LocalDateTime;

import errors.LogosException;
import tasklist.TaskList;
import tasks.Deadline;
import ui.Ui;

public class DeadlineCommand implements Command {
    private final String taskName;
    private final LocalDateTime deadline;

    public DeadlineCommand(String taskName, LocalDateTime deadline) {
        this.taskName = taskName;
        this.deadline = deadline;
    }

    @Override
    public void execute(TaskList taskList, Ui ui) throws LogosException, IOException {
        Deadline newDeadline = taskList.addDeadline(taskName, deadline);
        ui.respond(
                String.format("Deadline added: \"%s\", (by: %s)",
                        newDeadline.getDescription(),
                        newDeadline.getDeadline()),
                String.format("Now you have %d tasks in the list~", taskList.size()),
                "Use the command 'list' to view your current task list");
    }
}
