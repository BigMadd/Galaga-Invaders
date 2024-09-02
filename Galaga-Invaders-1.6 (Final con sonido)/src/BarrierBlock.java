import java.awt.Color;
import java.awt.Graphics;

public class BarrierBlock {
    private int x, y;
    private int width, height;

    public BarrierBlock(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, width, height);
    }

    public boolean checkCollision(int bulletX, int bulletY, int bulletWidth, int bulletHeight) {
        return bulletX < x + width &&
               bulletX + bulletWidth > x &&
               bulletY < y + height &&
               bulletY + bulletHeight > y;
    }
}