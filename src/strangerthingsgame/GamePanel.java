package strangerthingsgame;

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 *
 * @author erin
 */
public class GamePanel extends JPanel implements Runnable, KeyListener {

    // dimensiuni tile / ecran / jucător
    public static final int TILE_SIZE = 64;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int PLAYER_SIZE = 64;

    // poziție jucător
    int x = TILE_SIZE * 6;
    int y = TILE_SIZE * 7;
    int speed = 5;

    // input
    boolean up, down, left, right;

    // game loop
    boolean running = false;
    Thread gameThread;

    // imagini
    Image background;
    Image playerImg;

    StrangerThingsGame game;
    Player player;
    Random random = new Random();

    // hartă (tile map)
    // 1 = perete
    // 0 = liber
    // 2 = Demodog
    // 3 = Demogorgon
    // 4 = Vecna
    // 5 = Shop
    public static final int MAP_COLS = 12;
    public static final int MAP_ROWS = 9;

    int[][] map = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 4, 0, 0, 0, 3, 0, 1},
        {1, 2, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1},
        {1, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1},
        {1, 2, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1},
        {1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 5, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    public GamePanel(StrangerThingsGame game, Player player) {
        this.game = game;
        this.player = player;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        try {
            background = new ImageIcon(getClass().getResource("/strangerthingsgame/assets/mall.png")).getImage();
            playerImg = new ImageIcon(getClass().getResource("/strangerthingsgame/assets/player.png")).getImage();
        } catch (Exception e) {
            System.out.println("Nu s-au putut încărca imaginile.");
            e.printStackTrace();
        }
    }

    // pornește jocul
    public void startGame() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
        requestFocusInWindow();
    }

    // oprește jocul
    public void stopGame() {
        running = false;
    }

    // verifică dacă jucătorul se poate muta pe tile
    private boolean canMove(int nextX, int nextY) {
        int col = nextX / TILE_SIZE;
        int row = nextY / TILE_SIZE;

        if (row < 0 || col < 0 || row >= map.length || col >= map[0].length) {
            return false;
        }

        return map[row][col] != 1; // 1 = perete
    }

    @Override
    public void run() {
        while (running) {
            update();
            repaint();
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // update poziție + evenimente
    public void update() {
        if (up && canMove(x, y - speed)) {
            y -= speed;
        }
        if (down && canMove(x, y + speed)) {
            y += speed;
        }
        if (left && canMove(x - speed, y)) {
            x -= speed;
        }
        if (right && canMove(x + speed, y)) {
            x += speed;
        }

        checkEnemyTile();
    }

    // desenarea jocului
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(background, 0, 0, WIDTH, HEIGHT, null);
        g.drawImage(playerImg, x, y, PLAYER_SIZE, PLAYER_SIZE, null);

        // HUD simplu
        g.setColor(Color.BLACK);
        g.fillRect(10, 10, 200, 80);

        g.setColor(Color.GREEN);
        g.drawString("HP: " + player.hp + " / " + player.maxHp, 20, 30);

        g.setColor(Color.WHITE);
        g.drawString("Level: " + player.getLevel(), 20, 50);
        g.drawString("Gold: " + player.getGold(), 20, 70);
    }

    // controale WASD
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) {
            up = true;
        }
        if (code == KeyEvent.VK_S) {
            down = true;
        }
        if (code == KeyEvent.VK_A) {
            left = true;
        }
        if (code == KeyEvent.VK_D) {
            right = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) {
            up = false;
        }
        if (code == KeyEvent.VK_S) {
            down = false;
        }
        if (code == KeyEvent.VK_A) {
            left = false;
        }
        if (code == KeyEvent.VK_D) {
            right = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // verifică tile-ul curent (inamici + shop)
    private void checkEnemyTile() {
        int centerX = x + (TILE_SIZE / 2);
        int centerY = y + (TILE_SIZE / 2);

        int col = centerX / TILE_SIZE;
        int row = centerY / TILE_SIZE;

        if (row < 0 || col < 0 || row >= map.length || col >= map[0].length) {
            return;
        }

        int tileId = map[row][col];

        // inamici 2–4
        if (tileId >= 2 && tileId <= 4) {
            map[row][col] = 0; // ștergem inamicul

            Enemy enemy = null;

            // configurare inamic
            if (tileId == 2) {
                enemy = new Enemy("Demodog", 30, 5, 20, 40);
            } else if (tileId == 3) {
                enemy = new Enemy("Demogorgon", 80, 12, 100, 100);
            } else if (tileId == 4) {
                enemy = new Enemy("Vecna", 200, 20, 500, 500);
            }

            if (enemy != null) {
                up = down = left = right = false;
                game.startCombat(enemy);
            }
        } // tile 5 = shop
        else if (tileId == 5) {
            up = down = left = right = false;
            openShop();
            x += 20; // ieși din tile-ul shop-ului
        }
    }

    // fereastra de shop
    private void openShop() {

        int pricePotion = 20;
        int priceWeapon = 50;
        int priceTape = 40;

        String[] options = {
            "New Coke (Heal) - " + pricePotion + "g",
            "Spiked Bat (Atk+) - " + priceWeapon + "g",
            "Favorite Song Tape - " + priceTape + "g",
            "SAVE GAME",
            "Exit Shop"
        };

        int choice = javax.swing.JOptionPane.showOptionDialog(
                this,
                "Welcome to STARCOURT MALL!\n\n"
                + "Gold: " + player.gold + " G\n"
                + "HP: " + player.hp + "/" + player.maxHp,
                "Starcourt Shop",
                javax.swing.JOptionPane.DEFAULT_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        // heal
        if (choice == 0) {
            if (player.gold >= pricePotion) {
                player.gold -= pricePotion;
                player.hp = player.maxHp;
                javax.swing.JOptionPane.showMessageDialog(this, "HP fully restored.");
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Not enough gold.");
            }
        } // attack upgrade
        else if (choice == 1) {
            if (player.gold >= priceWeapon) {
                player.gold -= priceWeapon;
                player.attackPower += 15;
                javax.swing.JOptionPane.showMessageDialog(this, "Attack increased.");
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Too expensive.");
            }
        } // max HP upgrade
        else if (choice == 2) {
            if (player.gold >= priceTape) {
                player.gold -= priceTape;
                player.maxHp += 50;
                player.hp = player.maxHp;
                javax.swing.JOptionPane.showMessageDialog(this, "Max HP increased.");
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Too expensive.");
            }
        } // save
        else if (choice == 3) {
            player.saveProgress();
            javax.swing.JOptionPane.showMessageDialog(this, "Game saved.");
        }
    }
}
