package com.dregronprogram.enemy_types;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import com.dregronprogram.game_screen.BasicBlocks;
import com.dregronprogram.game_screen.Player;
import com.dregronprogram.handler.EnemyBulletHandler;
import com.dregronprogram.game_screen.GameScreen;
import com.dregronprogram.display.Display;

public class EnemyTypeKamikaze extends EnemyType {

    private boolean launched;
    private double speed = 1.0d;
    private Rectangle rect;
    private BufferedImage enemyImage;

    public EnemyTypeKamikaze(int x, int y, int speed, int health, EnemyBulletHandler bulletHandler) {
        super(x, y, speed, health, 30, 30, bulletHandler);
        this.launched = false;
        this.rect = new Rectangle(x, y, width, height);
        loadEnemyImage();
    }

    private void loadEnemyImage() {
        try {
            URL url = this.getClass().getResource("/com/dregronprogram/images/Kamikaze.png");
            enemyImage = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(double delta, Player player, BasicBlocks blocks) {
        if (!launched) {
            // Movimiento horizontal
            x += delta * speed;
            
            // Verificar colisión con los bordes de la pantalla
            if (x + width >= Display.WIDTH || x <= 0) {
                changeDirection(delta);
            }
        } else {
            // Lógica de persecución al jugador (mantén esto como está)
        }

        rect.setBounds(x, y, width, height);
    }


    @Override
    public void draw(Graphics2D g) {
        g.drawImage(enemyImage, x, y, width, height, null);
    }

    @Override
    public void deathScene() {
        System.out.println("Enemy Kamikaze has died.");
    }

    @Override
    public void changeDirection(double delta) {
        speed *= -1; // Simplemente invierte la dirección sin aumentar la velocidad
        y += 10; // Mueve la nave un poco hacia abajo
    }

    @Override
    public void collide(int i, Player player, BasicBlocks blocks, ArrayList<EnemyType> enemies) {
        if (!launched) return;

        for (int w = 0; w < player.playerWeapons.weapons.size(); w++) {
            if (player.playerWeapons.weapons.get(w).collisionRect(rect)) {
                enemies.remove(this);
                GameScreen.SCORE += 8;
                return;
            }
        }

        if (getBounds().intersects(player.getRect())) {
            player.hit();
            enemies.remove(this);
        }
    }

    @Override
    public boolean isOutOfBounds() {
        return x < -width || x > Display.WIDTH || y < -height || y > Display.HEIGHT;
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





