package game;
import java.io.File;
import java.util.Scanner;

//////////////// SAVE CLASS ///////////////////
public class Save {

    //////////////// LOAD SAVE ///////////////////
    public void loadSave(File loadPath) {
        try {
            if (!loadPath.exists()) {
                return;
            }
           
            Scanner loadScanner = new Scanner(loadPath);
            int worldWidth = Screen.room.blocks.length;      
            int worldHeight = Screen.room.blocks[0].length;
           
            for (int y = 0; y < worldHeight; y++) {
                for (int x = 0; x < worldWidth; x++) {
                    if (loadScanner.hasNextInt()) {
                        Screen.room.blocks[x][y].groundID = loadScanner.nextInt();
                    }
                }
            }
           
            for (int y = 0; y < worldHeight; y++) {
                for (int x = 0; x < worldWidth; x++) {
                    if (loadScanner.hasNextInt()) {
                        Screen.room.blocks[x][y].airID = loadScanner.nextInt();
                    }
                }
            }
           
            loadScanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //////////////// LOAD SAVE ///////////////////
}
