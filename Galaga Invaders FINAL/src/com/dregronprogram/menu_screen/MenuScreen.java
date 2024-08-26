package com.dregronprogram.menu_screen;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.dregronprogram.display.Display;
import com.dregronprogram.state.StateMachine;
import com.dregronprogram.state.SuperStateMachine;

public class MenuScreen extends SuperStateMachine implements KeyListener {

    private Font tittleFont = new Font("Minecraft", Font.PLAIN, 70);
    private Font startFont = new Font("Minecraft", Font.PLAIN, 40);
    private String tittle = "Galaga Invaders";
    private String start = "Press Enter";
    private Image backgroundImage;  // Declarar la variable para la imagen de fondo
    private boolean showStartText = true; // Controla si se muestra "Press Enter"
    private long lastBlinkTime = 0; // Tiempo de la última actualización del parpadeo
    private long blinkInterval = 350; // Intervalo de parpadeo en milisegundos (500 ms = 0.5 segundos)

    public MenuScreen(StateMachine stateMachine) {
        super(stateMachine);
        try {
            backgroundImage = ImageIO.read(getClass().getResource("universo2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(double delta) {
        // Controla el parpadeo del texto "Press Enter"
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBlinkTime >= blinkInterval) {
            showStartText = !showStartText; // Cambia la visibilidad
            lastBlinkTime = currentTime; // Actualiza el tiempo de la última actualización
        }
    }

    @Override
    public void draw(Graphics2D g) {
        // Dibujar la imagen de fondo
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, Display.WIDTH, Display.HEIGHT, null);
        }

        // Dibujar el título
        g.setFont(tittleFont);
        int tittleWidth = g.getFontMetrics().stringWidth(tittle);
        g.setColor(Color.yellow);
        g.drawString(tittle, ((Display.WIDTH/2)-(tittleWidth/2))-2, (Display.HEIGHT/2)-123);
        g.setColor(Color.green);
        g.drawString(tittle, (Display.WIDTH/2)-(tittleWidth/2), (Display.HEIGHT/2)-125);

        // Dibujar el texto "Press Enter" solo si showStartText es verdadero
        if (showStartText) {
            g.setFont(startFont);
            g.setColor(Color.white);
            int startWidth = g.getFontMetrics().stringWidth(start);
            g.drawString(start, (Display.WIDTH/2)-(startWidth/2)+15, (Display.HEIGHT/2)+75);
        }
    }

    @Override
    public void init(Canvas canvas) {
        canvas.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            getStateMachine().setState((byte) 1);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }
}
