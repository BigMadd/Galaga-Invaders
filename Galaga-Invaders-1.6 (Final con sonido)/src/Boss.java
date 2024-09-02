import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Boss {
    public int x, y;
    private int width, height;
    private float speed;
    private Color color;
    public static int levelSelect = 5;
    private List<BulletBoss> bullets; // Lista de balas del jefe

    private int direccionDerecha = 1; // Indica la dirección de movimiento
    private boolean isAlive = true; // Variable para el estado del jefe

    private Image image;
    private Timer shootTimer; // Temporizador para disparar automáticamente
    private Random random; // Generador de números aleatorios
    private Player player;
    public static int vidaBoss = 400;

    // Constructor que acepta un objeto Player existente
    public Boss(int x, int y, int width, int height, float speed, Color color, Player player) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.color = color;
        this.isAlive = true;
        this.bullets = new ArrayList<>();
        this.random = new Random(); // Inicializar el generador de números aleatorios
        this.player = player; // Inicializar la referencia al jugador

        try {
            image = ImageIO.read(getClass().getResource("/images/NaveInvasoraRoja.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se pudo cargar la imagen del jefe.");
        }

        // Configurar el temporizador para disparar cada 2 segundos con un retraso aleatorio al inicio
        int initialDelay = random.nextInt(2000); // Retraso inicial aleatorio entre 0 y 2000 ms
        shootTimer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isAlive && random.nextDouble() < 0.7 && (levelSelect > 1 && levelSelect <= 5)) { // 70% de probabilidad de disparar
                    shoot();
                }
            }
        });
        shootTimer.setInitialDelay(initialDelay); // Establecer el retraso inicial aleatorio
        shootTimer.start(); // Iniciar el temporizador
    }


    public void draw(Graphics g) {
        if (isAlive) {
            if (image != null) {
                g.drawImage(image, x, y, width, height, null); // Dibuja la imagen del jefe

                // Dibujar las balas
                for (BulletBoss bulletBoss : bullets) {
                    bulletBoss.draw(g);
                }

            } else {
                // Si no se carga la imagen, dibujar un rectángulo de color como fallback
                g.setColor(color);
                g.fillRect(x, y, width, height);
            }
        }
    }

    public void update(List<Enemy> enemies, List<Barrier> barriers) {
        if (isAlive) {
            // Verificar colisión con los bordes de la pantalla
            if (x + width >= Window.WIDTH) {
                direccionDerecha = 0; // Cambiar dirección hacia la izquierda
                y += 10;
            } else if (x <= 0) {
                direccionDerecha = 1; // Cambiar dirección hacia la derecha
                y += 10;
            }

            // Actualizar posición según la dirección actual
            if (direccionDerecha == 1) {
                x += speed;
            } else if (direccionDerecha == 0) {
                x -= speed;
            }

            // Actualizar todas las balas y eliminar las que están fuera de la pantalla
            List<BulletBoss> bulletsToRemove = new ArrayList<>();
            for (BulletBoss bulletBoss : bullets) {
                bulletBoss.update();
                for (BulletBoss.Bullet bullet : bulletBoss.bullets) {
                    if (bullet.getY() < 0) {
                        bulletsToRemove.add(bulletBoss);
                        break;
                    } else if (checkCollisionWithPlayer(bullet) && Window.noColisionasMasEnTodaTuPutaVida != 1) { // Usar método de colisión mejorado
                        bulletsToRemove.add(bulletBoss);
                        Window.vidas--;
                        break;
                    } else {
                        for (Barrier barrier : barriers) {
                            if (barrier.checkCollision(bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight())) {
                                bulletsToRemove.add(bulletBoss);
                                break;
                            }
                        }
                    }
                }
            }
            bullets.removeAll(bulletsToRemove);
        }
    }

    // Método para comprobar colisión entre una bala y el jugador
    public boolean checkCollisionWithPlayer(BulletBoss.Bullet bullet) {
        // Definir los límites de la bala
        int bulletLeft = bullet.getX();
        int bulletRight = bullet.getX() + bullet.getWidth();
        int bulletTop = bullet.getY();
        int bulletBottom = bullet.getY() + bullet.getHeight();

        // Definir los límites del jugador
        int playerLeft = player.getX();
        int playerRight = player.getX() + player.getWidth();
        int playerTop = player.getY();
        int playerBottom = player.getY() + player.getHeight();

        // Verificar si los límites de la bala se superponen con los límites del jugador
        boolean collisionX = bulletRight > playerLeft && bulletLeft < playerRight;
        boolean collisionY = bulletBottom > playerTop && bulletTop < playerBottom;

        return collisionX && collisionY;
    }

    private void shoot() {
        // Crear un nuevo conjunto de balas que disparan en tres direcciones
        BulletBoss bulletTriple = new BulletBoss(x + width / 2 - 2, y + height, 10, 10, 8, Color.RED);
        bullets.add(bulletTriple);
    }

    public void die() {
        isAlive = false; // Marcar al jefe como muerto
        shootTimer.stop(); // Detener el temporizador cuando el jefe muera
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void cambiarDireccion(boolean aux) {
        if (direccionDerecha == 1) {
            direccionDerecha = 0;
        } else {
            direccionDerecha = 1;
        }
    }

    public void aumentarVelocidad() {
        this.speed *= 1.0234;
    }
}
