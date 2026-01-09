package strangerthingsgame;

/**
 *
 * @author erin
 */
public class Enemy extends Entity {

    // 1. Atribute specifice inamicului
    private String name;        // numele inamicului
    private int xpReward;       // xp primit după luptă
    private int goldReward;     // gold primit după luptă

    // 2. Constructor
    // primești și goldReward pentru shop
    public Enemy(String name, int hp, int attack, int xpReward, int goldReward) {

        // atribute moștenite din Entity
        this.maxHp = hp;
        this.hp = maxHp;        // viață plină la început
        this.attack = attack;

        // valori default
        this.defense = 2;
        this.speed = 8;

        // atribute specifice inamicului
        this.name = name;
        this.xpReward = xpReward;
        this.goldReward = goldReward;
    }

    // 3. Metodă de atac (din Entity)
    @Override
    public int attack() {
        // damage-ul produs de inamic
        return this.attack;
    }

    // 4. Mesaj de atac (atmosferă)
    public String getAttackMessage() {
        if (this.name.equals("Vecna")) {
            return "Vecna invades your mind with dark visions!";
        } else if (this.name.equals("Demogorgon")) {
            return "The Demogorgon opens its face and bites!";
        } else {
            return "The Demodog lunges at you!";
        }
    }

    // 5. Getteri
    public String getName() {
        return name;
    }

    public int getXpReward() {
        return xpReward;
    }

    public int getGoldReward() {
        return goldReward;
    }
}
