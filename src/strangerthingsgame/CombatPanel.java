package strangerthingsgame;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author erin
 */
public class CombatPanel extends JPanel {

    Player player;
    Enemy enemy;
    StrangerThingsGame game;

    // Elemente din UI (lupta)
    JLabel imageLabel;
    JTextArea log;
    JButton attackBtn;
    JProgressBar playerBar;
    JProgressBar enemyBar;

    public CombatPanel(StrangerThingsGame game, Player player, Enemy enemy) {
        this.game = game;
        this.player = player;
        this.enemy = enemy;

        // Layout principal pentru panelul de luptă
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // 1. Bara de HP pentru player + enemy
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(Color.BLACK);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // HP Player
        playerBar = new JProgressBar(0, player.maxHp);
        playerBar.setValue(player.hp);
        playerBar.setString("YOU: " + player.hp + " HP");
        playerBar.setStringPainted(true);
        playerBar.setForeground(Color.GREEN);
        playerBar.setBackground(Color.DARK_GRAY);

        // HP Enemy
        enemyBar = new JProgressBar(0, enemy.maxHp);
        enemyBar.setValue(enemy.hp);
        enemyBar.setString(enemy.getName() + ": " + enemy.hp + " HP");
        enemyBar.setStringPainted(true);
        enemyBar.setForeground(Color.RED);
        enemyBar.setBackground(Color.DARK_GRAY);

        topPanel.add(playerBar);
        topPanel.add(enemyBar);
        add(topPanel, BorderLayout.NORTH);

        // 2. Zona centrală — imaginea luptei
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // aleg imaginea în funcție de inamic
        String imagePath = "";
        if (enemy.getName().equals("Vecna")) {
            imagePath = "/strangerthingsgame/assets/fight_vecna.png";
        } else if (enemy.getName().equals("Demogorgon")) {
            imagePath = "/strangerthingsgame/assets/fight_demogorgon.png";
        } else {
            imagePath = "/strangerthingsgame/assets/fight_demodog.png";
        }

        // încărcare + scalare imagine (dacă există)
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
            Image img = icon.getImage().getScaledInstance(500, 300, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            // fallback dacă poza lipsește
            imageLabel.setText("NO IMAGE FOUND: " + enemy.getName());
            imageLabel.setForeground(Color.WHITE);
        }

        add(imageLabel, BorderLayout.CENTER);

        // 3. Zona de jos - log + buton atac
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(800, 180));

        // log text lupte
        log = new JTextArea();
        log.setEditable(false);
        log.setFont(new Font("Monospaced", Font.BOLD, 14));
        log.setBackground(new Color(20, 20, 20));
        log.setForeground(Color.GREEN);
        log.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollLog = new JScrollPane(log);
        bottomPanel.add(scrollLog, BorderLayout.CENTER);

        // butonul de atac
        attackBtn = new JButton("ATTACK WITH WEAPON");
        attackBtn.setFont(new Font("Arial", Font.BOLD, 18));
        attackBtn.setBackground(new Color(150, 0, 0));
        attackBtn.setForeground(Color.WHITE);
        attackBtn.setFocusPainted(false);
        attackBtn.setPreferredSize(new Dimension(800, 50));

        attackBtn.addActionListener(e -> performTurn());

        bottomPanel.add(attackBtn, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // mesaj inițial în log
        startMessage();
    }

    // textul care apare la începutul luptei
    private void startMessage() {
        log.setText("");
        if (enemy.getName().equals("Vecna")) {
            log.append("!!! BOSS BATTLE !!!\nVecna has entered your mind.\nFIGHT FOR YOUR LIFE!\n");
        } else {
            log.append("A wild " + enemy.getName() + " blocked your path!\n");
        }
        log.append("Choose your action...\n");
    }

    // o rundă completă de luptă (player + enemy)
    private void performTurn() {

        // 1. atacul jucătorului
        int playerDmg = player.attack();
        enemy.hp -= playerDmg;
        if (enemy.hp < 0) {
            enemy.hp = 0;
        }

        enemyBar.setValue(enemy.hp);
        enemyBar.setString(enemy.getName() + ": " + enemy.hp + " HP");
        log.append("> You hit " + enemy.getName() + " for " + playerDmg + " dmg.\n");

        // verificăm dacă inamicul a murit
        if (enemy.hp <= 0) {
            winCombat();
            return;
        }

        // 2. atacul inamicului
        log.append("> " + enemy.getAttackMessage() + "\n");
        int enemyDmg = enemy.attack();

        // damage cu puțină reducere din defense
        int damageTaken = Math.max(0, enemyDmg - (player.defense / 2));
        player.hp -= damageTaken;
        if (player.hp < 0) {
            player.hp = 0;
        }

        playerBar.setValue(player.hp);
        playerBar.setString("YOU: " + player.hp + " HP");
        log.append("> You took " + damageTaken + " damage!\n");

        // scroll la finalul log-ului
        log.setCaretPosition(log.getDocument().getLength());

        // verificăm dacă jucătorul a murit
        if (player.hp <= 0) {
            loseCombat();
        }
    }

    // ce se întâmplă când câștigi lupta
    private void winCombat() {
        attackBtn.setEnabled(false);
        log.append("\n*** VICTORY ***\n");

        // final special pentru Vecna
        if (enemy.getName().equals("Vecna")) {
            JOptionPane.showMessageDialog(
                    this,
                    "AMAZING!\nYou defeated Vecna and closed the gate!\nHawkins is safe... for now.",
                    "VICTORY",
                    JOptionPane.INFORMATION_MESSAGE
            );
            System.exit(0);
            return;
        }

        // recompense normale
        player.gainExp(enemy.getXpReward());
        player.gainGold(enemy.getGoldReward());

        log.append("Loot: " + enemy.getGoldReward() + " Gold | " + enemy.getXpReward() + " XP.\n");
        log.append("Returning to map in 2 seconds...");

        Timer timer = new Timer(2000, e -> game.endCombat());
        timer.setRepeats(false);
        timer.start();
    }

    // ce se întâmplă când pierzi lupta
    private void loseCombat() {
        attackBtn.setEnabled(false);

        if (enemy.getName().equals("Vecna")) {
            JOptionPane.showMessageDialog(
                    this,
                    "SNAP... The clock strikes 4.\nVecna has taken your soul.",
                    "GAME OVER",
                    JOptionPane.ERROR_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(this, "You were defeated by " + enemy.getName());
        }

        System.exit(0);
    }
}
