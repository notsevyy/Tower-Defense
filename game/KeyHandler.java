package game;
import java.awt.event.*;

public class KeyHandler implements MouseMotionListener, MouseListener{
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
        Screen.shop.click(e.getButton());
    }
    public void mouseReleased(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    
    public void mouseMoved(MouseEvent e) {
        Screen.mse = e.getPoint();
    }
}