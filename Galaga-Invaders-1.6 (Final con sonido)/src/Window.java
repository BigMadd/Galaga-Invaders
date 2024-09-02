import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.Timer;

public class Window extends JFrame implements Runnable, KeyListener {

    public static int WIDTH = 700, HEIGHT = 800;
    private Canvas canvas;
    private Thread thread;
    private boolean running;
    private boolean windowClosed = false; // NUEVO CAMPO PARA SEGUIMIENTO DEL ESTADO DE LA VENTANA

    private BufferStrategy bs;
    private Graphics g;

    private final int FPS = 60;
    private double TARGETTIME = 1000000000 / FPS;
    private double delta = 0;
    private int AVERAGEFPS = FPS;

    private Player player;
    private List<Enemy> enemies; // Lista de enemigos
    private List<Kamikaze> kamikazes; // Lista de kamikazes
    private int kamikazeLaunchIndex = 0;
    private int kamikazeLaunchDelay = 100; // Delay entre lanzamientos
    private int kamikazeCounter = 0;
    private List<Barrier> barriers;
    private Clip clip; // Referencia al clip de audio
    
    private BufferedImage backgroundImage; // fondo basicamente

    int cantidadDeEnemigos;

    // Variables para el control del jugador
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean space = false;
    // variable control de niveles
    public int levelSelect = 1;
    public int score = 0;
    public int nivelCompletado;
    public int perder = 0;
    public static int vidas = 3;
    public int ganar = 0;
    public static int noColisionasMasEnTodaTuPutaVida = 0;
    private Timer levelCompleteTimer; // CAMBIADO A TIMER A NIVEL DE CLASE
    Font font = new Font("Minecraft", Font.PLAIN, 20);
    Font fontLargo = new Font("Minecraft", Font.PLAIN, 40);
    private BufferedImage lifeImage;
    private Boss boss; // Variable para el jefe
    public int DetenerMusica = 0;
    
    
    public Window() {
        setTitle("Galaga Invaders");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        canvas.setMaximumSize(new Dimension(WIDTH, HEIGHT));
        canvas.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        canvas.setFocusable(true);

        add(canvas);
        canvas.addKeyListener(this); // Añadir el KeyListener al canvas

        try {
            lifeImage = ImageIO.read(getClass().getResource("/images/cora_verde.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se pudo cargar la imagen de vida.");
        }

        player = new Player(WIDTH / 2 - 50, HEIGHT - 135, 60, 60, 6, Color.blue);

        enemies = new ArrayList<>();
        kamikazes = new ArrayList<>();  // Inicializa la lista de kamikazes

        int y = 0, z = 0;
        for (int i = 0; i < 30; i++) {
            if (i < 10) {
                enemies.add(new Enemy(i * 50, 120, 30, 30, 2, Color.RED, player));
            } else if (i < 20) {
                enemies.add(new Enemy(y * 50, 170, 30, 30, 2, Color.RED, player));
                y++;
            } else if (i < 30) {
                enemies.add(new Enemy(z * 50, 220, 30, 30, 2, Color.RED, player));
                z++;
            }
        }
        cantidadDeEnemigos = enemies.size();
        if(Menu_Options.flagBlock != 1) {
            barriers = new ArrayList<>();
            barriers.add(new Barrier(WIDTH/3-175, (HEIGHT-HEIGHT/3), 0, 0));
            barriers.add(new Barrier((WIDTH/2)-60, (HEIGHT-HEIGHT/3), 0, 0));
            barriers.add(new Barrier((WIDTH/3-60)*3, (HEIGHT-HEIGHT/3), 0, 0));
        }
        else {
            barriers = new ArrayList<>();
            barriers.add(new Barrier(WIDTH/3-175, (HEIGHT-HEIGHT/3), 100, 30));
            barriers.add(new Barrier((WIDTH/2)-60, (HEIGHT-HEIGHT/3), 100, 30));
            barriers.add(new Barrier((WIDTH/3-60)*3, (HEIGHT-HEIGHT/3), 100, 30));
        }

        
        
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/images/fondo.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se pudo cargar la imagen de fondo.");
        }
        reproducirSonido("MusicaOptima.wav");


        
    }


    private void update() {
    	
    	if(vidas == 0) {
    		if(perder != 2) {
        		perder = 1;
    		}

    		noColisionasMasEnTodaTuPutaVida = 1; 
    	}
    	
    	if(cantidadDeEnemigos == 0 && ganar != 2) {
    		ganar = 5;
    	}
    	
      
            player.update(leftPressed, rightPressed, space, enemies, kamikazes);
 
            for (Enemy enemy : enemies) {
                enemy.update(enemies, barriers);
            }
            
            // Eliminar enemigos que hayan sido impactados por balas
            for (int i = enemies.size() - 1; i >= 0; i--) {
                Enemy enemy = enemies.get(i);
                if (!enemy.isAlive()) {
                    enemies.remove(i);
                    score += 10;
                    cantidadDeEnemigos--;
                }
            }

            // Chequear colisiones y cambiar dirección si es necesario
            for (Enemy enemy : enemies) {
            	if(Enemy.levelSelect == 4) { 
                    if (enemy.getX() + enemy.getWidth() >= Window.WIDTH - 15) {
                        todosCambian(1); // todos cambian de dirección y descienden
                        enemy.x -= 1;

                        // Aumentar la velocidad de todos los enemigos
                        /*for (Enemy e : enemies) {
                            e.aumentarVelocidad();
                        }*/

                        break;
                    } else if(enemy.getX() <= 0) {
                    	todosCambian(3);
                    	enemy.x += 1;
                    	break;
                    } else if(enemy.getY() + enemy.getHeight() >= Window.HEIGHT - 290) {
                    	todosCambian(2);
                    	enemy.y -= 1;
                    	break;
                    } else if(enemy.getY() <= 120) {
                    	todosCambian(4);
                    	enemy.y += 1;
                    	break;
                    } 
            	} else {
            		if(enemy.getX() + enemy.getWidth() >= Window.WIDTH - 15 || enemy.getX() <= 0) {
                        todosCaen(); // todos cambian de dirección y descienden

                        // Aumentar la velocidad de todos los enemigos
                        for (Enemy e : enemies) {
                            e.aumentarVelocidad();
                        }

                        break;
                    }
            		}
            }
            
            // esto actualiza las barreras a tiempo real papu
            if(Menu_Options.flagBlock == 1) {
	            Iterator<Barrier> barrierIterator = barriers.iterator();
	            while (barrierIterator.hasNext()) {
	                Barrier barrier = barrierIterator.next();
	                if (barrier.isDestroyed()) {
	                    barrierIterator.remove();
	                }
	            }	
            }

        
            if (cantidadDeEnemigos == 0 && ganar == 5) {
                // Comprobar si es el nivel donde se debe aparecer el jefe (nivel 5 en este caso)
                if (Enemy.levelSelect == 5 && boss == null) {
                    boss = new Boss(WIDTH / 2 - 50, 50, 100, 100, 3, Color.RED, player);
                    player.setBoss(boss); // Asegúrate de establecer la referencia del jefe en el jugador
     
                }

                // Si ya se venció al jefe o no hay jefe, se gana el nivel
                if (boss == null || !boss.isAlive()) {
                    ganar = 1;
                }
            }

            // Actualizar el jefe si está presente
            if (boss != null && boss.isAlive()) {
                boss.update(enemies, barriers);
                if (!boss.isAlive()) {
                    score += 50; // O cualquier puntuación que desees otorgar por derrotar al jefe
                    boss = null; // Elimina al jefe después de que sea derrotado
                    ganar = 1; // Marcar nivel como ganado
                }
            }
            
            
        // En el caso de que ya no queden más enemigos de la lista que se termine el juego
            if (cantidadDeEnemigos == 0 && ganar == 1) {
                ganar++;
                System.out.println("OH NOOOO");
                // Detener el audio de fondo
                if (clip != null && clip.isRunning()) {
                    clip.stop(); // Detener la música
                }
                
                if (Enemy.levelSelect == 1) {
                    Menu_Level.nivelesDisponibles[1] = 1;
                }
        	
        	else if(Enemy.levelSelect == 2) {
        		Menu_Level.nivelesDisponibles[2] = 1;
        	}
        	
        	else if(Enemy.levelSelect == 3) {
        		Menu_Level.nivelesDisponibles[3] = 1;
        	}
        	
        	else if(Enemy.levelSelect == 4) {
        		Menu_Level.nivelesDisponibles[4] = 1;
        	}
            if (levelCompleteTimer == null || !levelCompleteTimer.isRunning()) {
                nivelCompletado = 1;
                cantidadDeEnemigos = 30; // Reinicia la cantidad de enemigos si es necesario

                // Inicializa el Timer para esperar 3 segundos antes de cerrar la ventana
                levelCompleteTimer = new Timer(1500, e -> {
                    // DETENER EL TIMER ANTES DE CERRAR LA VENTANA
                    if (levelCompleteTimer != null && levelCompleteTimer.isRunning()) {
                        levelCompleteTimer.stop();
                    }
                    // Cerrar la ventana actual
                    windowClosed = true; // MARCAR LA VENTANA COMO CERRADA
                    this.dispose();
               
                    // Iniciar la nueva ventana de menú después de cerrar la actual
                    new Menu_Level().start();
                });
                levelCompleteTimer.setRepeats(false); // Hacer que el Timer se ejecute solo una vez
                levelCompleteTimer.start();
                running = false;
                
            }
        }
            
        else if(DetenerMusica == 1) {
            if (clip != null && clip.isRunning()) {
                clip.stop(); // Detener la música
            }        	
        }
            
           
        
     
        
        // modo kamikaze (solo nivel 3)
        
        if (Enemy.levelSelect == 3 ) {
        	
        	            // Verificar si es necesario crear nuevos kamikazes
            if (kamikazes.size() < 5 && kamikazeLaunchIndex < 5) {
                kamikazeCounter++;
                if (kamikazeCounter >= kamikazeLaunchDelay) {
                    kamikazes.add(new Kamikaze(100 * kamikazeLaunchIndex + 50, -100, 40, 40, 3, Color.RED,player));
                    kamikazeLaunchIndex++;
                    kamikazeCounter = 0; // Reiniciar el contador de tiempo
                }
            }

            // Actualizar kamikazes existentes
            for (int i = kamikazes.size() - 1; i >= 0; i--) {
                Kamikaze kamikaze = kamikazes.get(i);
                kamikaze.setLaunched(true); 
                kamikaze.update();

                // Si el kamikaze alcanza al jugador o sale de la pantalla, eliminarlo
                if (kamikaze.getY() > HEIGHT || !kamikaze.isAlive()) {
                    kamikazes.remove(i);
                }
            }
        }

     
        
        // en el caso de perder entra aca una sola vez
        
        if (vidas == 0 && perder == 1) {
             cantidadDeEnemigos = 30; // Reinicia la cantidad de enemigos si es necesario
            perder++;
            
            // Inicializa el Timer para esperar 3 segundos antes de cerrar la ventana
            levelCompleteTimer = new Timer(1500, e -> {
                // DETENER EL TIMER ANTES DE CERRAR LA VENTANA
                if (levelCompleteTimer != null && levelCompleteTimer.isRunning()) {
                    levelCompleteTimer.stop();
                    
                }
                
                // Cerrar la ventana actual
                windowClosed = true; // MARCAR LA VENTANA COMO CERRADA
                this.dispose();
                   
                // Iniciar la nueva ventana de menú después de cerrar la actual
                new Menu_Level().start();
                
            });
            levelCompleteTimer.setRepeats(false); // Hacer que el Timer se ejecute solo una vez
            levelCompleteTimer.start();
            running = false;
        }
        
        
        
        // para el jefe
        if (Enemy.levelSelect == 5 && boss != null && boss.isAlive()) {
            boss.update(enemies, barriers);
            if (!boss.isAlive()) {
                score += 50; // O cualquier puntuación que desees otorgar por derrotar al jefe
                boss = null; // Elimina al jefe después de que sea derrotado
                ganar = 1; // Marcar nivel como ganado
  
            }
        }

        
    }

    private void draw() {
        if (windowClosed) { // COMPROBAR SI LA VENTANA ESTÁ CERRADA
            return;
        }

        bs = canvas.getBufferStrategy();

        if (bs == null) {
            canvas.createBufferStrategy(3);
            return;
        }

        g = bs.getDrawGraphics();
        
        

        g.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, null);

        // Dibujar al jugador
        player.draw(g);

        // Dibujar enemigos
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
        
        // barreras
        if((Enemy.levelSelect > 1 && Enemy.levelSelect <= 5) && Menu_Options.flagBlock == 1) {
            for (Barrier barrier : barriers) {
                barrier.draw(g);
            }
        }
        
     

        // dibujar kamikaze solo en el nivel 3
        if (Enemy.levelSelect == 3) {
            for (Kamikaze kamikaze : kamikazes) {
                kamikaze.draw(g);
            }
        }

        // dibujar al jefe
        if (Enemy.levelSelect == 5 && boss != null && boss.isAlive()) {
            boss.draw(g);
        }

        

        // puntuación
        g.setFont(font);
        g.setColor(Color.green);
        g.drawString("Puntuacion: " + score, 20, 50); 

        // nivel seleccionado
    
        g.setFont(font);
        g.setColor(Color.white);
        
        if(Enemy.levelSelect == 1) {
        	g.drawString("Nivel 1",WIDTH/2-28, 50);   
        }
        
        else if(Enemy.levelSelect == 2) {
        	g.drawString("Nivel 2",WIDTH/2-28, 50);  
        }
        
        else if(Enemy.levelSelect == 3) {
        	g.drawString("Nivel 3",WIDTH/2-28, 50);  
        }
        
        else if(Enemy.levelSelect == 4) {
        	g.drawString("Nivel 4",WIDTH/2-28, 50);  
        }
        
        else if(Enemy.levelSelect == 5) {
        	g.drawString("Nivel 5",WIDTH/2-28, 50);  
        }
        
        // enemigos restantes
        g.setFont(font);
        g.setColor(Color.green);
        g.drawString("Restantes: " + cantidadDeEnemigos, WIDTH - 165, 50);


        
        
        // dibujar vidas
        if (vidas > 0) {
            int startX = 20; // Posición X inicial para dibujar las vidas
            int startY = 70; // Posición Y para dibujar las vidas
            int lifeWidth = 35; // Ancho de la imagen de vida
            int lifeHeight = 35; // Alto de la imagen de vida
            int spacing = 10; // Espaciado entre las imágenes de vidas

            for (int i = 0; i < vidas; i++) {
                g.drawImage(lifeImage, startX + (i * (lifeWidth + spacing)), startY, lifeWidth, lifeHeight, null);
            }
        }
        
        
        // si perdes muestra este mensaje para aquello la varible de vidas debe ser igual a 0
        if(vidas == 0) {
            g.setFont(fontLargo);
            g.setColor(Color.red);
            g.drawString("GAME OVER", (WIDTH / 2) - 115, HEIGHT / 2);     
            DetenerMusica = 1;
        }
        
        // si gana muestra este mensaje para aquello la variable de nivelCompletado debe ser igual a 1
        
        if(Enemy.levelSelect != 5) {
            if (cantidadDeEnemigos == 0 ) {
                g.setFont(fontLargo);
                g.setColor(Color.blue);
                g.drawString("NIVEL COMPLETADO", (WIDTH / 2) - 215, HEIGHT / 2);
            }        	
        }
        
        else {
            if (cantidadDeEnemigos == 0 && ganar == 1) {
                g.setFont(fontLargo);
                g.setColor(Color.blue);
                g.drawString("NIVEL COMPLETADO", (WIDTH / 2) - 215, HEIGHT / 2);
            }           	
        }


        g.dispose();
        bs.show();
    }

    @Override
    public void run() {
        long now = 0;
        long lastTime = System.nanoTime();
        int frames = 0;
        long time = 0;

        while (running) {
            now = System.nanoTime();
            delta += (now - lastTime) / TARGETTIME;
            time += (now - lastTime);
            lastTime = now;

            if (delta >= 1) {
                update();
                draw();
                delta--;
                frames++;
            }

            if (time >= 1000000000) {
                AVERAGEFPS = frames;
                frames = 0;
                time = 0;
            }
        }

        stop();
    }

    public void start() {
        thread = new Thread(this);
        thread.start();

        running = true;
    }

    private void stop() {
        try {
            thread.join();
            running = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
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
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al reproducir el sonido.");
        }
    }




    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if (key == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }

        if (key == KeyEvent.VK_SPACE) {
            space = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (key == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }

        if (key == KeyEvent.VK_SPACE) {
            space = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public void todosCaen() {
        // Cambiar dirección y hacer descender a todos los enemigos
        for (Enemy enemy : enemies) {
            enemy.cambiarDireccion(true);
            enemy.descender(); // Nuevo método para hacer que todos desciendan
        }
    }
    
    public void todosCambian(int num) {
        for (Enemy enemy : enemies) {
            enemy.cambiarDireccionReloj(num);
            //enemy.descender(); // Nuevo método para hacer que todos desciendan
        }
    }
}
