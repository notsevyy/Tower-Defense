package game;
import java.awt.*;

//////////////// SHOP CLASS ///////////////////
public class Shop {

    //////////////// VARIABLES ///////////////////
    public static int buttonSize = 32;
    public int shopWidth = 3;
    public static int cellSpace = 2;
    public Rectangle[] button = new Rectangle[3];
    public static int iconSize = 40;
    public static int iconSpace = 5;
    public Rectangle buttonHealth;
    public Rectangle buttonPoints;
    public static int itemIn = 4;
    public static int heldID = -1;
    public static int[] buttonID = {Value.airTower1, Value.airTower2, Value.Trash};
    public boolean holdItem = false;
    public static int[] buttonPrice = {10, 25, 0};
    //////////////// VARIABLES ///////////////////

    //////////////// CONSTRUCTOR ///////////////////
    public Shop() {
        define();
    }
    //////////////// CONSTRUCTOR ///////////////////

    //////////////// CLICK HANDLER ///////////////////
    public void click(int mouseButton){
        if (mouseButton == 1){
            boolean clickedButton = false;
            for(int i = 0; i < 3; i++){
                if(button[i].contains(Screen.mse)){
                    clickedButton = true;
                    
                    if(buttonID[i] == Value.airTower2 && WaveSystem.currentLevel < 2) {
                        System.out.println("Tower 2 locked until Level 2!");
                        break;
                    }
                    
                    if(buttonID[i] == Value.Trash) {
                        holdItem = false;
                        heldID = -1;
                        System.out.println("Dropped item");
                    } else if(buttonID[i] != Value.air) {
                        heldID = buttonID[i];
                        holdItem = true;
                        System.out.println("Picked up tower ID: " + heldID);
                    }
                    break;
                }
            }
            
            if(!clickedButton && holdItem && heldID >= 0) {
                int price = 0;
                for(int i = 0; i < buttonID.length; i++) {
                    if(buttonID[i] == heldID) {
                        price = buttonPrice[i];
                        break;
                    }
                }                
                if(Screen.points >= price){
                    for(int x = 0; x < Screen.room.blocks.length; x++){
                        for(int y = 0; y < Screen.room.blocks[0].length; y++){
                            if(Screen.room.blocks[x][y].contains(Screen.mse)){    
                                if(Screen.room.blocks[x][y].groundID == Value.grass && Screen.room.blocks[x][y].airID == Value.air){
                                    Screen.room.blocks[x][y].airID = heldID;
                                    Screen.points -= price;
                                    System.out.println("Placed tower! Points: " + Screen.points);
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
    //////////////// CLICK HANDLER ///////////////////

    //////////////// DEFINE ///////////////////
    public void define() {
        int totalWidth = (3 * buttonSize) + (2 * cellSpace);
        int startX = (Screen.myWidth / 2) - (totalWidth / 2);
       
        for(int i = 0; i < 3; i++) {
            button[i] = new Rectangle(
                startX + (i * (buttonSize + cellSpace)),
                10,
                buttonSize,
                buttonSize
            );
        }
        
        buttonPoints = new Rectangle(10, Screen.myHeight - iconSize - 10, iconSize, iconSize);
        buttonHealth = new Rectangle(Screen.myWidth - iconSize - 10, Screen.myHeight - iconSize - 10, iconSize, iconSize);
    }
    //////////////// DEFINE ///////////////////

    //////////////// DRAW ///////////////////
    public void draw(Graphics g) {
        for(int i = 0; i < 3; i++) {
            g.drawImage(Screen.tileset_shp[0], button[i].x, button[i].y, button[i].width, button[i].height, null); 
            
            boolean isLocked = (buttonID[i] == Value.airTower2 && WaveSystem.currentLevel < 2);
            
            if(button[i].contains(Screen.mse)) {
                g.setColor(new Color(255, 255, 255, 150));
                g.fillRect(button[i].x, button[i].y, button[i].width, button[i].height);
            }
            
            g.setColor(Color.WHITE);
            g.drawRect(button[i].x, button[i].y, button[i].width, button[i].height);
            
            if(buttonID[i] != Value.air && buttonID[i] >= 0) {
                if(buttonID[i] == Value.Trash && Screen.tileset_shp.length > 4 && Screen.tileset_shp[4] != null) {
                    g.drawImage(Screen.tileset_shp[4], button[i].x + itemIn, button[i].y + itemIn, button[i].width - (itemIn * 2), button[i].height - (itemIn * 2), null);
                } else if(buttonID[i] < Screen.tileset_air.length && Screen.tileset_air[buttonID[i]] != null) {
                    g.drawImage(Screen.tileset_air[buttonID[i]], button[i].x + itemIn, button[i].y + itemIn, button[i].width - (itemIn * 2), button[i].height - (itemIn * 2), null);
                    
                    if(isLocked) {
                        g.setColor(new Color(0, 0, 0, 180));
                        g.fillRect(button[i].x, button[i].y, button[i].width, button[i].height);
                        g.setColor(Color.RED);
                        g.setFont(new Font("Monospaced", Font.BOLD, 20));
                        g.drawString("?", button[i].x + 8, button[i].y + 22);
                    } else if(buttonPrice[i] > 0) {
                        g.setColor(Color.YELLOW);
                        g.setFont(new Font("Monospaced", Font.BOLD, 12));
                        g.drawString("$" + buttonPrice[i], button[i].x + 3, button[i].y + button[i].height - 2);
                    }
                }
            }
        }
        
        g.drawImage(Screen.tileset_shp[1], buttonHealth.x, buttonHealth.y, buttonHealth.width, buttonHealth.height, null);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.drawString("" + Screen.health, buttonHealth.x - 42, buttonHealth.y + (buttonHealth.height / 2) + 7);
        
        g.drawImage(Screen.tileset_shp[2], buttonPoints.x, buttonPoints.y, buttonPoints.width, buttonPoints.height, null);
        g.drawString("" + Screen.points, buttonPoints.x + buttonPoints.width + iconSpace, buttonPoints.y + (buttonPoints.height / 2) + 7);
        
        if(holdItem && heldID >= 0 && heldID != Value.air) {
            int itemSize = button[0].width - (itemIn * 2);
            if(Screen.tileset_air[heldID] != null) {
                g.drawImage(Screen.tileset_air[heldID], 
                    Screen.mse.x - (itemSize / 2), 
                    Screen.mse.y - (itemSize / 2), 
                    itemSize, 
                    itemSize, 
                    null);
            }
        }
    }
    //////////////// DRAW ///////////////////
}
