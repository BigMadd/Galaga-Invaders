import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Kamikaze {

    private int x, y;
    private int width, height;
    private float speed;
    private Color color;
    
    private boolean isAlive = true; // Estado del enemigo
    private boolean launched;
    private Player player;
    private Image image;
    
    public Kamikaze(int x, int y, int width, int height, float speed, Color color, Player player) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.color = color;        
        this.launched = false;
        this.player = player; 
    
        try {
            image = ImageIO.read(getClass().getResource("/images/kamikaze.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se pudo cargar la imagen del enemigo.");
        }
    }
    
    public void draw(Graphics g) {
        if (isAlive) {
            if (image != null) {
                g.drawImage(image, x, y, width, height, null); // Dibuja la imagen del enemigo
            } else {
                // Si no se carga la imagen, dibujar un rectángulo de color como fallback
                g.setColor(color);
                g.fillRect(x, y, width, height);
            }
        }
    }
    
    public void update() {
        if (!launched || !isAlive) return;

        int playerX = player.getX();
        int playerY = player.getY();

        // Movimiento hacia el jugador con posible movimiento errático
        int deltaX = playerX - x;
        int deltaY = playerY - y;

        // Calcular una probabilidad de movimiento errático basada en la distancia vertical
        double erraticFactor = Math.min(1, (double) Math.abs(deltaY) / 500.0);

        if (Math.random() < erraticFactor) {
            if (Math.random() < 0.5) {
                x += speed * 1.5;
            } else {
                x -= speed * 1.5;
            }
        } else {
            if (x < playerX) {
                x += speed * 2;
            } else if (x > playerX) {
                x -= speed * 2;
            }
        }

        // Movimiento vertical hacia el jugador
        y += speed * 1.5;

        // Verificar colisión con el jugador
        if (checkCollisionWithPlayer()) {
        	System.out.println("soyyyyy");
            Window.vidas--; // Reducir la vida del jugador
            die(); // El kamikaze muere al colisionar
        }

        // Verificar si el kamikaze ha salido de la pantalla
        if (y > 690) {
            die(); // Muere si sale de la pantalla
        }
    }
    
    public void die() {
        isAlive = false; // Marcar al enemigo como muerto
    }
    
    public boolean checkCollisionWithPlayer() {
        // Definir los límites del kamikaze
        int kamikazeLeft = x;
        int kamikazeRight = x + width;
        int kamikazeTop = y;
        int kamikazeBottom = y + height;

        // Definir los límites del jugador
        int playerLeft = player.getX();
        int playerRight = player.getX() + player.getWidth();
        int playerTop = player.getY();
        int playerBottom = player.getY() + player.getHeight();

        // Verificar si los límites del kamikaze se superponen con los límites del jugador
        boolean collisionX = kamikazeRight > playerLeft && kamikazeLeft < playerRight;
        boolean collisionY = kamikazeBottom > playerTop && kamikazeTop < playerBottom;

        return collisionX && collisionY;
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
        
    public void aumentarVelocidad() {
        this.speed *= 1.0234; // Incrementar la velocidad
    }
    
    public void setLaunched(boolean launched) {
        this.launched = launched;
    }
}
