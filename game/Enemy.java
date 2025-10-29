package game;
import java.awt.*;

public class Enemy extends Rectangle {
    //////////////// ENEMY PROPERTIES ///////////////////
    public int enemySize = 52;
    public int enemyID = Value.enemyAir;
    public boolean inGame = false;
    public int xC, yC;
    public int enemyWalk = 0, upward = 0, downward = 1, right = 2;
    public int direction = right;
    public int previousDirection = -1;
    public int maxHealth = 50;
    public int health = maxHealth;
    public int healthSpace = 3, healthHeight = 6;
    //////////////// ENEMY PROPERTIES ///////////////////
    
    public Enemy() {
    }

    //////////////// SPAWN ENEMY ///////////////////
    public void spawnMob(int enemyID) {
        boolean spawned = false;
        
        // level 2 spawns from top
        if (WaveSystem.currentLevel == 2) {
            for (int x = 0; x < Screen.room.blocks.length && !spawned; x++) {
                if (Screen.room.blocks[x][0].groundID == Value.enemyGround) {
                    int spawnX = Screen.room.blocks[x][0].x;
                    int spawnY = Screen.room.blocks[x][0].y;
                    
                    boolean positionTaken = false;
                    for (int i = 0; i < Screen.enemy.length; i++) {
                        if (Screen.enemy[i] != null && Screen.enemy[i].inGame && 
                            Screen.enemy[i].x == spawnX && 
                            Screen.enemy[i].y == spawnY) {
                            positionTaken = true;
                            break;
                        }
                    }
                    
                    if (!positionTaken) {
                        setBounds(spawnX, spawnY, enemySize, enemySize);
                        xC = x;
                        yC = 0;
                        spawned = true;
                        direction = downward;
                        maxHealth = WaveSystem.getEnemyHealth();
                        health = maxHealth;
                    }
                }
            }
        } else {
            // levels 1 and 3 spawn from left
            for (int y = 0; y < Screen.room.blocks[0].length && !spawned; y++) {
                if (Screen.room.blocks[0][y].groundID == Value.enemyGround) {
                    int spawnX = Screen.room.blocks[0][y].x;
                    int spawnY = Screen.room.blocks[0][y].y;
                    
                    boolean positionTaken = false;
                    for (int i = 0; i < Screen.enemy.length; i++) {
                        if (Screen.enemy[i] != null && Screen.enemy[i].inGame && 
                            Screen.enemy[i].x == spawnX && 
                            Screen.enemy[i].y == spawnY) {
                            positionTaken = true;
                            break;
                        }
                    }
                    
                    if (!positionTaken) {
                        setBounds(spawnX, spawnY, enemySize, enemySize);
                        xC = 0;
                        yC = y;
                        spawned = true;
                        direction = right;
                        maxHealth = WaveSystem.getEnemyHealth();
                        health = maxHealth;
                    }
                }
            }
        }
        
        this.enemyID = enemyID;
        inGame = spawned;
    }
    //////////////// SPAWN ENEMY ///////////////////

    //////////////// ENEMY MOVEMENT ///////////////////
    public int walkSpeed = 1, walkFrame = 0;

    public void physics() {
        if (!inGame) return;

        walkFrame++;
        if (walkFrame < walkSpeed) return;
        walkFrame = 0;

        // move
        if (direction == right) {
            x += 2;
        } else if (direction == upward) {
            y -= 2;
        } else if (direction == downward) {
            y += 2;
        }

        enemyWalk += 2;

        // moved a full block
        if (enemyWalk >= Screen.room.blockSize) {
            enemyWalk = 0;

            // update grid position
            if (direction == right) {
                xC += 1;
            } else if (direction == upward) {
                yC -= 1;
            } else if (direction == downward) {
                yC += 1;
            }

            // check if reached end
            if (xC >= Screen.room.blocks.length || xC < 0 || 
                yC >= Screen.room.blocks[0].length || yC < 0) {
                escape();
                return;
            }

            // check if hit core
            if (Screen.room.blocks[xC][yC].airID == Value.airCore) {
                escape();
                return;
            }

            // pathfinding
            boolean foundPath = false;

            if (direction == right && xC + 1 < Screen.room.blocks.length &&
                Screen.room.blocks[xC + 1][yC].groundID == Value.enemyGround) {
                direction = right;
                foundPath = true;
            }
            else if (direction == downward && yC + 1 < Screen.room.blocks[0].length &&
                     Screen.room.blocks[xC][yC + 1].groundID == Value.enemyGround) {
                direction = downward;
                foundPath = true;
            }
            else if (direction == upward && yC - 1 >= 0 &&
                     Screen.room.blocks[xC][yC - 1].groundID == Value.enemyGround) {
                direction = upward;
                foundPath = true;
            }
            else if (yC + 1 < Screen.room.blocks[0].length &&
                     Screen.room.blocks[xC][yC + 1].groundID == Value.enemyGround &&
                     direction != upward) {
                direction = downward;
                foundPath = true;
            }
            else if (yC - 1 >= 0 &&
                     Screen.room.blocks[xC][yC - 1].groundID == Value.enemyGround &&
                     direction != downward) {
                direction = upward;
                foundPath = true;
            }
            else if (xC + 1 < Screen.room.blocks.length &&
                     Screen.room.blocks[xC + 1][yC].groundID == Value.enemyGround) {
                direction = right;
                foundPath = true;
            }

            if (!foundPath) {
                escape();
            }
        }
    }

    public void deleteMob() {
        inGame = false;
        health = 0;
    }
    
    public void escape() {
        Screen.health -= 10;
        deleteMob();
    }
    //////////////// ENEMY MOVEMENT ///////////////////

    //////////////// DRAW ENEMY ///////////////////
    public void draw(Graphics g) {
        if (!inGame) return;
        
        // draw sprite
        if (Screen.tileset_enemy[enemyID] != null) {
            g.drawImage(Screen.tileset_enemy[enemyID], x, y, width, height, null);
        }
        
        // draw health bar
        int barWidth = width;
        int barX = x;
        int barY = y - (healthSpace + healthHeight);
        
        g.setColor(Color.RED);
        g.fillRect(barX, barY, barWidth, healthHeight);
        
        int healthWidth = (int)((health / (float)maxHealth) * barWidth);
        g.setColor(Color.GREEN);
        g.fillRect(barX, barY, healthWidth, healthHeight);
        
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, healthHeight);
    }
    //////////////// DRAW ENEMY ///////////////////
}