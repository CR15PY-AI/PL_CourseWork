public class Account {

    private String name;
    private int balance;
    private boolean locked;

    public Account(String name) {
        this.name = name;
        this.balance = 0;
        this.locked = false;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public boolean isLocked() {
        return locked;
    }

    public void lock() {
        locked = true;
    }

    public void unlock() {
        locked = false;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public boolean withdraw(int amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

}