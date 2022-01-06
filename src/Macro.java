

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

import java.io.Serializable;

public class Macro implements Serializable {
    private LinkedList<ClickCord> clicks; 
    private boolean isSet;

    public Macro(){
        this.isSet = false;
    }

    public Macro(Macro macro){
        this.clicks = new LinkedList<>(macro.getClicks());
        this.isSet = macro.getIsSet();
    }

    public void removeMacro(){
        this.clicks = null;
        this.isSet = false;
    }
    
    public LinkedList<ClickCord> getClicks() {
        return clicks;
    }

    public void changeDelay(int type, int index, int time){
        long delay;
        if(type == 2){
            delay = TimeUnit.MINUTES.toMillis(time);
        } else if (type == 1){
            delay = TimeUnit.SECONDS.toMillis(time);
        } else delay = TimeUnit.SECONDS.toMillis(5);

        clicks.get(index).setDelay(delay);
    }

    public boolean addClick(int type, int time, int index){
        if(!isSet){
            this.clicks = new LinkedList<>();
            this.isSet = true;
        }
        
        Robot robot = null;
        try{
            robot = new Robot();
        } catch (Exception ex){
            System.out.println("Blad przy tworzeniu bota" + ex);
            return false;
        }
        
        final long delay;
        if(type == 1){
            delay = TimeUnit.SECONDS.toMillis(time);
        } else if (type == 2){
            delay = TimeUnit.MINUTES.toMillis(time);
        } else delay = TimeUnit.SECONDS.toMillis(5);
        final Dimension screenSize = Toolkit.getDefaultToolkit().
            getScreenSize();       

        final BufferedImage screen = robot.createScreenCapture(
        new Rectangle(screenSize));

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JLabel screenLabel = new JLabel(new ImageIcon(screen));
                JScrollPane screenScroll = new JScrollPane(screenLabel);
                screenScroll.setPreferredSize(new Dimension(
                    (int)(screenSize.getWidth()/2),
                    (int)(screenSize.getHeight()/2)));

                final Point pointOfInterest = new Point();

                JPanel panel = new JPanel(new BorderLayout());
                panel.add(screenScroll, BorderLayout.CENTER);

                final JLabel pointLabel = new JLabel(
                    "Kliknij w wybranym miejscu na zrzucie ekranu");
                panel.add(pointLabel, BorderLayout.SOUTH);
                

                screenLabel.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent me) {
                        pointOfInterest.setLocation(me.getPoint());
                        pointLabel.setText(
                            "Punkt: " +
                            pointOfInterest.getX() +
                            " x " +
                            pointOfInterest.getY());
                    }
                });

                JOptionPane.showMessageDialog(null, panel);
                

                clicks.add(index + 1, new ClickCord((int)pointOfInterest.getX(), (int)pointOfInterest.getY(), delay));
                }
            }); 
            return true;
        }


    public boolean recordMacro(int type, int time){
        if(!isSet){
            this.clicks = new LinkedList<>();
            this.isSet = true;
        }
        
        Robot robot = null;
        try{
            robot = new Robot();
        } catch (Exception ex){
            return false;
        }
        
        final Dimension screenSize = Toolkit.getDefaultToolkit().
            getScreenSize();         
        
        final long delay;
        if(type == 1){
            delay = TimeUnit.SECONDS.toMillis(time);
        } else if (type == 2){
            delay = TimeUnit.MINUTES.toMillis(time);
        } else delay = TimeUnit.SECONDS.toMillis(5);

        final BufferedImage screen = robot.createScreenCapture(
        new Rectangle(screenSize));

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JLabel screenLabel = new JLabel(new ImageIcon(screen));
                JScrollPane screenScroll = new JScrollPane(screenLabel);
                screenScroll.setPreferredSize(new Dimension(
                    (int)(screenSize.getWidth()/2),
                    (int)(screenSize.getHeight()/2)));

                final Point pointOfInterest = new Point();

                JPanel panel = new JPanel(new BorderLayout());
                panel.add(screenScroll, BorderLayout.CENTER);

                final JLabel pointLabel = new JLabel(
                    "Kliknij w wybranym miejscu na zrzucie ekranu");
                panel.add(pointLabel, BorderLayout.SOUTH);
                

                screenLabel.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent me) {
                        pointOfInterest.setLocation(me.getPoint());
                        pointLabel.setText(
                            "Punkt: " +
                            pointOfInterest.getX() +
                            " x " +
                            pointOfInterest.getY());
                    }
                });

                JOptionPane.showMessageDialog(null, panel);
                
                clicks.add(new ClickCord((int)pointOfInterest.getX(), (int)pointOfInterest.getY(), delay));
            }
        });  
        return true;
    }

    public void runClicks(){
        try {
            Robot bot = new Robot();
            TimeUnit.SECONDS.sleep(15);

            for (ClickCord click : clicks){          
                bot.mouseMove(click.getX(), click.getY());           
                bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    
                
                Thread.sleep(click.getDelay());
                }
        } catch (Exception ex) {
            Main.displayError(ex.getMessage());
            System.exit(0);
        }
    }

    public void removeClick(int index) {
        try{
            clicks.remove(index);
            clicks.get(0);
        } catch (Exception ex) {
            this.clicks = null;
            this.isSet = false;
        }
    }

    public boolean getIsSet(){
        return this.isSet;
    }  

    public void setIsSet(boolean isSet) {
        this.isSet = isSet;
    }
}
