package strangerthingsgame;

import java.io.*;
import java.util.ArrayList;
/**
 *
 * @author erin
 */
public class Player extends Entity {

    // Datele principale ale jucătorului
    public int exp;
    public int gold;
    public int level;
    public int attackPower;   // puterea de atac cumpărată/crescută din shop 

    // inventar simplu cu nume de iteme
    private ArrayList<String> inventory;

    // constructor - setez valorile de start
    public Player() {
        level = 1;

        maxHp = 100;
        hp = maxHp;

        attackPower = 20;
        defense = 5;
        speed = 10;

        exp = 0;
        gold = 50;

        inventory = new ArrayList<>();
        inventory.add("Walkman");
    }

    // Atacul jucătorului (folosește attackPower)
    @Override
    public int attack() {
        return attackPower;
    }

    // XP + verificare level up
    public void gainExp(int amount) {
        exp += amount;
        System.out.println("Ai castigat " + amount + " XP!");

        if (exp >= 100) {
            levelUp();
            exp -= 100;
        }
    }

    // Crește nivelul și statisticile
    private void levelUp() {
        level++;
        maxHp += 20;
        attackPower += 5;
        defense += 3;
        hp = maxHp;

        System.out.println("LEVEL UP! Esti acum nivel " + level);
    }

    // Gold primit
    public void gainGold(int amount) {
        gold += amount;
        System.out.println("Ai gasit " + amount + " Gold!");
    }

    // Adăugare item în inventar
    public void addItem(String item) {
        inventory.add(item);
        System.out.println("Ai primit: " + item);
    }

    // Salvare progres în fișier
    public void saveProgress() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("savegame.txt"))) {
            writer.println(level);
            writer.println(hp);
            writer.println(gold);
            writer.println(exp);
            writer.println(maxHp);
            writer.println(attackPower);
            System.out.println("Progres salvat!");
        } catch (IOException e) {
            System.out.println("Eroare la salvare: " + e.getMessage());
        }
    }

    // Încărcare progres din fișier
    public boolean loadProgress() {
        File f = new File("savegame.txt");
        if (!f.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            level = Integer.parseInt(reader.readLine());
            hp = Integer.parseInt(reader.readLine());
            gold = Integer.parseInt(reader.readLine());
            exp = Integer.parseInt(reader.readLine());
            maxHp = Integer.parseInt(reader.readLine());
            attackPower = Integer.parseInt(reader.readLine());
            return true;
        } catch (IOException | NumberFormatException e) {
            System.out.println("Salvarea este corupta: " + e.getMessage());
            return false;
        }
    }

    // Getteri simpli
    public int getLevel() {
        return level;
    }

    public int getGold() {
        return gold;
    }

    public int getExp() {
        return exp;
    }

    public ArrayList<String> getInventory() {
        return inventory;
    }
}
