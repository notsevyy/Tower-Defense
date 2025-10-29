package game;
import java.awt.*;
import java.io.File;

//////////////// ROOM CLASS ///////////////////
public class Room {

    //////////////// VARIABLES ///////////////////
    public int worldWidth = 12;
    public int worldHeight = 8;
    public int blockSize = 52;
    public Block[][] blocks = new Block[worldWidth][worldHeight];
    //////////////// VARIABLES ///////////////////

    //////////////// CONSTRUCTOR ///////////////////
    public Room() {
        define();
    }
    //////////////// CONSTRUCTOR ///////////////////

    //////////////// DEFINE GRID ///////////////////
    public void define() {
        blocks = new Block[worldWidth][worldHeight];
        int totalGridWidth = worldWidth * blockSize;
        int totalGridHeight = worldHeight * blockSize;
        int startX = (Screen.myWidth - totalGridWidth) / 2;
        int startY = (Screen.myHeight - totalGridHeight) / 2;
        
        for(int x = 0; x < blocks.length; x++) {
            for(int y = 0; y < blocks[0].length; y++) {
                blocks[x][y] = new Block(startX + x * blockSize, startY + y * blockSize, blockSize, blockSize, Value.grass, Value.air);
            }
        }
    }
    //////////////// DEFINE GRID ///////////////////

    //////////////// LOAD LEVEL ///////////////////
    public void loadLevel(int level) {
        for(int x = 0; x < blocks.length; x++) {
            for(int y = 0; y < blocks[0].length; y++) {
                blocks[x][y].groundID = Value.grass;
                blocks[x][y].airID = Value.air;
            }
        }
        
        String filename = "";
        switch(level) {
            case 1:
                filename = "save/level1.svl";
                break;
            case 2:
                filename = "save/level2.svl";
                break;
            case 3:
                filename = "save/level3.svl";
                break;
            default:
                filename = "save/level1.svl";
        }
        
        Screen.save.loadSave(new File(filename));
        System.out.println("Loaded level " + level + " from " + filename);
    }
    //////////////// LOAD LEVEL ///////////////////

    //////////////// PHYSICS ///////////////////
    public void physics() {
        for(int x = 0; x < blocks.length; x++) {
            for(int y = 0; y < blocks[0].length; y++) {
                blocks[x][y].physics();
            }
        }
    }
    //////////////// PHYSICS ///////////////////

    //////////////// DRAW ///////////////////
    public void draw(Graphics g) {
        for(int x = 0; x < blocks.length; x++) {
            for(int y = 0; y < blocks[0].length; y++) {
                blocks[x][y].draw(g);
            }
        }
    }
    //////////////// DRAW ///////////////////
}
