import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Player {
    public static int x, y;
    private int width, height;
    private int speed;
    private Color color;
    private List<BulletPlayer> bullets; // array de balas
    private Boss boss; // Referencia al jefe
    
    private long lastShotTime = 0; // Tiempo del último disparo
    private final long COOLDOWN = 150; // Tiempo de espera entre disparos (milisegundos)
    private Clip clip; // Referencia al clip de audio
    private Image image; // Imagen del jugador

    public Player(int x, int y, int width, int height, int speed, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.color = color;
        this.bullets = new ArrayList<>();

        // Cargar la imagen del jugador
        try {
            image = ImageIO.read(getClass().getResource("/images/player.png")); // Asegúrate de que el archivo está en esta ruta
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se pudo cargar la imagen del jugador.");
        }
    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null); // Dibuja la imagen del jugador
        } else {
            g.setColor(color);
            g.fillRect(x, y, width, height); // Dibuja un rectángulo de color como fallback
        }

        // Dibujar las balas
        for (BulletPlayer bullet : bullets) {
            bullet.draw(g);
        }
    }

    public void update(boolean left, boolean right, boolean space, List<Enemy> enemies, List<Kamikaze> kamikazes) {
        if (left) {
            x -= speed;
        }
        if (right) {
            x += speed;
        }

        long currentTime = System.currentTimeMillis();
        if ((space && (currentTime - lastShotTime >= COOLDOWN))) {
            shoot();
            lastShotTime = currentTime; // Actualizar el tiempo del último disparo
        }

        // Limitar la posición dentro de los límites de la ventana
        if (x < 5) {
            x = 5;
        }
        if (x + width + 25 > Window.WIDTH) {
            x = Window.WIDTH - width - 25;
        }

        // Actualizar todas las balas y eliminar las que están fuera de la pantalla
        List<BulletPlayer> bulletsToRemove = new ArrayList<>();
        for (BulletPlayer bullet : bullets) {
            bullet.update();
            if (bullet.getY() < 0) {
                bulletsToRemove.add(bullet);
            }

            // Colisiones con enemigos normales
            for (Enemy enemy : enemies) {
                if (enemy.isAlive() &&
                    bullet.getY() < enemy.getY() + enemy.getHeight() &&
                    bullet.getY() + bullet.getHeight() > enemy.getY() &&
                    bullet.getX() < enemy.getX() + enemy.getWidth() &&
                    bullet.getX() + bullet.getWidth() > enemy.getX()) {
                    enemy.die(); // Marcar al enemigo como muerto
                    bulletsToRemove.add(bullet);
                }
            }

            // Colisiones con kamikazes
            for (Kamikaze kamikaze : kamikazes) {
                if (kamikaze.isAlive() &&
                    bullet.getY() < kamikaze.getY() + kamikaze.getHeight() &&
                    bullet.getY() + bullet.getHeight() > kamikaze.getY() &&
                    bullet.getX() < kamikaze.getX() + kamikaze.getWidth() &&
                    bullet.getX() + bullet.getWidth() > kamikaze.getX()) {
                    kamikaze.die(); // Marcar al kamikaze como muerto
                    bulletsToRemove.add(bullet);
                }
            }
            
            // Colisiones con jefe
            if (boss != null && boss.isAlive() && bullet.getY() < boss.getY() + boss.getHeight() &&
                    bullet.getY() + bullet.getHeight() > boss.getY() &&
                    bullet.getX() < boss.getX() + boss.getWidth() &&
                    bullet.getX() + bullet.getWidth() > boss.getX()) {
                
                if (Boss.vidaBoss <= 0) {
                    boss.die();
                }
                System.out.println("COL");
                Boss.vidaBoss--;
            }
        }
        bullets.removeAll(bulletsToRemove);
    }

    private void shoot() {
        // Crear una nueva bala y añadirla a la lista de balas
        BulletPlayer bullet = new BulletPlayer(x + width / 2 - 2, y, 5, 5, 10, Color.green);
        bullets.add(bullet);
        reproducirSonido("mus_sfx_a_bullet.wav");
    }
    
    private void reproducirSonido(String nombreArchivo) {
        try {
            // Ajustar la ruta para que sea relativa a la raíz de la carpeta src
            InputStream inputStream = getClass().getResourceAsStream("/" + nombreArchivo);
            if (inputStream == null) {
                System.err.println("No se pudo encontrar el archivo de sonido: " + nombreArchivo);
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            clip = AudioSystem.getClip(); // Almacenar la referencia del clip
            clip.open(audioInputStream);
            if (Menu_Options.getFlagSonido() == 1) {
                clip.start(); // Reproducir el sonido solo una vez
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al reproducir el sonido.");
        }
    }

    // Getters y setters (opcional si necesitas acceder a estas variables)
    public static int getX() { return x; }
    public static int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getSpeed() { return speed; }
}
