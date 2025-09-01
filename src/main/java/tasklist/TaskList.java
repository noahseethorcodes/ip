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

/**
 * Represents a list of {@link Task} objects and provides methods
 * to manage them. 
 * <p>
 * This class serves as the in-memory task manager and handles persistence
 * through a {@link Storage} instance. Tasks can be created, listed,
 * marked as done/undone, and deleted. All operations are saved
 * to local storage automatically.
 */
public class TaskList {
    private ArrayList<Task> tasks;
    private Storage storage;

    /**
     * Creates a new {@code TaskList} bound to the given storage.
     * Initially, the list is empty until tasks are loaded from storage.
     *
     * @param storage the storage used for persisting tasks
     */
    public TaskList(Storage storage) {
        this.tasks = new ArrayList<Task>();
        this.storage = storage;
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return the size of the task list
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Loads tasks from the bound storage into this task list.
     * Tasks already in memory are preserved and new ones are appended.
     */
    public void loadFromStorgae() {
        storage.loadTasks(tasks);
    }

    /**
     * Saves the current task list to persistent storage.
     *
     * @throws IOException if an error occurs while writing to storage
     */
    private void saveToStorage() throws IOException {
        storage.saveTasks(tasks);
    }

    /**
     * Returns all tasks formatted as user-friendly list items.
     *
     * @return a list of string representations of tasks
     */
    public List<String> listTasks() {
        return tasks.stream()
                .map(Task::getAsListItem)
                .toList();
    }

    /**
     * Adds a new {@link Todo} task and saves the updated list.
     *
     * @param taskName the description of the todo task
     * @return the newly created {@code Todo}
     * @throws IOException if an error occurs while saving to storage
     */
    public Todo addTodo(String taskName) throws IOException{
        Todo newTodo = new Todo(taskName);
        tasks.add(newTodo);
        saveToStorage();
        return newTodo;
    }

    /**
     * Adds a new {@link Deadline} task and saves the updated list.
     *
     * @param taskName the description of the deadline task
     * @param deadline the due date and time of the task
     * @return the newly created {@code Deadline}
     * @throws IOException if an error occurs while saving to storage
     */
    public Deadline addDeadline(String taskName, LocalDateTime deadline) throws IOException {
        Deadline newDeadline = new Deadline(taskName, deadline);
        tasks.add(newDeadline);
        saveToStorage();
        return newDeadline;
    }

    /**
     * Adds a new {@link Event} task and saves the updated list.
     *
     * @param taskName the description of the event task
     * @param startDateTime the start date and time of the event
     * @param endDateTime the end date and time of the event
     * @return the newly created {@code Event}
     * @throws IOException if an error occurs while saving to storage
     */
    public Event addEvent(String taskName, LocalDateTime startDateTime, LocalDateTime endDateTime) throws IOException {
        Event newEvent = new Event(taskName, startDateTime, endDateTime);
        tasks.add(newEvent);
        saveToStorage();
        return newEvent;
    }

    /**
     * Marks the task at the given index as done, if it is not already.
     *
     * @param taskIndex the 1-based index of the task to mark
     * @return the task that was marked
     * @throws InvalidIndexException if the index is out of range
     * @throws IOException if an error occurs while saving to storage
     */
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

    /**
     * Marks the task at the given index as not done, if it is currently done.
     *
     * @param taskIndex the 1-based index of the task to unmark
     * @return the task that was unmarked
     * @throws InvalidIndexException if the index is out of range
     * @throws IOException if an error occurs while saving to storage
     */
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

    /**
     * Deletes the task at the given index from the list and storage.
     *
     * @param taskIndex the 1-based index of the task to delete
     * @return the task that was removed
     * @throws InvalidIndexException if the index is out of range
     * @throws IOException if an error occurs while saving to storage
     */
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
