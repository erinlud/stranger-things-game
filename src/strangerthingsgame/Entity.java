package strangerthingsgame;

/**
 *
 * @author erin
 */
// Clasă de bază pentru Player și Enemy
public abstract class Entity {

    // 1. Atribute comune
    protected int hp;
    protected int maxHp;
    protected int attack;
    protected int defense;  // reduce damage-ul primit
    protected int speed;    // ordinea în luptă

    // 2. Constructor gol
    // valorile sunt setate în clasele copil
    public Entity() {
    }

    // 3. Metodă abstractă
    // fiecare entitate are propria logică de atac
    public abstract int attack();

    // 4. Metode comune
    public boolean isAlive() {
        return hp > 0;
    }

    // 5. Getteri
    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    // 6. Setter pentru HP
    public void setHp(int hp) {
        // nu lăsăm HP să treacă peste maxim
        if (hp > maxHp) {
            this.hp = maxHp;
        } else {
            this.hp = hp;
        }
    }
}
