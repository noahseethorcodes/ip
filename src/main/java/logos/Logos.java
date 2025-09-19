package logos;

import java.io.IOException;

import ui.Ui;

import errors.UnknownCommandException;
import localstorage.Storage;
import parser.Parser;
import tasklist.TaskList;
import errors.InvalidCommandFormatException;
import errors.InvalidIndexException;
import errors.LogosException;
import commands.ByeCommand;
import commands.Command;
import commands.CommandType;

public class Logos {
    private static String LOCAL_STORAGE_FILE_PATH = "./data/tasks.txt";
    
    public Storage storage;
    private TaskList taskList;
    private Ui ui;
    private Parser parser;

    private boolean isActive = true;
    

    public Logos() {
        // Initialise Tasks
        this.storage = new Storage(LOCAL_STORAGE_FILE_PATH);
        this.taskList = new TaskList(storage);
        taskList.loadFromStorgae();

        // Initialise Ui and Parser
        this.ui = new Ui();
        this.parser = new Parser();
    }

    public boolean isActive() {
        return this.isActive;
    }

    public String getResponse(String userInput) {
        try {
            Command command = parser.parse(userInput);
            if (command != null) {
                if (command instanceof ByeCommand) {
                    this.isActive = false;
                }
                return(command.execute(taskList, ui));
            }
        } catch (UnknownCommandException e) {
            return(ui.respond(e.getMessage()));
        } catch (InvalidCommandFormatException e) {
            return(ui.respond(e.getMessage()));
        } catch (InvalidIndexException e) {
            return(ui.respond(e.getMessage()));
        } catch (IOException e) {
            return(ui.respond("Error handling local storage: " + e.getMessage()));
        } catch (LogosException e) {
            return(ui.respond(e.getMessage()));
        } catch (Exception e) {
            return(ui.respond(e.getMessage()));
        }
        return "ERROR: LOGOS DOESN'T KNOW HOW TO RESPOND";
    }

    public CommandType getCurrentCommandType() {
        return this.parser.getCurrentCommandType();
    }

    public String getWelcome() {
        return ui.showWelcome("Logos", "Logos");
    }
}
