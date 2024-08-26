package com.dregronprogram.enemy_types;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.IOException;
import com.dregronprogram.game_screen.BasicBlocks;
import com.dregronprogram.game_screen.Player;
import com.dregronprogram.handler.EnemyBulletHandler;
import com.dregronprogram.game_screen.GameScreen; 
import com.dregronprogram.display.Display; 
import com.dregronprogram.enemy_bullets.EnemyBasicBullet; 
import java.net.URL;

public class EnemyTypeKamikaze extends EnemyType {

    private boolean launched;
    private Rectangle rect;  
    private Image kamikazeImage;  // Variable para almacenar la imagen

    public EnemyTypeKamikaze(int x, int y, int speed, int health, EnemyBulletHandler bulletHandler) {
        super(x, y, speed, health, 30, 30, bulletHandler);
        this.launched = false;
        this.rect = new Rectangle(x, y, width, height);  

        // Cargar la imagen Kamikaze.png
        try {
        	URL url = this.getClass().getResource("/com/dregronprogram/images/Kamikaze.png");
			kamikazeImage = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(double delta, Player player, BasicBlocks blocks) {
        if (!launched) return;
        
        int playerX = player.getRect().x;
        int playerY = player.getRect().y;

        if (x < playerX) {
            x += speed;
        } else if (x > playerX) {
            x -= speed;
        }

        if (y < playerY) {
            y += speed;
        } else if (y > playerY) {
            y -= speed;
        }
        
        rect.setBounds(x, y, width, height);
    }

    @Override
    public void draw(Graphics2D g) {
        if (kamikazeImage != null) {
            g.drawImage(kamikazeImage, x, y, width, height, null);
        } else {
            // Si la imagen no se carga correctamente, dibuja un rectángulo como respaldo
            g.fillRect(x, y, width, height);
        }
    }

    @Override
    public void deathScene() {
        System.out.println("Enemy Kamikaze has died.");
    }

    @Override
    public void changeDirection(double delta) {
    }

    @Override
    public void collide(int i, Player player, BasicBlocks blocks, ArrayList<EnemyType> enemies) {
        if (!launched) return;

        for (int w = 0; w < player.playerWeapons.weapons.size(); w++) {
            if (player.playerWeapons.weapons.get(w).collisionRect(rect)) {
                enemies.remove(i);
                GameScreen.SCORE += 8; 
                return;
            }
        }

        if (getBounds().intersects(player.getRect())) {
            player.hit();
            enemies.remove(i);
        }
    }

    @Override
    public boolean isOutOfBounds() {
        return x < 0 || x > Display.WIDTH || y < 0 || y > Display.HEIGHT;
    }

    public void setLaunched(boolean launched) {
        this.launched = launched;
    }

    public boolean isLaunched() {
        return launched;
    }

    public Rectangle getBounds() {
        return rect;
    }
}






