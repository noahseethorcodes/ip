package localstorage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import errors.LogosException;
import tasks.Task;

public class Storage {
    private final String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;

        File f = new File(filePath); // create a File for the given file path
        if (!f.isFile()) { 
            System.out.println("Could not find existing data file. Creating new data file...");
            // Ensure parent directories exist
            File parentDir = f.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (parentDir.mkdirs()) {
                    System.out.println("Created missing directories: " + parentDir.getAbsolutePath());
                } else {
                    System.out.println("Failed to create directories: " + parentDir.getAbsolutePath());
                }
            }

            // Create New File
            try {
                f.createNewFile();
                System.out.println("New data file created successfully.");
            } catch (IOException e) {
                System.out.println("An error occurred while creating a new data file.");
                e.printStackTrace();
            }
        }
    }

    public void loadTasks(List<Task> tasks) {
        System.out.println("Loading tasks from local storage...");

        int successCount = 0;
        int failedCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(this.filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tasks.add(Task.fromStorageLine(line));
                successCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            failedCount++;
        } catch (LogosException e) {
            System.out.println(e.getMessage());
            failedCount++;
        }

        // Show loading results
        if (successCount + failedCount <= 0) {
            System.out.println("No tasks found in local storage. Nothing to load!");
        } else {
            System.out.println(
                    String.format("Tasks loaded: %d tasks found in storage, "
                            + "%d tasks loaded successfully, %d tasks failed", 
                            successCount + failedCount,
                            successCount, 
                            failedCount));
        }
    }

    public void saveTasks(List<Task> tasks) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.filePath))) {
            for (Task task : tasks) {
                writer.write(task.toStorageLine());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
