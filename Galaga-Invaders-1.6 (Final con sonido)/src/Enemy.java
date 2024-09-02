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

public class Enemy {

    public int x, y;
    private int width, height;
    private float speed;
    private Color color;
    public static int levelSelect = 1;
    private List<BulletEnemy> bullets; // array de balas basicamente

    private int direccionDerecha = 1; // Indica la dirección de movimiento
    private int direccionAbajo = 2;
    private boolean isAlive = true; // Variable para el estado del enemigo

    private Image image;
    private Timer shootTimer; // Temporizador para disparar automáticamente
    private Random random; // Generador de números aleatorios
    private Player player; // Referencia al objeto Player existente

    // Modificación: Aceptar un Player existente como parámetro
    public Enemy(int x, int y, int width, int height, float speed, Color color, Player player) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.color = color;
        this.isAlive = true;
        this.bullets = new ArrayList<>();
        this.player = player; // Utilizar la instancia existente de Player
        this.random = new Random(); // Inicializar el generador de números aleatorios

        try {
        	// rojo
        	if(Menu_Options.flagColor == 0) {
                image = ImageIO.read(getClass().getResource("/images/NaveInvasoraRoja.png"));
        	}
        	
        	// azul
        	else if(Menu_Options.flagColor == 1) {
                image = ImageIO.read(getClass().getResource("/images/NaveInvasoraAzul.png"));
        	}
        	
        	// verde
        	else if(Menu_Options.flagColor == 2) {
                image = ImageIO.read(getClass().getResource("/images/NaveInvasoraVerde.png"));
        	}
        	
        	//gris
        	else if(Menu_Options.flagColor == 3) {
                image = ImageIO.read(getClass().getResource("/images/NaveInvasoraGris.png"));
        	}
        	
        	// violeta
        	else if(Menu_Options.flagColor == 4) {
                image = ImageIO.read(getClass().getResource("/images/NaveInvasoraVioleta.png"));
        	}

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se pudo cargar la imagen del enemigo.");
        }

        // Configurar el temporizador para disparar cada 2 segundos con un retraso aleatorio al inicio
        int initialDelay = random.nextInt(2000); // Retraso inicial aleatorio entre 0 y 2000 ms
        shootTimer = new Timer(2000, new ActionListener() {
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
                g.drawImage(image, x, y, width, height, null); // Dibuja la imagen del enemigo

                // Dibujar las balas
                for (BulletEnemy bullet : bullets) {
                    bullet.draw(g);
                }

            } else {
                // Si no se carga la imagen, dibujar un rectángulo de color como fallback
                g.setColor(color);
                g.fillRect(x, y, width, height);
            }
        }
    }

    public void update(List<Enemy> enemies,  List<Barrier> barriers) {
        if (isAlive) {
        	if(direccionDerecha == 1) {
        		x += speed;
        	} else if(direccionDerecha == 0) {
        		x -= speed;
        	}
        	if(direccionAbajo == 1) {
        		y += speed;
        	} else if(direccionAbajo == 0) {
        		y -= speed;
        	}

            // Actualizar todas las balas y eliminar las que están fuera de la pantalla
            List<BulletEnemy> bulletsToRemove = new ArrayList<>();
            for (BulletEnemy bullet : bullets) {
                bullet.update();
                if (bullet.getY() < 0) {
                    bulletsToRemove.add(bullet);
                } else if (checkCollisionWithPlayer(bullet) && Window.noColisionasMasEnTodaTuPutaVida != 1) { // Usar método de colisión mejorado
                    bulletsToRemove.add(bullet);
                    Window.vidas--;
                    // Aquí podrías reducir la vida del jugador o aplicar algún efecto
                } else {
                    for (Barrier barrier : barriers) {
                        if (barrier.checkCollision(bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight())) {
                            bulletsToRemove.add(bullet);
                            break;
                        }
                    }
                }
            }
            bullets.removeAll(bulletsToRemove);
        }
    }

    // Método para comprobar colisión entre una bala y el jugador
    public boolean checkCollisionWithPlayer(BulletEnemy bullet) {
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
        // Crear una nueva bala y añadirla a la lista de balas
        BulletEnemy bullet = new BulletEnemy(x + width / 2 - 2, y + height, 5, 5, 8, Color.RED);
        bullets.add(bullet);
    }

    public void die() {
        isAlive = false; // Marcar al enemigo como muerto
        shootTimer.stop(); // Detener el temporizador cuando el enemigo muera
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
    	if(direccionDerecha == 1) {
    		direccionDerecha = 0;
    	} else {
    		direccionDerecha = 1;
    	}
    }
    
    public void cambiarDireccionReloj(int num) {
    	if(num == 1) {
    		direccionAbajo = 1;
    		direccionDerecha = 2;
    	} else if(num == 2) {
            direccionDerecha = 0;    	
            direccionAbajo = 2;
    	} else if(num == 3) {
    		direccionAbajo = 0;
    		direccionDerecha = 2;
    		x += 1;
    	} else if(num == 4) {
            direccionDerecha = 1;
            direccionAbajo = 2;
    	}
    }

    public void descender() {
        y += 10;
    }

    public void aumentarVelocidad() {
        this.speed *= 1.0234;
    }
}
