package com.dregronprogram.enemy_types;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import com.dregronprogram.game_screen.BasicBlocks;
import com.dregronprogram.game_screen.Player;
import com.dregronprogram.handler.EnemyBulletHandler;

public abstract class EnemyType {
    
    protected EnemyBulletHandler bulletHandler;
    protected int x, y, speed, health, width, height;
    
    public EnemyType(int x, int y, int speed, int health, int width, int height, EnemyBulletHandler bulletHandler) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.health = health;
        this.width = width;
        this.height = height;
        this.bulletHandler = bulletHandler;
    }

    public abstract void draw(Graphics2D g);
    public abstract void update(double delta, Player player, BasicBlocks blocks);
    public abstract void changeDirection(double delta);
    
    public abstract void deathScene();
    public abstract void collide(int i, Player player, BasicBlocks blocks, ArrayList<EnemyType> enemys);
    public abstract boolean isOutOfBounds();
    
    public EnemyBulletHandler getBulletHandler() {
        return bulletHandler;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
