package game;

public class WaveSystem {
    ////////////////////////////////
    // Variables
    ////////////////////////////////
    public static int currentLevel = 1;
    public static int currentWave = 1;
    public static int enemiesKilled = 0;
    public static int enemiesSpawned = 0;
    public static int enemiesToSpawn = 5;
    
    public static int waveTimer = 0;
    public static int waveDuration = 900;
    public static boolean waveActive = false;
    public static boolean levelComplete = false;
    public static boolean gameWon = false;
    
    public static int wavesPerLevel = 2;
    public static int finalLevelWaves = 3;
    
    public static int baseHealth = 50;
    public static int healthIncreasePerWave = 20;

    ////////////////////////////////
    // Reset
    ////////////////////////////////
    public static void reset() {
        currentLevel = 1;
        currentWave = 1;
        enemiesKilled = 0;
        enemiesSpawned = 0;
        enemiesToSpawn = 5;
        waveTimer = 0;
        waveActive = false;
        levelComplete = false;
        gameWon = false;
        Screen.health = 100;
        Screen.points = 30;
    }

    ////////////////////////////////
    // Update
    ////////////////////////////////
    public static void update() {
        if (gameWon || Screen.health <= 0) return;
        
        waveTimer++;
        if (!waveActive && waveTimer >= waveDuration) {
            startWave();
        }
        
        if (waveActive && enemiesSpawned >= enemiesToSpawn) {
            int activeEnemies = 0;
            for (int i = 0; i < Screen.enemy.length; i++) {
                if (Screen.enemy[i].inGame) activeEnemies++;
            }
            if (activeEnemies == 0) {
                completeWave();
            }
        }
    }

    ////////////////////////////////
    // Wave Start
    ////////////////////////////////
    public static void startWave() {
        waveActive = true;
        enemiesSpawned = 0;
        waveTimer = 0;
        enemiesToSpawn = 5 + (currentWave * 2);
        
        System.out.println("=== WAVE " + currentWave + " START ===");
        System.out.println("Level: " + currentLevel + " | Enemies: " + enemiesToSpawn);
    }

    ////////////////////////////////
    // Wave Complete
    ////////////////////////////////
    public static void completeWave() {
        waveActive = false;
        waveTimer = 0;
        currentWave++;
        
        System.out.println("=== WAVE COMPLETE ===");
        System.out.println("Enemies killed this wave: " + enemiesKilled);
        Screen.points += 20;
        
        int requiredWaves = (currentLevel == 3) ? finalLevelWaves : wavesPerLevel;
        if (currentWave > requiredWaves) {
            if (currentLevel < 3) {
                levelComplete = true;
                nextLevel();
            } else {
                gameWon = true;
                System.out.println("=== GAME WON! ===");
            }
        }
    }

    ////////////////////////////////
    // Next Level
    ////////////////////////////////
    public static void nextLevel() {
        currentLevel++;
        currentWave = 1;
        enemiesKilled = 0;
        levelComplete = false;
        
        if (currentLevel == 2) Screen.points = 40;
        else if (currentLevel == 3) Screen.points = 50;
        
        Screen.health = Math.min(Screen.health + 20, 100);
        
        System.out.println("=== LEVEL " + currentLevel + " START ===");
        System.out.println("Points reset to: " + Screen.points);
        
        Screen.room.loadLevel(currentLevel);
        
        for (int i = 0; i < Screen.enemy.length; i++) {
            Screen.enemy[i].inGame = false;
        }
        
        for (int x = 0; x < Screen.room.blocks.length; x++) {
            for (int y = 0; y < Screen.room.blocks[0].length; y++) {
                if (Screen.room.blocks[x][y].airID == Value.airTower1 ||
                    Screen.room.blocks[x][y].airID == Value.airTower2) {
                    Screen.room.blocks[x][y].airID = Value.air;
                }
            }
        }
    }

    ////////////////////////////////
    // Utility
    ////////////////////////////////
    public static int getEnemyHealth() {
        return baseHealth + (currentWave * healthIncreasePerWave);
    }

    public static String getWaveInfo() {
        if (gameWon) return "VICTORY!";
        if (Screen.health <= 0) return "DEFEATED";
        
        int requiredWaves = (currentLevel == 3) ? finalLevelWaves : wavesPerLevel;
        String status = waveActive ? "ACTIVE" : "Preparing...";
        return "Level " + currentLevel + " | Wave " + currentWave + "/" + requiredWaves + " | " + status;
    }
}
