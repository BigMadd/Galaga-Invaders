package com.dregronprogram.levels;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import com.dregronprogram.enemy_types.EnemyType;
import com.dregronprogram.enemy_types.EnemyTypeKamikaze;
import com.dregronprogram.enemy_types.EnemyTypeBasic;
import com.dregronprogram.game_screen.BasicBlocks;
import com.dregronprogram.game_screen.Player;
import com.dregronprogram.handler.EnemyBulletHandler;
import com.dregronprogram.sound.Sound;
import com.dregronprogram.timer.Timer;

public class Level3 implements SuperLevel {

    private Player player;
    private ArrayList<EnemyType> enemies = new ArrayList<>();
    private EnemyBulletHandler bulletHandler;
    
    private Sound beep, boop;
    private boolean beepboop;
    
    private Timer kamikazeTimer;
    private Random random;
    
    public Level3(Player player, EnemyBulletHandler bulletHandler) {
        this.player = player;
        this.bulletHandler = bulletHandler;
        addEnemies();
        
        beep = new Sound("/com/dregronprogram/sounds/beep.wav");
        boop = new Sound("/com/dregronprogram/sounds/boop.wav");
        
        kamikazeTimer = new Timer();
        random = new Random();
    }

    @Override
    public void draw(Graphics2D g) {
        if (enemies == null)
            return;
        
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).draw(g);
        }
        bulletHandler.draw(g);
    }

    @Override
    public void update(double delta, BasicBlocks blocks) {
        if (enemies == null)
            return;

        // Lanzamiento de naves kamikazes de forma secuencial
        if (kamikazeTimer.timerEvent(2000 + random.nextInt(2000))) {  // Entre 2 a 4 segundos de intervalo
            launchNextKamikaze();
        }
        
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).update(delta, player, blocks);
        }
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).collide(i, player, blocks, enemies);
        }
        hasDirectionChange(delta);
        bulletHandler.update(delta, blocks, player);
    }

    @Override
    public void hasDirectionChange(double delta) {
        if (enemies == null)
            return;
        
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).isOutOfBounds()) {
                changeDirectionAllEnemies(delta);
            }
        }
    }

    @Override
    public void changeDirectionAllEnemies(double delta) {
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).changeDirection(delta);
        }
        if (beepboop) {
            beepboop = false;
            boop.play();
        } else {
            beepboop = true;
            beep.play();
        }
    }

    @Override
    public boolean isGameOver() {
        return player.getHealth() <= 0;
    }

    @Override
    public void reset() {
        player.reset();
        enemies.clear();
        addEnemies();
        
        bulletHandler.reset();
        kamikazeTimer = new Timer();
    }
    
    public void addEnemies() {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 10; x++) {
                EnemyType e = new EnemyTypeBasic(150 + (x * 40), 25 + (y * 40), 1 , 3, bulletHandler);
                enemies.add(e);
            }
        }
    }

    @Override
    public boolean isComplete() {
        return enemies.isEmpty();
    }

    @Override
    public void destroy() {
        // Aquí puedes agregar lógica para destruir recursos si es necesario.
    }
    
    private void launchNextKamikaze() {
        int x = 150 + random.nextInt(10) * 40;
        int y = 25 + random.nextInt(5) * 40;
        EnemyTypeKamikaze kamikaze = new EnemyTypeKamikaze(x, y, 1, 3, bulletHandler);
        kamikaze.setLaunched(true);
        enemies.add(kamikaze);
    }

	@Override
	public void changeDurectionAllEnemys(double delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destory() {
		// TODO Auto-generated method stub
		
	}
}
