package strangerthingsgame;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

// Fereastra principală a jocului
public class StrangerThingsGame extends JFrame {

    // Panou de joc (hartă) 
    GamePanel worldPanel;

    // Datele jucătorului
    Player player;

    public StrangerThingsGame() {
        setTitle("Stranger Things: The Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Creăm un player
        player = new Player();

        // Afișăm meniul principal
        showMenu();

        // Ajustare fereastră după panouri
        pack();
        // Centrare pe ecran
        setLocationRelativeTo(null);
        setVisible(true);
    }


    // MANAGEMENT SCENE/PANOURI
    // Meniul principal
    public void showMenu() {
        getContentPane().removeAll();

        MainMenuPanel menu = new MainMenuPanel(this);
        add(menu);

        revalidate();
        repaint();
    }

    // Joc nou
    public void startNewGame() {
        player = new Player();
        startGamePanel();
    }

    // Încărcare salvare
    public void loadGame() {
        if (player.loadProgress()) {
            JOptionPane.showMessageDialog(this, "Salvare încărcată! Level: " + player.getLevel());
            startGamePanel();
        } else {
            JOptionPane.showMessageDialog(this, "Nu există salvare!", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Pornirea hărții
    private void startGamePanel() {
        getContentPane().removeAll();

        worldPanel = new GamePanel(this, player);
        add(worldPanel);

        // Pornește animația jocului
        worldPanel.startGame();

        revalidate();
        repaint();

        // Focus pe hartă pentru taste
        worldPanel.requestFocusInWindow();
    }


    // SISTEM DE LUPTĂ
    // Intrare în luptă
    public void startCombat(Enemy enemy) {
        if (worldPanel != null) {
            worldPanel.stopGame();
        }

        getContentPane().removeAll();

        CombatPanel combat = new CombatPanel(this, player, enemy);
        add(combat);

        revalidate();
        repaint();

        combat.requestFocusInWindow();
    }

    // Revenire pe hartă după luptă
    public void endCombat() {
        getContentPane().removeAll();

        add(worldPanel);

        worldPanel.startGame();

        revalidate();
        repaint();

        worldPanel.requestFocusInWindow();
    }


    // MAIN – punct de intrare
    public static void main(String[] args) {
        // Look and Feel = Nimbus dacă există
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(StrangerThingsGame.class.getName())
                .log(java.util.logging.Level.SEVERE, null, ex);
        }

        // Lansăm fereastra pe thread-ul grafic (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StrangerThingsGame();
            }
        });
    }
}
