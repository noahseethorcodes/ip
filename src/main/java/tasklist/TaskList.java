package tasklist;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import errors.InvalidIndexException;

import localstorage.Storage;

import tasks.Deadline;
import tasks.Event;
import tasks.Task;
import tasks.Todo;

public class TaskList {
    private ArrayList<Task> tasks;
    private Storage storage;

    public TaskList(Storage storage) {
        this.tasks = new ArrayList<Task>();
        this.storage = storage;
    }

    public int size() {
        return tasks.size();
    }

    public void loadFromStorgae() {
        storage.loadTasks(tasks);
    }

    private void saveToStorage() throws IOException {
        storage.saveTasks(tasks);
    }

    public List<String> listTasks() {
        return tasks.stream()
                .map(Task::getAsListItem)
                .toList();
    }

    public Todo addTodo(String taskName) throws IOException{
        Todo newTodo = new Todo(taskName);
        tasks.add(newTodo);
        saveToStorage();
        return newTodo;
    }

    public Deadline addDeadline(String taskName, LocalDateTime deadline) throws IOException {
        Deadline newDeadline = new Deadline(taskName, deadline);
        tasks.add(newDeadline);
        saveToStorage();
        return newDeadline;
    }

    public Event addEvent(String taskName, LocalDateTime startDateTime, LocalDateTime endDateTime) throws IOException {
        Event newEvent = new Event(taskName, startDateTime, endDateTime);
        tasks.add(newEvent);
        saveToStorage();
        return newEvent;
    }

    public Task markTask(int taskIndex) throws InvalidIndexException, IOException {
        if (taskIndex > tasks.size() | taskIndex <= 0) {
            throw new InvalidIndexException(taskIndex);
        }
        
        Task selectedTask = tasks.get(taskIndex - 1);

        if (selectedTask.isDone()) {
            return selectedTask;
        }

        selectedTask.markAsDone();
        saveToStorage();
        return selectedTask;
    }

    public Task unmarkTask(int taskIndex) throws InvalidIndexException, IOException {
        if (taskIndex > tasks.size() | taskIndex <= 0) {
            throw new InvalidIndexException(taskIndex);
        }
        
        Task selectedTask = tasks.get(taskIndex - 1);

        if (!selectedTask.isDone()) {
            return selectedTask;
        }

        selectedTask.markAsNotDone();
        saveToStorage();
        return selectedTask;
    }

    public Task deleteTask(int taskIndex) throws InvalidIndexException, IOException {
        if (taskIndex > tasks.size() | taskIndex <= 0) {
            throw new InvalidIndexException(taskIndex);
        }
        Task selectedTask = tasks.get(taskIndex - 1);
        tasks.remove(taskIndex - 1);
        saveToStorage();
        return selectedTask;
    }
}
