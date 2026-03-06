import java.util.*;

public class Main {

    static HashMap<String, Account> accounts = new HashMap<>();
    static HashMap<String, Integer> categorySpend = new HashMap<>();
    static HashMap<String, Integer> goalTarget = new HashMap<>();
    static HashMap<String, Integer> goalSaved = new HashMap<>();

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {

            String line = sc.nextLine();
            String[] parts = line.split(" ");

            String cmd = parts[0].toUpperCase();

            switch (cmd) {

                case "HELP":
                    help();
                    break;

                case "OPEN":
                    open(parts[1]);
                    break;

                case "DEPOSIT":
                    deposit(parts[1], Integer.parseInt(parts[2]));
                    break;

                case "WITHDRAW":
                    withdraw(parts[1], Integer.parseInt(parts[2]));
                    break;

                case "TRANSFER":
                    transfer(parts[1], parts[2], Integer.parseInt(parts[3]));
                    break;

                case "BALANCE":
                    balance(parts[1]);
                    break;

                case "ACCOUNTS":
                    accounts();
                    break;

                case "SPEND":
                    spend(parts[1], parts[2], Integer.parseInt(parts[3]));
                    break;

                case "CATEGORY":
                    category(parts[1]);
                    break;

                case "TOP_CATEGORIES":
                    topCategories(Integer.parseInt(parts[1]));
                    break;

                case "SET_GOAL":
                    setGoal(parts[1], Integer.parseInt(parts[2]));
                    break;

                case "SAVE":
                    save(parts[1], parts[2], Integer.parseInt(parts[3]));
                    break;

                case "GOAL_STATUS":
                    goalStatus(parts[1]);
                    break;

                case "GOALS":
                    goals();
                    break;

                case "LOCK":
                    lock(parts[1]);
                    break;

                case "UNLOCK":
                    unlock(parts[1]);
                    break;

                case "RESET":
                    reset();
                    break;

                case "EXIT":
                    return;

                default:
                    System.out.println("Unknown command");
            }
        }
    }

    static void help() {

        System.out.println("Commands:");
        System.out.println("OPEN <account>");
        System.out.println("DEPOSIT <account> <amount>");
        System.out.println("WITHDRAW <account> <amount>");
        System.out.println("TRANSFER <from> <to> <amount>");
        System.out.println("BALANCE <account>");
        System.out.println("ACCOUNTS");
        System.out.println("SPEND <account> <category> <amount>");
        System.out.println("CATEGORY <category>");
        System.out.println("TOP_CATEGORIES <k>");
        System.out.println("SET_GOAL <goal> <target>");
        System.out.println("SAVE <account> <goal> <amount>");
        System.out.println("GOAL_STATUS <goal>");
        System.out.println("GOALS");
        System.out.println("LOCK <account>");
        System.out.println("UNLOCK <account>");
        System.out.println("RESET");
        System.out.println("EXIT");
    }

    static void open(String name) {

        if (accounts.containsKey(name)) {
            System.out.println("Account exists");
            return;
        }
        accounts.put(name, new Account(name));
    }

    static void deposit(String name, int amount) {

        Account acc = accounts.get(name);
        if (acc == null) {
            System.out.println("Account not found");
            return;
        }
        if (amount <= 0) {
            return;
        }
        acc.deposit(amount);
    }

    static void withdraw(String name, int amount) {

        Account acc = accounts.get(name);
        if (acc == null) {
            System.out.println("Account not found");
            return;
        }
        if (acc.isLocked()) {
            System.out.println("Account locked");
            return;
        }

        acc.withdraw(amount);
    }

    static void transfer(String from, String to, int amount) {

        Account a = accounts.get(from);
        Account b = accounts.get(to);
        if (a == null || b == null) {
            System.out.println("Account not found");
            return;
        }
        if (a.isLocked() || b.isLocked()) {
            System.out.println("Account locked");
            return;
        }
        if (a.getBalance() < amount) {
            return;
        }
        a.withdraw(amount);
        b.deposit(amount);
    }

    static void balance(String name) {

        Account acc = accounts.get(name);
        if (acc == null) {
            System.out.println("Account not found");
            return;
        }
        System.out.println(acc.getBalance());
    }

    static void accounts() {

        for (Account a : accounts.values()) {
            System.out.println(a.getName() + " " + a.getBalance());
        }
    }

    static void spend(String name, String category, int amount) {

        Account acc = accounts.get(name);

        if (acc == null) {
            System.out.println("Account not found");
            return;
        }
        if (acc.isLocked() || acc.getBalance() < amount) {
            return;
        }
        acc.withdraw(amount);

        categorySpend.put(
                category,
                categorySpend.getOrDefault(category, 0) + amount
        );
    }

    static void category(String cat) {

        System.out.println(categorySpend.getOrDefault(cat, 0));
    }

    static void topCategories(int k) {

        List<Map.Entry<String, Integer>> list = new ArrayList<>(categorySpend.entrySet());

        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {

                if (list.get(j).getValue() > list.get(i).getValue()) {

                    Map.Entry<String, Integer> temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
        }

        for (int i = 0; i < Math.min(k, list.size()); i++) {

            System.out.println(
                    list.get(i).getKey() + " " + list.get(i).getValue()
            );
        }
    }

    static void setGoal(String goal, int target) {

        if (target <= 0) return;
        goalTarget.put(goal, target);
        goalSaved.putIfAbsent(goal, 0);
    }

    static void save(String account, String goal, int amount) {
        Account acc = accounts.get(account);

        if (acc == null) {
            System.out.println("Account not found");
            return;
        }
        if (!goalTarget.containsKey(goal)) {
            goalTarget.put(goal, 0);
            goalSaved.put(goal, 0);
        }
        if (acc.getBalance() < amount) {
            return;
        }

        acc.withdraw(amount);

        goalSaved.put(
                goal,
                goalSaved.getOrDefault(goal, 0) + amount
        );
    }

    static void goalStatus(String goal) {

        if (!goalTarget.containsKey(goal)) {
            System.out.println("Goal not found");
            return;
        }

        int target = goalTarget.get(goal);
        int saved = goalSaved.get(goal);

        int percent = (int)((saved * 100.0) / target);

        if (percent > 100) percent = 100;

        System.out.println("Target=" + target + " Saved=" + saved + " Progress=" + percent + "%");
    }

    static void goals() {
        for (String g : goalTarget.keySet()) {
            System.out.println(g);
        }
    }

    static void lock(String name) {
        Account acc = accounts.get(name);
        if (acc == null) {
            System.out.println("Account not found");
            return;
        }
        acc.lock();
    }
    static void unlock(String name) {

        Account acc = accounts.get(name);
        if (acc == null) {
            System.out.println("Account not found");
            return;
        }
        acc.unlock();
    }

    static void reset() {

        accounts.clear();
        categorySpend.clear();
        goalTarget.clear();
        goalSaved.clear();
    }

}