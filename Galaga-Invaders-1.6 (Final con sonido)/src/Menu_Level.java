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

public class Menu_Level extends JFrame implements Runnable, KeyListener {

    private Canvas canvas;
    private Thread thread;
    private boolean running;
    private BufferStrategy bs;
    private Graphics g;
    private Font levelFont = new Font("Minecraft", Font.PLAIN, 40);
    private String[] levels = {"Nivel 1", "Nivel 2", "Nivel 3", "Nivel 4", "Nivel 5"};
    private int currentSelection = 0;
    private static int seleccionDeNivel = 0;
    public static int[] nivelesDisponibles = {1, 0, 0, 0, 0};
    public int seleccionActual;
    
    
    
    private boolean startGame = false;
    private Image backgroundImage; // Imagen de fondo
    private Image lockIcon; // Imagen de icono de bloqueo
    private Clip clip; // Referencia al clip de audio

    public Menu_Level() {
        setTitle("Menu");
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
        
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/images/universo2.jpg"));
            lockIcon = ImageIO.read(getClass().getResource("/images/candado.png")); // Cargar la imagen del icono de bloqueo
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
            if (startGame) {
            	Window.noColisionasMasEnTodaTuPutaVida = 0;
            	Window.vidas = 3;
                if (seleccionDeNivel == 1) {
                    Enemy.levelSelect = 1; // Nivel 1 seleccionado
                } else if (seleccionDeNivel == 2) {
                    Enemy.levelSelect = 2; // Nivel 2 seleccionado
                } else if (seleccionDeNivel == 3) {
                    Enemy.levelSelect = 3; // Nivel 3 seleccionado
                } else if (seleccionDeNivel == 4) {
	                Enemy.levelSelect = 4; // Nivel 4 seleccionado
	            } else if (seleccionDeNivel == 5) {
		            Enemy.levelSelect = 5; // Nivel 5 seleccionado
		        }
                
                new Window().start(); 
                
                this.dispose(); // Cerrar el menú
                stop(); // Asegúrate de detener el hilo antes de cerrar la ventana
                break; // Salir del bucle run
            }
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

        // Dibuja los niveles
        g.setFont(levelFont);
        for (int i = 0; i < levels.length; i++) {
            int yPosition = (Window.HEIGHT / 2) + (i * 60) - 125; // Calcula la posición Y

            if (nivelesDisponibles[i] == 0) {
                g.setColor(Color.gray); // Niveles no disponibles en gris
                if (lockIcon != null) {
                    g.drawImage(lockIcon, (Window.WIDTH / 2) + 70, yPosition - 40, 40, 40, null); // Dibuja el icono de bloqueo
                }
            } else if (i == currentSelection) {
                g.setColor(Color.green); // Nivel seleccionado en verde
            } else {
                g.setColor(Color.white); // Niveles disponibles en blanco
            }

            g.drawString(levels[i], (Window.WIDTH / 2) - 72, yPosition);
        }

        g.dispose();
        bs.show();
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            do {
                currentSelection = (currentSelection - 1 + levels.length) % levels.length;
            } while (nivelesDisponibles[currentSelection] == 0); // Saltar niveles no disponibles
            seleccionActual = currentSelection + 1; // Actualiza seleccionActual
            reproducirSonido("snd_select.wav");
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            do {
                currentSelection = (currentSelection + 1) % levels.length;
            } while (nivelesDisponibles[currentSelection] == 0); // Saltar niveles no disponibles
            seleccionActual = currentSelection + 1; // Actualiza seleccionActual
            reproducirSonido("snd_select.wav");
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (nivelesDisponibles[currentSelection] == 1) {
                seleccionDeNivel = seleccionActual; // Usa seleccionActual para establecer el nivel seleccionado
                startGame = true; // Cambia el estado a iniciar el juego
            }
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
