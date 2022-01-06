import java.io.Serializable;

public class ClickCord implements Serializable{
    private int x;
    private int y;
    private long delay;

    public ClickCord(int x, int y, long delay){
        this.x = x;
        this.y = y;
        this.delay = delay;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public long getDelay() {
        return delay;
    }
    public void setDelay(long delay) {
        this.delay = delay;
    }
}
