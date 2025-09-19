package tasklist;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

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
    public Todo addTodo(String taskName) throws IOException {
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
        assert taskIndex >= 1 && taskIndex <= tasks.size() : "Index out of bounds for task list";
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
        assert taskIndex >= 1 && taskIndex <= tasks.size() : "Index out of bounds for task list";
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
        assert taskIndex >= 1 && taskIndex <= tasks.size() : "Index out of bounds for task list";
        Task selectedTask = tasks.get(taskIndex - 1);
        tasks.remove(taskIndex - 1);
        saveToStorage();
        return selectedTask;
    }

    public List<String> filterByIndexes(List<Integer> indexes) {
        return indexes.stream()
                .map(tasks::get)
                .map(Task::getAsListItem)
                .toList();
    }

    public List<Integer> findIndexes(String searchWord) {
        final String q = searchWord.toLowerCase();
        return IntStream.range(0, tasks.size())
                .filter(i -> tasks.get(i).getDescription().toLowerCase().contains(q))
                .boxed()
                .toList();
    }

    /**
     * Sorts the in-memory task list with the following precedence and then saves:
     * <ol>
     *   <li>Todos first (relative order among Todos is preserved)</li>
     *   <li>Deadlines next, ascending by deadline datetime</li>
     *   <li>Events last, ascending by start datetime, then by end datetime</li>
     * </ol>
     * When comparing a deadline against an event, the deadline's datetime is
     * compared against the event's start datetime.
     *
     * @throws IOException if an error occurs while saving to storage
     */
    public void sortTasks() throws IOException {
        Comparator<Task> cmp = (a, b) -> {
            int ra = rank(a);
            int rb = rank(b);
            if (ra != rb) return Integer.compare(ra, rb);

            // Same rank → type-specific comparison
            if (a instanceof Todo && b instanceof Todo) {
                // Keep insertion order for Todos; List.sort is stable.
                return 0;
            }

            if (a instanceof Deadline && b instanceof Deadline) {
                LocalDateTime deadlineA = ((Deadline) a).getDeadline();
                LocalDateTime deadlineB = ((Deadline) b).getDeadline();
                return deadlineA.compareTo(deadlineB);
            }

            if (a instanceof Event && b instanceof Event) {
                Event eventA = (Event) a;
                Event eventB = (Event) b;
                int byStart = eventA.getStartDateTime().compareTo(eventB.getStartDateTime());
                return (byStart != 0) ? byStart : eventA.getEndDateTime().compareTo(eventB.getEndDateTime());
            }

            // Cross-type within same rank (Deadline vs Event)
            if (a instanceof Deadline && b instanceof Event) {
                LocalDateTime deadlineA = ((Deadline) a).getDeadline();
                LocalDateTime eventBStart = ((Event) b).getStartDateTime();
                return deadlineA.compareTo(eventBStart);
            }
            if (a instanceof Event && b instanceof Deadline) {
                LocalDateTime eventAStart = ((Event) a).getStartDateTime();
                LocalDateTime deadlineB = ((Deadline) b).getDeadline();
                return eventAStart.compareTo(deadlineB);
            }

            // Fallback: keep order
            return 0;
        };

        // TimSort used by List.sort is stable → Todos keep relative order
        tasks.sort(cmp);
        saveToStorage();
    }

    /** Returns a type rank: lower comes first (Todo → Deadline → Event). */
    private static int rank(Task t) {
        if (t instanceof Todo) return 0;
        if (t instanceof Deadline) return 1;
        if (t instanceof Event) return 2;
        // Unknown kinds go last by default
        return Integer.MAX_VALUE;
    }

}
