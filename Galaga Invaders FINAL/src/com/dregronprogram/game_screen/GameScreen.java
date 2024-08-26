package com.dregronprogram.game_screen;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import com.dregronprogram.display.Display;
import com.dregronprogram.handler.EnemyBulletHandler;
import com.dregronprogram.levels.Level1;
import com.dregronprogram.levels.Level2;
import com.dregronprogram.levels.Level3;
import com.dregronprogram.menu_screen.LevelSelectScreen;
import com.dregronprogram.state.StateMachine;
import com.dregronprogram.state.SuperStateMachine;
import com.dregronprogram.timer.TickTimer;

public class GameScreen extends SuperStateMachine {
	
	private Player player;
	private BasicBlocks blocks;
	private Level1 level;
	private Level2 level2;
	private Level3 level3;
	private int nivelPermitido = 2;
	private EnemyBulletHandler bulletHandler;
	
	public static int SCORE = 0;
	
	private Font gameScreen = new Font("Arial", Font.PLAIN, 48);
	private TickTimer gameOverTimer = new TickTimer(180);
	private TickTimer completeTimer = new TickTimer(180);
	
	public GameScreen(StateMachine stateMachine){
		super(stateMachine);
		blocks = new BasicBlocks();
		bulletHandler = new EnemyBulletHandler();
		player = new Player(Display.WIDTH/2-50, Display.HEIGHT-75, 50, 50, blocks);
		level = new Level1(player, bulletHandler);	
		level2 = new Level2(player, bulletHandler);
		level3 = new Level3(player, bulletHandler);
	}
	
	@Override
	public void update(double delta) {
		nivelPermitido = LevelSelectScreen.getSeleccionDeNivel();
		player.update(delta);

		/* nivel 1 */
		if(nivelPermitido == 1) {
			level.update(delta, blocks);
			if (level.isGameOver()) {
				gameOverTimer.tick(delta);
				if (gameOverTimer.isEventReady()) {
					level.reset();
					blocks.reset();
					getStateMachine().setState((byte) 0); // Regresa al menú de niveles
					SCORE = 0;
				}
			}
			
			if (level.isComplete()) {
				completeTimer.tick(delta);
				if (completeTimer.isEventReady()) {
					level.reset();
					blocks.reset();
					getStateMachine().setState((byte) 0); // Regresa al menú de niveles
				}
			}			
		}
		
		/* nivel 2 */
		if(nivelPermitido == 2) {
			level2.update(delta, blocks);
			if (level2.isGameOver()) {
				gameOverTimer.tick(delta);
				if (gameOverTimer.isEventReady()) {
					level2.reset();
					blocks.reset();
					getStateMachine().setState((byte) 0); // Regresa al menú de niveles
					SCORE = 0;
				}
			}
			
			if (level2.isComplete()) {
				completeTimer.tick(delta);
				if (completeTimer.isEventReady()) {
					level2.reset();
					blocks.reset();
					getStateMachine().setState((byte) 0); // Regresa al menú de niveles
				}
			}			
		}		
		
		// nivel 3
		if(nivelPermitido == 3) {
			level3.update(delta, blocks);
			if (level3.isGameOver()) {
				gameOverTimer.tick(delta);
				if (gameOverTimer.isEventReady()) {
					level3.reset();
					blocks.reset();
					getStateMachine().setState((byte) 0); // Regresa al menú de niveles
					SCORE = 0;
				}
			}
			
			if (level3.isComplete()) {
				completeTimer.tick(delta);
				if (completeTimer.isEventReady()) {
					level3.reset();
					blocks.reset();
					getStateMachine().setState((byte) 0); // Regresa al menú de niveles
				}
			}			
		}
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.white);
		g.drawString("Puntaje: " + SCORE, 5, 15);
		
		g.setColor(Color.red);
		g.drawString("Vidas: " + player.getHealth(), 5, 35);
		
		blocks.draw(g);
		player.draw(g);
		
		if(nivelPermitido == 1) {
			level.draw(g);
			
			if (level.isGameOver()) {
				g.setColor(Color.red);
				g.setFont(gameScreen);
				String gameOver = "GAME OVER!";
				int gameOverWidth = g.getFontMetrics().stringWidth(gameOver);
				g.drawString(gameOver, (Display.WIDTH/2)-(gameOverWidth/2), Display.HEIGHT/2);
			}
			
			if (level.isComplete()) {
				g.setColor(Color.green);
				g.setFont(gameScreen);
				String complete = "LEVEL COMPLETE!";
				int completeWidth = g.getFontMetrics().stringWidth(complete);
				g.drawString(complete, (Display.WIDTH/2)-(completeWidth/2), Display.HEIGHT/2);
			}
		}
		
		else if(nivelPermitido == 2) {
			level2.draw(g);
			
			if (level2.isGameOver()) {
				g.setColor(Color.red);
				g.setFont(gameScreen);
				String gameOver = "GAME OVER!";
				int gameOverWidth = g.getFontMetrics().stringWidth(gameOver);
				g.drawString(gameOver, (Display.WIDTH/2)-(gameOverWidth/2), Display.HEIGHT/2);
			}
			
			if (level2.isComplete()) {
				g.setColor(Color.green);
				g.setFont(gameScreen);
				String complete = "LEVEL COMPLETE!";
				int completeWidth = g.getFontMetrics().stringWidth(complete);
				g.drawString(complete, (Display.WIDTH/2)-(completeWidth/2), Display.HEIGHT/2);
			}			
		}
		
		else if(nivelPermitido == 3) {
			level3.draw(g);
			
			if (level3.isGameOver()) {
				g.setColor(Color.red);
				g.setFont(gameScreen);
				String gameOver = "GAME OVER!";
				int gameOverWidth = g.getFontMetrics().stringWidth(gameOver);
				g.drawString(gameOver, (Display.WIDTH/2)-(gameOverWidth/2), Display.HEIGHT/2);
			}
			
			if (level3.isComplete()) {
				g.setColor(Color.green);
				g.setFont(gameScreen);
				String complete = "LEVEL COMPLETE!";
				int completeWidth = g.getFontMetrics().stringWidth(complete);
				g.drawString(complete, (Display.WIDTH/2)-(completeWidth/2), Display.HEIGHT/2);
			}			
		}
	}

	@Override
	public void init(Canvas canvas) {
		canvas.addKeyListener(player);
	}
}
