package rxclass.cmush.todolist.util;

import java.util.ArrayList;
import java.util.List;

import rxclass.cmush.todolist.models.Task;

public class DataSource {
    public static List<Task> createTasksList(){
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Take out the trash", true, 3));
        tasks.add(new Task("Walk the dog", false, 2));
        tasks.add(new Task("Make my bed", true, 1));
        tasks.add(new Task("Unload the dishwasher", false, 0));
        tasks.add(new Task("Make dinner", true, 5));
        return tasks;
    }

    public static Task[] createTasksArray(){
        Task[] tasksArray = new Task[5];
        tasksArray[0] = (new Task("Take out the trash", true, 3));
        tasksArray[1] = (new Task("Walk the dog", false, 2));
        tasksArray[2] = (new Task("Make my bed", true, 1));
        tasksArray[3] = (new Task("Unload the dishwasher", false, 0));
        tasksArray[4] = (new Task("Make dinner", true, 5));
        return tasksArray;
    }
}
