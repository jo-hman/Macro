import java.io.Serializable;

import java.util.LinkedList;
import java.util.TimerTask;

import java.awt.Desktop;

import java.io.File;

public class TasksList extends TimerTask implements Serializable{
    private LinkedList<Task> tasks = new LinkedList<>();
    private int hour;
    private int minute;
    private int singleTaskRunIndex;

    public void removeMacro(int index, int type){
        tasks.get(index ).removeMacro();
    }

    public Task getTask(int index) {
        return this.tasks.get(index );
    }

    public void addTask(String filePath){
        tasks.add(new Task(filePath));  
    }

    public LinkedList<Task> getTasks() {
        return tasks;
    }
    
    @Override
    public void run() { // had to make displaying error here because in java 11 i cant make overridden method throw an exception
        if(singleTaskRunIndex > -1){
            File file = new File(tasks.get(singleTaskRunIndex ).getFilePath());
            try {
                Desktop.getDesktop().open(file);
            } catch (Exception e) {
                Main.displayError(e.getMessage());
                return;
            }
            if(tasks.get(singleTaskRunIndex ).getMacro().getIsSet()){
                int id = singleTaskRunIndex;
                singleTaskRunIndex = -1;
                    tasks.get(id).getMacro().runClicks();
            }
        } else {
            for(Task task : tasks){
                
                if(!task.getIsBypassed()){ 
                    File file = new File(task.getFilePath());
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (Exception e) {
                        Main.displayError(e.getMessage());
                        continue;
                    }

                    if(task.getMacro().getIsSet())
                        task.getMacro().runClicks();
                }
            }
        }
    }

    public void setSingleTaskRun(int set){
        this.singleTaskRunIndex = set;
    }

    public void remove(int index){  
        tasks.remove(index );
    }
    public int getHour() {
        return hour;
    }
    public int getMinute() {
        return minute;
    }
    public void setHour(int hour) {
        this.hour = hour;
    }
}
