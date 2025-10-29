package game;
import javax.swing.*;
import java.awt.*;

//////////////// FRAME CLASS ///////////////////
public class Frame extends JFrame {

    //////////////// VARIABLES ///////////////////
    public static String title = "CS201";
    public static Dimension size = new Dimension(800, 600);
    //////////////// VARIABLES ///////////////////

    //////////////// CONSTRUCTOR ///////////////////
    public Frame() {
        this.setTitle(title);
        this.setSize(size);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        init(); 
    }
    //////////////// CONSTRUCTOR ///////////////////

    //////////////// INITIALIZATION ///////////////////
    public void init() {
        this.setLayout(new GridLayout(1, 1, 0, 0));
        this.setVisible(true);
        Screen screen = new Screen(this);
        add(screen);
    }
    //////////////// INITIALIZATION ///////////////////

    //////////////// MAIN ENTRY POINT ///////////////////
    public static void main(String[] args) {
        Frame frame = new Frame();
        frame.setTitle("Tower Defense CS201");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    //////////////// MAIN ENTRY POINT ///////////////////
}
