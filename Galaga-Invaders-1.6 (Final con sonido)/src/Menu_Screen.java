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

public class Menu_Screen extends JFrame implements Runnable, KeyListener {

    private Canvas canvas;
    private Thread thread;
    private boolean running;
    private BufferStrategy bs;
    private Graphics g;
    
    private boolean startGame = false;
    private boolean startOptions = false;
    private boolean showStartText = true;
    private long lastBlinkTime = 0;
    private long blinkInterval = 350;
    
    private Font tittleFont = new Font("Minecraft", Font.PLAIN, 60);
    private Font startFont = new Font("Minecraft", Font.PLAIN, 40);
    private String tittle = "Galaga Invaders";
    private String start = "Enter para jugar";
    private String options = "Espacio para opciones";
    private Image backgroundImage; 
    private Clip clip; // Referencia al clip de audio
    
    public Menu_Screen() {
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

        // INTENTA CARGAR LA IMAGEN DEL FONDO
        try {
            // Asegúrate de que la ruta sea correcta. Si la imagen está en una carpeta 'resources', usa "/universo2.jpg"
            backgroundImage = ImageIO.read(getClass().getResource("/images/universo2.jpg"));
        } catch (IOException e) {
            e.printStackTrace(); // Imprime el error en caso de que no se pueda cargar la imagen
        }
        
        setVisible(true);
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public void stop() {
        try {
            thread.join();
            running = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (running) {
            draw();
            update();
            if (startGame) {
                running = false;
                this.dispose(); // Cerrar el menú
                new Menu_Level().start(); // Iniciar el juego
            }
            
            if(startOptions) {
            	running = false;
            	this.dispose();
            	new Menu_Options().start();
            }
        }
    }

    private void update() {
        // Controla el parpadeo del texto "Press Enter"
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBlinkTime >= blinkInterval) {
            showStartText = !showStartText; // Cambia la visibilidad
            lastBlinkTime = currentTime; // Actualiza el tiempo de la última actualización
        }
    }
    
    private void draw() {
        bs = canvas.getBufferStrategy();
        if (bs == null) {
            canvas.createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();
        
        if (backgroundImage != null) {
            // Dibuja la imagen de fondo
            g.drawImage(backgroundImage, 0, 0, Window.WIDTH, Window.HEIGHT, null);
        }
        
        // Dibujar el título
        g.setFont(tittleFont);
        int tittleWidth = g.getFontMetrics().stringWidth(tittle);
        g.setColor(Color.yellow);
        g.drawString(tittle, ((Window.WIDTH/2)-(tittleWidth/2))-2, (Window.HEIGHT/2)-123);
        g.setColor(Color.green);
        g.drawString(tittle, (Window.WIDTH/2)-(tittleWidth/2), (Window.HEIGHT/2)-125);
        
        g.setFont(startFont);
        g.setColor(Color.white);
        int optionsWidth = g.getFontMetrics().stringWidth(options);
        g.drawString(options, (Window.WIDTH/2)-(optionsWidth/2)+15, (Window.HEIGHT/2)+150);


        // Dibujar el texto "Press Enter" solo si showStartText es verdadero
        if (showStartText) {
            g.setFont(startFont);
            g.setColor(Color.white);
            int startWidth = g.getFontMetrics().stringWidth(start);
            g.drawString(start, (Window.WIDTH/2)-(startWidth/2)+15, (Window.HEIGHT/2)+75);
        }
        
        g.dispose();
        bs.show();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            startGame = true; // Cambia el estado a iniciar el juego
            reproducirSonido("snd_select.wav");
        } else if(key == KeyEvent.VK_SPACE) {
        	startOptions = true;
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
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}
