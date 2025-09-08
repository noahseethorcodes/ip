package commands;

import java.io.IOException;
import java.time.LocalDateTime;

import errors.LogosException;
import tasklist.TaskList;
import tasks.Event;
import ui.Ui;

public class EventCommand implements Command {
    private final String taskName;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    public EventCommand(String taskName, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.taskName = taskName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    @Override
    public String execute(TaskList taskList, Ui ui) throws LogosException, IOException {
        Event newEvent = taskList.addEvent(taskName, startDateTime, endDateTime);
        return(ui.respond(
                String.format("Event added: \"%s\", (from: %s, to: %s)",
                        newEvent.getDescription(),
                        newEvent.getStartDateTime(),
                        newEvent.getEndDateTime()),
                String.format("Now you have %d tasks in the list~", taskList.size()),
                "Use the command 'list' to view your current task list"));
    }
}
