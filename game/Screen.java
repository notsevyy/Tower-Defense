package game;
import java.awt.*;
import javax.swing.*;

public class Screen extends JPanel implements Runnable {
    //////////////// RENDERING STUFF ///////////////////
    public Thread GameLoop = new Thread(this);
    public static Image[] tileset_ground = new Image[100];
    public static Image[] tileset_air = new Image[100];
    public static Image[] tileset_shp = new Image[100];
    public static Image[] tileset_enemy = new Image[100];
    public static int myWidth = 800, myHeight = 600;
    public static boolean isFirst = true;
    public static Point mse = new Point(0,0);
    public static boolean loadComplete = false;
    public static boolean isDebug = false;
    public static Image backgroundImg;
    //////////////// RENDERING STUFF ///////////////////
    
    //////////////// GAME OBJECTS ///////////////////
    public static Room room;
    public static Save save;
    public static Shop shop;
    public static Enemy[] enemy = new Enemy[100];
    public static int points = 30, health = 100;
    //////////////// GAME OBJECTS ///////////////////
    
    public Screen(Frame frame) {
        this.addMouseMotionListener(new KeyHandler());
        this.addMouseListener(new KeyHandler());
        this.setDoubleBuffered(true);
        GameLoop.start();
        this.setPreferredSize(new Dimension(800, 600));
    }
   
    //////////////// LOAD IMAGES AND SETUP ///////////////////
    public void define() {
        room = new Room();
        save = new Save();
        shop = new Shop();
        WaveSystem.reset();
        
        MediaTracker tracker = new MediaTracker(this);
        backgroundImg = new ImageIcon("bin/WP29.png").getImage();
        
        tileset_ground[0] = new ImageIcon("bin/grass.png").getImage();
        tracker.addImage(tileset_ground[0], 0);
       
        tileset_ground[1] = new ImageIcon("bin/road.png").getImage();
        tracker.addImage(tileset_ground[1], 1);
       
        tileset_shp[0] = new ImageIcon("bin/blocks.png").getImage();
        tracker.addImage(tileset_shp[0], 2);

        tileset_shp[1] = new ImageIcon("bin/heart.png").getImage();
        tracker.addImage(tileset_shp[1], 3);
    
        tileset_shp[2] = new ImageIcon("bin/points.png").getImage();
        tracker.addImage(tileset_shp[2], 4);

        tileset_enemy[0] = new ImageIcon("bin/enemy.png").getImage();
    
        tileset_air[0] = new ImageIcon("bin/core.png").getImage();
        tracker.addImage(tileset_air[0], 5);

        tileset_shp[4] = new ImageIcon("bin/trash.png").getImage();
        tracker.addImage(tileset_shp[4], 6);

        tileset_air[1] = new ImageIcon("bin/tower1.png").getImage();
        tracker.addImage(tileset_air[1], 7);
        
        tileset_air[2] = new ImageIcon("bin/tower2.png").getImage();
        tracker.addImage(tileset_air[2], 8);
        
        tileset_air[3] = new ImageIcon("bin/fire.png").getImage();
        tracker.addImage(tileset_air[3], 9);

        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       
        room.loadLevel(1);
        loadComplete = true;

        for(int i = 0; i < enemy.length; i++) {
            enemy[i] = new Enemy();
        }
    }
    //////////////// LOAD IMAGES AND SETUP ///////////////////
   
    //////////////// DRAW EVERYTHING ///////////////////
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isFirst) {
            myWidth = getWidth();
            myHeight = getHeight();
            room = new Room();
            room.define();
            define();
            isFirst = false;
        }
       
        // draw background
        if(backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        
        // draw grid
        if(loadComplete && room != null) {
            room.draw(g);
        }
        
        // draw enemies
        for(int i = 0; i < enemy.length; i++){
            if(enemy[i].inGame){
                enemy[i].draw(g);
            }
        }

        // draw shop
        if(shop != null) {
            shop.draw(g);
        }
        
        // wave info text (only show if game ongoing)
        if(health > 0 && !WaveSystem.gameWon) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.BOLD, 18));
            String waveInfo = WaveSystem.getWaveInfo();
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(waveInfo);
            g.drawString(waveInfo, (myWidth - textWidth) / 2, 60);
        }
        
        // game over screen - check this FIRST
        if(health <= 0){
            g.setColor(new Color(0, 0, 0, 220));
            g.fillRect(0, 0, myWidth, myHeight);

            g.setFont(new Font("Monospaced", Font.BOLD, 80));
            g.setColor(Color.RED);

            String text = "GAME OVER";
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            int x = (myWidth - textWidth) / 2;
            int y = (myHeight - textHeight) / 2 + fm.getAscent();
 
            g.drawString(text, x, y);
            
            g.setFont(new Font("Monospaced", Font.PLAIN, 24));
            g.setColor(Color.WHITE);
            String stats = "Level " + WaveSystem.currentLevel + " | Wave " + WaveSystem.currentWave;
            g.drawString(stats, (myWidth - g.getFontMetrics().stringWidth(stats)) / 2, y + 60);
        }
        // victory screen - only if NOT defeated
        else if(WaveSystem.gameWon){
            g.setColor(new Color(0, 0, 0, 220));
            g.fillRect(0, 0, myWidth, myHeight);

            g.setFont(new Font("Monospaced", Font.BOLD, 80));
            g.setColor(new Color(255, 215, 0));

            String text = "VICTORY";
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            int x = (myWidth - textWidth) / 2;
            int y = (myHeight - textHeight) / 2 + fm.getAscent();

            g.drawString(text, x, y);
            
            g.setFont(new Font("Monospaced", Font.PLAIN, 24));
            g.setColor(Color.WHITE);
            String congrats = "All Levels Complete!";
            g.drawString(congrats, (myWidth - g.getFontMetrics().stringWidth(congrats)) / 2, y + 60);
        }
    }
    //////////////// DRAW EVERYTHING ///////////////////

    //////////////// ENEMY SPAWNER ///////////////////
    public int spawnTime = 60, spawnFrame = 0;
    
    public int getSpawnRate() {
        switch(WaveSystem.currentLevel) {
            case 1:
                return 90;
            case 2:
                return 75;
            case 3:
                return 45;
            default:
                return 60;
        }
    }
    
    public void enemySpawner(){
        if(!WaveSystem.waveActive) return;
        if(WaveSystem.enemiesSpawned >= WaveSystem.enemiesToSpawn) return;
        
        spawnTime = getSpawnRate();
        
        if(spawnFrame >= spawnTime){
            for(int i = 0; i < enemy.length; i++){
                if(!enemy[i].inGame){
                    enemy[i].spawnMob(Value.enemy1);
                    WaveSystem.enemiesSpawned++;
                    break;
                }
            }
            spawnFrame = 0;
        } else {
            spawnFrame++;
        }
    }
    //////////////// ENEMY SPAWNER ///////////////////

    //////////////// GAME LOOP ///////////////////
    public void run() {
        while (true) {
            if (!isFirst && health > 0 && !WaveSystem.gameWon) {
                WaveSystem.update();
                room.physics();
                enemySpawner();

                for (int i = 0; i < enemy.length; i++) {
                    if (enemy[i].inGame) {
                        enemy[i].physics();
                    }
                }
            }
            
            repaint();
            
            try {
                Thread.sleep(16);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //////////////// GAME LOOP ///////////////////
}