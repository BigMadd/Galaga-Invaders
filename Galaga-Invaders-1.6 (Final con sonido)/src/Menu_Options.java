import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import java.awt.FontMetrics;


public class Menu_Options extends JFrame implements Runnable, KeyListener {
    private Canvas canvas;
    private Thread thread;
    private boolean running;
    private BufferStrategy bs;
    private Graphics g;
    public static int flagSonido = 0;
    public static int flagBlock = 0;
    public static int flagColor = 0;
    private Font levelFont = new Font("Minecraft", Font.PLAIN, 40);
    private String opcionSonido = "Activar sonido?: NO";
    private String opcionBlock = "Activar barreras?: NO";
    private String opcionColor = "Color del enemigo: ROJO";
    private String[] opciones = {opcionSonido, opcionBlock, opcionColor};
    private int currentSelection = 0;
    private Clip clip; // Referencia al clip de audio
    private Image backgroundImage; // Imagen de fondo

    public Menu_Options() {
        setTitle("Opciones");
        setSize(Window.WIDTH, Window.HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(Window.WIDTH, Window.HEIGHT));
        canvas.setMaximumSize(new Dimension(Window.WIDTH, Window.HEIGHT));
        canvas.setMinimumSize(new Dimension(Window.WIDTH, Window.HEIGHT));
        canvas.setFocusable(true);

        add(canvas);
        canvas.addKeyListener(this);

        try {
            backgroundImage = ImageIO.read(getClass().getResource("/images/universo2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        setVisible(true);
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (running) {
            draw();
    
        }
    }

    private void draw() {
        if (!running) return; // Si no está corriendo, no dibujes

        bs = canvas.getBufferStrategy();
        if (bs == null) {
            canvas.createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();

        // Dibuja la imagen de fondo
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, Window.WIDTH, Window.HEIGHT, null);
        }

        // Dibuja las opciones
        g.setFont(levelFont);
        FontMetrics fm = g.getFontMetrics(levelFont);
        int yOffset = (Window.HEIGHT / 2) - 125; // Ajusta la altura según tus necesidades
        for (int i = 0; i < opciones.length; i++) {
            int textWidth = fm.stringWidth(opciones[i]);
            int xPosition = (Window.WIDTH - textWidth) / 2; // Centra el texto horizontalmente

            if (i == currentSelection) {
                g.setColor(Color.green);
            } else {
                g.setColor(Color.white);
            }
            g.drawString(opciones[i], xPosition, yOffset + (i * 60));
        }

        g.dispose();
        bs.show();
    }

    
    public static int getFlagSonido() {
    	return flagSonido;
    }
    
    public static int getFlagBlock() {
    	return flagBlock;
    }
    
    public static int getFlagColor() {
    	return flagColor;
    }
    
    private void updateOptions() {
    	switch(getFlagSonido()) {
    		case 0:
    			opcionSonido = "Activar sonido?: NO";
    			break;
    		
    		case 1:
    			opcionSonido = "Activar sonido?: SI";
    			break;
    	}
    	
    	switch(getFlagBlock()) {
    		case 0:
    			opcionBlock = "Activar barreras?: NO";
    			break;
    		
    		case 1:
    			opcionBlock = "Activar barreras?: SI";
    			break;
    	}
    	
        switch (getFlagColor()) {
            case 0:
                opcionColor = "Color del enemigo: ROJO";
                break;
            case 1:
                opcionColor = "Color del enemigo: AZUL";
                break;
            case 2:
                opcionColor = "Color del enemigo: VERDE";
                break;
            case 3:
                opcionColor = "Color del enemigo: GRIS";
                break;
            case 4:
                opcionColor = "Color del enemigo: VIOLETA";
                break;
        }
        opciones[0] = opcionSonido;
        opciones[1] = opcionBlock;
        opciones[2] = opcionColor;
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            currentSelection = (currentSelection - 1 + opciones.length) % opciones.length;
            reproducirSonido("snd_select.wav");
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            currentSelection = (currentSelection + 1) % opciones.length;
            reproducirSonido("snd_select.wav");
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (currentSelection == 0 && getFlagSonido() == 1) {
                flagSonido--;
                reproducirSonido("snd_select.wav");
            } else if (currentSelection == 1 && getFlagBlock() == 1) {
                flagBlock--;
                reproducirSonido("snd_select.wav");
            } else if (currentSelection == 2 && getFlagColor() != 0) {
                flagColor--;
                reproducirSonido("snd_select.wav");
            }
            updateOptions();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (currentSelection == 0 && getFlagSonido() < 1) {
                flagSonido++;
                reproducirSonido("snd_select.wav");
            } else if (currentSelection == 1 && getFlagBlock() < 1) {
                flagBlock++;
                reproducirSonido("snd_select.wav");
            } else if (currentSelection == 2 && getFlagColor() < 4) {
                flagColor++;
                reproducirSonido("snd_select.wav");
            }
            updateOptions();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            running = false;
            this.dispose(); // Cerrar el menú
            new Menu_Screen().start(); // Iniciar el juego
        }
    }
    
    private void reproducirSonido(String nombreArchivo) {
        try {
            // Ajustar la ruta para que sea relativa a la raíz de la carpeta src
            InputStream inputStream = getClass().getResourceAsStream("/" + nombreArchivo);
            if (inputStream == null) {
                System.err.println("No se pudo encontrar el archivo de sonido: " + nombreArchivo);
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            clip = AudioSystem.getClip(); // Almacenar la referencia del clip
            clip.open(audioInputStream);
            if (Menu_Options.getFlagSonido() == 1) {
                clip.start(); // Reproducir el sonido solo una vez
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al reproducir el sonido.");
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
}
