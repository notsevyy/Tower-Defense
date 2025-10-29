package game;
import java.awt.*;
import java.util.ArrayList;

public class Block extends Rectangle {
    //////////////// TOWER PROPERTIES ///////////////////
    public int groundID;
    public int airID;
    public Rectangle towerSquare;
    public int towerRange = 130;
    public int shotEnemy = -1;
    public boolean shooting = false;
    public int damage = 10;
    public int shootSpeed = 30;
    public int shootFrame = 0;
    public int killReward = 5;
    public ArrayList<Projectile> projectiles = new ArrayList<>();
    //////////////// TOWER PROPERTIES ///////////////////

    public Block(int x, int y, int width, int height, int groundID, int airID) {
        setBounds(x, y, width, height);
        towerSquare = new Rectangle(
            x - (towerRange / 2),
            y - (towerRange / 2),
            width + towerRange,
            height + towerRange
        );
        this.groundID = groundID;
        this.airID = airID;
    }

    //////////////// TOWER SHOOTING LOGIC ///////////////////
    public void physics() {
        // tower 1 - projectile shooter
        if(airID == Value.airTower1) {
            // update projectiles
            for(int i = projectiles.size() - 1; i >= 0; i--) {
                Projectile p = projectiles.get(i);
                p.update();
                
                // check if hit enemy
                if(p.targetIndex < Screen.enemy.length && Screen.enemy[p.targetIndex].inGame) {
                    if(p.getBounds().intersects(Screen.enemy[p.targetIndex])) {
                        Screen.enemy[p.targetIndex].health -= damage;
                        if(Screen.enemy[p.targetIndex].health <= 0) {
                            Screen.enemy[p.targetIndex].inGame = false;
                            Screen.points += killReward;
                            WaveSystem.enemiesKilled++;
                        }
                        projectiles.remove(i);
                        continue;
                    }
                }
                
                // remove dead projectiles
                if(p.lifeTime <= 0 || !Screen.enemy[p.targetIndex].inGame) {
                    projectiles.remove(i);
                }
            }
            
            // shoot at enemies in range
            if(shootFrame >= shootSpeed) {
                for (int i = 0; i < Screen.enemy.length; i++) {
                    if (Screen.enemy[i].inGame && towerSquare.intersects(Screen.enemy[i])) {
                        projectiles.add(new Projectile(
                            x + width / 2, 
                            y + height / 2, 
                            i
                        ));
                        shootFrame = 0;
                        break;
                    }
                }
            } else {
                shootFrame++;
            }
        }
        // tower 2 - laser
        else if(airID == Value.airTower2) {
            if (shotEnemy != -1) {
                if (Screen.enemy[shotEnemy].inGame && towerSquare.intersects(Screen.enemy[shotEnemy])) {
                    shooting = true;
                    
                    if(shootFrame >= shootSpeed) {
                        dealDamage();
                        shootFrame = 0;
                    } else {
                        shootFrame++;
                    }
                    return;
                } else {
                    shotEnemy = -1;
                    shooting = false;
                    shootFrame = 0;
                }
            }
            
            for (int i = 0; i < Screen.enemy.length; i++) {
                if (Screen.enemy[i].inGame && towerSquare.intersects(Screen.enemy[i])) {
                    shotEnemy = i;
                    shooting = true;
                    shootFrame = 0;
                    break;
                }
            }
        } else {
            shooting = false;
            shotEnemy = -1;
            shootFrame = 0;
        }
    }
    
    public void dealDamage() {
        if(shotEnemy != -1 && Screen.enemy[shotEnemy].inGame) {
            Screen.enemy[shotEnemy].health -= damage * 2;
            
            if(Screen.enemy[shotEnemy].health <= 0) {
                Screen.enemy[shotEnemy].inGame = false;
                Screen.points += killReward;
                WaveSystem.enemiesKilled++;
                shotEnemy = -1;
                shooting = false;
            }
        }
    }
    //////////////// TOWER SHOOTING LOGIC ///////////////////

    //////////////// DRAW BLOCK ///////////////////
    public void draw(Graphics g) {
        // draw ground
        if (groundID >= 0 && groundID < Screen.tileset_ground.length && Screen.tileset_ground[groundID] != null) {
            g.drawImage(Screen.tileset_ground[groundID], x, y, width, height, null);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x, y, width, height);
        }

        // draw tower if placed
        if (airID != Value.air && airID >= 0 && airID < Screen.tileset_air.length && Screen.tileset_air[airID] != null) {
            g.drawImage(Screen.tileset_air[airID], x, y, width, height, null);
        }

        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);
        
        // draw projectiles for tower 1
        if(airID == Value.airTower1) {
            for(Projectile p : projectiles) {
                p.draw(g);
            }
        }
        
        // draw laser for tower 2
        if(airID == Value.airTower2 && shooting && shotEnemy != -1 && Screen.enemy[shotEnemy].inGame){
            g.setColor(Color.RED);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(
                x + (width / 2), 
                y + (height / 2), 
                Screen.enemy[shotEnemy].x + (Screen.enemy[shotEnemy].width / 2), 
                Screen.enemy[shotEnemy].y + (Screen.enemy[shotEnemy].height / 2)
            );
            g2d.setStroke(new BasicStroke(1));
        }
        
        combat(g);
    }
    //////////////// DRAW BLOCK ///////////////////

    public void combat(Graphics g) {
        if(Screen.isDebug){
            if(airID == Value.airTower1 || airID == Value.airTower2){
                g.setColor(new Color(255, 255, 0, 100));
                g.drawRect(towerSquare.x, towerSquare.y, towerSquare.width, towerSquare.height);
            }
        }
    }    
    //////////////// PROJECTILE CLASS ///////////////////
    public class Projectile {
        int x, y, size = 8;
        int targetIndex;
        int speed = 5;
        int lifeTime = 60;
        
        public Projectile(int startX, int startY, int targetIndex) {
            this.x = startX;
            this.y = startY;
            this.targetIndex = targetIndex;
        }
        
        // move toward enemy
        public void update() {
            lifeTime--;
            if(targetIndex < Screen.enemy.length && Screen.enemy[targetIndex].inGame) {
                int targetX = Screen.enemy[targetIndex].x + Screen.enemy[targetIndex].width / 2;
                int targetY = Screen.enemy[targetIndex].y + Screen.enemy[targetIndex].height / 2;
                
                double angle = Math.atan2(targetY - y, targetX - x);
                x += Math.cos(angle) * speed;
                y += Math.sin(angle) * speed;
            }
        }
        
        public Rectangle getBounds() {
            return new Rectangle(x - size/2, y - size/2, size, size);
        }
        
        // draw fire projectile
        public void draw(Graphics g) {
            if(Screen.tileset_air[3] != null) {
                g.drawImage(Screen.tileset_air[3], x - size, y - size, size * 2, size * 2, null);
            } else {
                g.setColor(Color.ORANGE);
                g.fillOval(x - size/2, y - size/2, size, size);
            }
        }
    }
    //////////////// PROJECTILE CLASS ///////////////////
}