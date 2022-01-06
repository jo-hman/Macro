import java.io.Serializable;

public class Task implements Serializable{
    String filePath;
    private boolean isBypassed;

    private Macro macro;
    
    public Task(String filePath){
        this.filePath = filePath;
        this.macro = new Macro();
        this.isBypassed = false;
    }

    public Task(Task task){
        this.isBypassed = task.getIsBypassed();
        this.filePath = task.getFilePath();
        this.macro = task.getMacro();
    }

    public void removeClickFromMacro(int index){
        macro.removeClick(index);
    }

    public void copyMacro(Macro macro){
            this.macro = new Macro(macro);   
    }

    public  void setFilePath(String filePath){
        this.filePath = filePath;
    }

    public void removeMacro(){
        macro.removeMacro();
    }

    public String getFilePath() {
        return filePath;
    }

    public Macro getMacro() {
        return macro;
    }

    public boolean getIsSet(){
        return macro.getIsSet();
    }

    public boolean getIsBypassed(){
        return isBypassed;
    }
    public void setBypassed() {
        if(this.isBypassed) this.isBypassed = false;
        else this.isBypassed = true;
    }
}
