import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class Barrier {
    private int x, y;
    private int width, height;
    private List<BarrierBlock> blocks;

    public Barrier(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.blocks = new ArrayList<>();

        int blockSize = 7; // Tamaño de cada bloque pequeño
        for (int bx = 0; bx < width; bx += blockSize) {
            for (int by = 0; by < height; by += blockSize) {
                blocks.add(new BarrierBlock(x + bx, y + by, blockSize, blockSize));
            }
        }
    }

    public void draw(Graphics g) {
        for (BarrierBlock block : blocks) {
            block.draw(g);
        }
    }

    public boolean checkCollision(int bulletX, int bulletY, int bulletWidth, int bulletHeight) {
        for (int i = blocks.size() - 1; i >= 0; i--) {
            BarrierBlock block = blocks.get(i);
            if (block.checkCollision(bulletX, bulletY, bulletWidth, bulletHeight)) {
                blocks.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean isDestroyed() {
        return blocks.isEmpty();
    }
}