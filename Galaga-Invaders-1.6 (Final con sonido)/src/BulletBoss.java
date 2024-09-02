import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class BulletBoss {
    List<Bullet> bullets; // Lista para almacenar las tres balas

    public BulletBoss(int x, int y, int width, int height, int speed, Color color) {
        bullets = new ArrayList<>();
        
        // Crea tres balas con diferentes direcciones
        bullets.add(new Bullet(x - width, y, width, height, speed, color, -1)); // Bala hacia la izquierda
        bullets.add(new Bullet(x, y, width, height, speed, color, 0)); // Bala hacia abajo
        bullets.add(new Bullet(x + width, y, width, height, speed, color, 1)); // Bala hacia la derecha
    }

    public void update() {
        // Actualiza cada bala individualmente
        for (Bullet bullet : bullets) {
            bullet.update();
        }
    }

    public void draw(Graphics g) {
        // Dibuja cada bala individualmente
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
    }

    // Clase interna para representar cada bala individual
    class Bullet {
        private int x, y;
        private int width, height;
        private int speed;
        private int direction; // -1 para izquierda, 0 para abajo, 1 para derecha
        private Color color;

        public Bullet(int x, int y, int width, int height, int speed, Color color, int direction) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.speed = speed;
            this.color = color;
            this.direction = direction;
        }

        public void update() {
            y += speed; // La bala siempre se mueve hacia abajo

            // Movimiento horizontal basado en la dirección
            x += direction * speed;
        }

        public void draw(Graphics g) {
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }

        public int getY() {
            return y;
        }

        public int getX() {
            return x;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}
