package com.dregronprogram.enemy_types;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;
import com.dregronprogram.display.Display;
import com.dregronprogram.enemy_bullets.EnemyBasicBullet;
import com.dregronprogram.game_screen.BasicBlocks;
import com.dregronprogram.game_screen.GameScreen;
import com.dregronprogram.game_screen.Player;
import com.dregronprogram.handler.EnemyBulletHandler;
import com.dregronprogram.sound.Sound;
import com.dregronprogram.sprite.SpriteAnimation;
import com.dregronprogram.timer.Timer;

public class EnemyTypeBasic extends EnemyType {
    private double speed = 1.0d; 
    
    private Rectangle rect;
    private SpriteAnimation enemySprite;
    
    private int shootTime;
    private Timer shootTimer;
    
    private Sound explosionSound;
    
    public EnemyTypeBasic(double xPos, double yPos, int rows, int columns, EnemyBulletHandler bulletHandler) {
        super((int)xPos, (int)yPos, 1, 1, 25, 25, bulletHandler);
        
        enemySprite = new SpriteAnimation(xPos, yPos, rows, columns, 300, "/com/dregronprogram/images/Invaders.png");
        enemySprite.setWidth(25);
        enemySprite.setHeight(25);
        enemySprite.setLimit(2);
        
        this.rect = new Rectangle((int) enemySprite.getxPos(), (int) enemySprite.getyPos(), enemySprite.getWidth(), enemySprite.getHeight());
        enemySprite.setLoop(true);
        
        shootTimer = new Timer();
        shootTime = new Random().nextInt(12000);
        
        explosionSound = new Sound("/com/dregronprogram/sounds/explosion.wav");
    }
    
    @Override
    public void draw(Graphics2D g) {
        enemySprite.draw(g);
    }

    @Override
    public void update(double delta, Player player, BasicBlocks blocks) {
        enemySprite.update(delta);
        
        enemySprite.setxPos(enemySprite.getxPos() - (delta * speed));
        this.rect.x = (int) enemySprite.getxPos();
        
        if (shootTimer.timerEvent(shootTime)) {
            getBulletHandler().addBullet(new EnemyBasicBullet(rect.x, rect.y));
            shootTime = new Random().nextInt(12000);
        }
    }

    @Override
    public void changeDirection(double delta) {
        speed *= -1.15d;
        enemySprite.setxPos(enemySprite.getxPos() - (delta * speed));
        this.rect.x = (int) enemySprite.getxPos();
        
        enemySprite.setyPos(enemySprite.getyPos() + (delta * 15));
        this.rect.y = (int) enemySprite.getyPos();
    }

    @Override
    public void deathScene() {
        if(!enemySprite.isPlay()) return;
        
        if(enemySprite.isSpriteAnimDestroyed()) {
            if (!explosionSound.isPlaying()) {
                explosionSound.play();
            }
        }
    }

    @Override
    public void collide(int i, Player player, BasicBlocks blocks, ArrayList<EnemyType> enemys) {
        if(enemySprite.isPlay()) {
            if(enemys.get(i) instanceof EnemyTypeBasic && ((EnemyTypeBasic)enemys.get(i)).enemySprite.isSpriteAnimDestroyed()) {
                enemys.remove(i);
            }
            return;
        }
        
        for(int w = 0; w < player.playerWeapons.weapons.size(); w++) {
            if(enemys != null && player.playerWeapons.weapons.get(w).collisionRect(((EnemyTypeBasic) enemys.get(i)).getRect())) {
                enemySprite.resetLimit();
                enemySprite.setAnimationSpeed(120);
                enemySprite.setPlay(true, true);
                GameScreen.SCORE += 8;
                return;
            }
        }
    }

    @Override
    public boolean isOutOfBounds() {
        return !(rect.x > 0 && rect.x < Display.WIDTH - rect.width);
    }

    public Rectangle getRect() {
        return rect;
    }

    public void setRect(Rectangle rect) {
        this.rect = rect;
    }
}
