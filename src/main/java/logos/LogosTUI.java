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

public class LogosTUI {
    private static TaskList taskList;

    private static final String LOCAL_STORAGE_FILE_PATH = "./data/tasks.txt";
    public static Storage storage;

    public static void main(String[] args) {
        // Initialise Tasks
        LogosTUI.storage = new Storage(LOCAL_STORAGE_FILE_PATH);
        LogosTUI.taskList = new TaskList(storage);
        LogosTUI.taskList.loadFromStorgae();

        // Initialise Ui and Parser
        Ui ui = new Ui();
        Parser parser = new Parser();

        // Welcome!
        String logo = " _                           \n"
                + "| |    ___   __ _  ___  ___  \n"
                + "| |   / _ \\ / _` |/ _ \\/ __| \n"
                + "| |__| (_) | (_| | (_) \\__ \\ \n"
                + "|_____\\___/ \\__, |\\___/|___/ \n"
                + "            |___/            \n";
        ui.showWelcome(logo, "Logos");

        // Input and Response
        boolean isActive = true;
        while (isActive) {
            String userInput = ui.readLine();
            try {
                Command command = parser.parse(userInput);
                if (command != null) {
                    command.execute(taskList, ui);
                }
                if (command instanceof ByeCommand) {
                    isActive = false;
                }
            } catch (UnknownCommandException e) {
                ui.respond(e.getMessage());
            } catch (InvalidCommandFormatException e) {
                ui.respond(e.getMessage());
            } catch (InvalidIndexException e) {
                ui.respond(e.getMessage());
            } catch (IOException e) {
                ui.respond("Error handling local storage: " + e.getMessage());
            } catch (LogosException e) {
                ui.respond(e.getMessage());
            }
        }
    }
}
