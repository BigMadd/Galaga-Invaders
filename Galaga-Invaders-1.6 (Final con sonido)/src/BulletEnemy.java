import java.awt.Color;
import java.awt.Graphics;

public class BulletEnemy {
    private int x, y;
    private int width, height;
    private int speed;
    private Color color;

    public BulletEnemy(int x, int y, int width, int height, int speed, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.color = color;
    }

    public void update() {
        y += speed; // La bala se mueve hacia abajo
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
		return width;
	}
}
