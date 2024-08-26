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

public class LevelSelectScreen extends SuperStateMachine implements KeyListener {

    private Font levelFont = new Font("Minecraft", Font.PLAIN, 40);
    private String[] levels = {"Nivel 1", "Nivel 2", "Nivel 3"};
    private int currentSelection = 0;
    private static int seleccionDeNivel = 0;

    private Image backgroundImage;

    public LevelSelectScreen(StateMachine stateMachine) {
        super(stateMachine);
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/com/dregronprogram/images/universo2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
            // Manejo de error si no se puede cargar la imagen
        }
    }

    @Override
    public void update(double delta) {
        // Aquí puedes agregar alguna lógica de actualización si es necesario
    }

    @Override
    public void draw(Graphics2D g) {
        // Dibuja la imagen de fondo
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, Display.WIDTH, Display.HEIGHT, null);
        }

        // Dibuja los niveles
        g.setFont(levelFont);
        g.setColor(Color.white);
        for (int i = 0; i < levels.length; i++) {
            if (i == currentSelection) {
                g.setColor(Color.green);
            } else {
                g.setColor(Color.white);
            }
            g.drawString(levels[i], (Display.WIDTH / 2) - 52, (Display.HEIGHT / 2) + (i * 50)-40);
        }
    }

    @Override
    public void init(Canvas canvas) {
        canvas.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            currentSelection = (currentSelection - 1 + levels.length) % levels.length;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            currentSelection = (currentSelection + 1) % levels.length;
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            switch (currentSelection) {
                case 0: // Nivel 1

                    seleccionDeNivel = 1;
                    break;
                case 1: // Nivel 2

                    seleccionDeNivel = 2;
                    break;
                case 2: // Nivel 3
                	seleccionDeNivel = 3;
                    break;
            }
            getStateMachine().setState((byte) (currentSelection + 2)); // Cambia el estado según el nivel seleccionado
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No es necesario hacer nada aquí por ahora
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No es necesario hacer nada aquí por ahora
    }
    
    public static int getSeleccionDeNivel() {
        return seleccionDeNivel;
    }
}
