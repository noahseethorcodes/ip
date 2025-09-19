# Logos — Your Friendly Chatbot Task Manager

Welcome to **Logos**, your personal task manager that runs in the terminal.
Logos helps you keep track of todos, deadlines, and events with simple commands.

---

## Quick Start

1. Download and run Logos from your terminal.
2. On startup, Logos greets you and shows the list of supported commands.
3. Type commands into the input field and press Enter to interact with the chatbot.
4. Use `bye` to exit the program.

---

## Features

### 1. Add a Todo

Add a simple task without a date or time.

```
todo Finish CS2103T tutorial
```

Output:

```
Todo added: "Finish CS2103T tutorial"
Now you have 2 tasks in the list~
Use the command 'list' to view your current task list
```

---

### 2. Add a Deadline

Add a task with a due date and time (format: `yyyy-MM-dd HHmm`).

```
deadline Submit lab report /by 2025-10-05 2359
```

Output:

```
Deadline added: "Submit lab report", (by: Oct 05 2025, 11:59pm)
Now you have 3 tasks in the list~
Use the command 'list' to view your current task list
```

---

### 3. Add an Event

Add an event with a start and end date/time.

```
event Project meeting /from 2025-10-03 1400 /to 2025-10-03 1600
```

Output:

```
Event added: "Project meeting", (from: Oct 03 2025, 2:00pm, to: Oct 03 2025, 4:00pm)
Now you have 4 tasks in the list~
Use the command 'list' to view your current task list
```

---

### 4. View Tasks

List all current tasks in the order they were added or sorted.

```
list
```

Output:

```
Here’s your current tasks:
1. [T][ ] Finish CS2103T tutorial
2. [D][ ] Submit lab report (by: Oct 05 2025, 11:59 PM)
...
```

---

### 5. Mark / Unmark Tasks

Mark a task as done or not done.

```
mark 1
unmark 1
```

---

### 6. Delete Tasks

Remove a task by its task number.

```
delete 2
```

---

### 7. Find Tasks

Search for tasks containing a keyword.

```
find project
```

Output will show only matching tasks, but the **task numbers are preserved from the full list**.
This means you can use the displayed index directly with other commands like `mark`, `unmark`, or `delete` without confusion.

Example:

```
Here are the matching tasks in your list:
3. [D][ ] Submit project report (by: Oct 05 2025, 11:59 PM)
5. [E][ ] Project meeting (from: Oct 03 2025, 2:00 PM to: 4:00 PM)
```

If you now run:

```
mark 3
```

…it will mark **task #3 in the full list** as done.

---

### 8. Sort Tasks

Sort tasks so that:

* Todos come first
* Deadlines follow, sorted by date
* Events come last, sorted by start time, then end time

```
sort
```

---

### 9. Exit the Program

Quit the chatbot.

```
bye
```

---

## 💡 Tips

* Date/time format is strict: `yyyy-MM-dd HHmm`. Example: `2025-10-05 2359`.
* Use `list` often to check your current tasks.
* After sorting, your task list is saved in the new order.

---

## 📸 Product Snapshot

Here’s an example task list after adding and sorting tasks:

```
1. [T][X] Finish CS2103T tutorial
2. [T][ ] Buy groceries
3. [D][ ] Submit lab report (by: Oct 05 2025, 11:59 PM)
4. [D][ ] Pay tuition fees (by: Oct 10 2025, 5:00 PM)
5. [E][ ] Project meeting (from: Oct 03 2025, 2:00 PM to: 4:00 PM)
6. [E][ ] Team dinner (from: Oct 07 2025, 7:00 PM to: 9:00 PM)
```

---

## 🛠 Troubleshooting

* **Invalid command:** Logos will show you the correct format. Example:
* **Wrong date format:** Make sure to use `yyyy-MM-dd HHmm`.

---

## 👨‍💻 About

This chatbot is developed as part of **CS2103T: Software Engineering**.
