import java.util.*;

/**
 * Main class for running the Bank System application, handling user commands and interactions.
 */
public class Main {
    /**
     * The entry point of the application which processes commands to manage bank accounts.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); // Scanner to read from standard input
        String[] line1 = sc.nextLine().split(" ");
        int n = Integer.parseInt(line1[0]); // Number of operations to perform
        for (int i = 0; i < n; i++) { // Process each operation
            String[] line = sc.nextLine().split(" ");
            String command = line[0]; // The command to execute
            if (command.equals("Create")){
                String type = line[2];
                String owner = line[3];
                String initialDeposit = line[4];
                BankSystem.createAccount(owner, initialDeposit, type);
            } else if (command.equals("Transfer")) {
                String from = line[1];
                String to = line[2];
                String fund = line[3];
                BankSystem.Transfer(from, to, fund);
            } else if (command.equals("Deposit")) {
                String owner = line[1];
                String fund = line[2];
                BankSystem.Deposit(fund, owner);
            } else if (command.equals("Withdraw")) {
                String owner = line[1];
                String fund = line[2];
                BankSystem.Withdrawal(fund, owner);
            } else if (command.equals("Activate")) {
                String owner = line[1];
                BankSystem.Activate(owner);
            } else if (command.equals("Deactivate")) {
                String owner = line[1];
                BankSystem.Deactivate(owner);
            } else {
                String owner = line[1];
                BankSystem.View(owner);
            }
        }
    }
}

/**
 * Singleton class that manages bank accounts and transactions. Used also for facade pattern.
 */
class BankSystem {
    private static BankSystem unique = new BankSystem(); // The single instance
    private static ArrayList<Account> accounts = new ArrayList<>(); // List of all accounts
    private BankSystem() {} // Private constructor to prevent instantiation
    /**
     * Returns the singleton instance of the BankSystem.
     * @return The singleton instance of the BankSystem.
     */
    public static BankSystem getInstance() {
        return unique;
    }

    /**
     * Creates a new bank account. Uses factory method pattern.
     * @param owner The owner of the account.
     * @param initial_dep The initial deposit amount as a string.
     * @param type The type of the account (e.g., Savings, Checking).
     */
    public static void createAccount(String owner, String initial_dep, String type){
        double initial_deposit = Double.parseDouble(initial_dep);
        // Create different types of accounts based on the input type
        if (Objects.equals(type, "Savings")){
            SavingsAccount account = new SavingsAccount(owner, initial_deposit);
            accounts.add(account);
        } else if (Objects.equals(type, "Checking")){
            CheckingAccount account = new CheckingAccount(owner, initial_deposit);
            accounts.add(account);
        } else if (Objects.equals(type, "Business")){
            BusinessAccount account = new BusinessAccount(owner, initial_deposit);
            accounts.add(account);
        }
        // Feedback to user about account creation
        System.out.println("A new " + type + " account created for " + owner + " with an initial balance of $" + format(initial_deposit) + ".");
    }

    /**
     * Transfers money from one account to another.
     * @param sender The account from which funds are being sent.
     * @param getter The account to which funds are being sent.
     * @param fund The amount of money to transfer as a string.
     */
    public static void Transfer(String sender, String getter, String fund){
        double funds = Double.parseDouble(fund);
        Account send = getAccount(sender); // Sender account
        Account get = getAccount(getter); // Receiver account
        if (send == null){
            System.out.println("Error: Account " + sender + " does not exist.");
        } else {
            if (get == null){
                System.out.println("Error: Account " + getter + " does not exist.");
            } else {
                send.makeTransfer(funds, get); // Perform the transfer
            }
        }
    }

    /**
     * Deposits money into an account.
     * @param funds The amount of money to deposit as a string.
     * @param owner The owner of the account where the deposit is made.
     */
    public static void Deposit(String funds, String owner){
        double fund = Double.parseDouble(funds);
        Account own = getAccount(owner);
        if (own == null){
            System.out.println("Error: Account " + owner + " does not exist.");
        } else {
            own.makeDeposit(fund); // Perform the deposit
        }
    }

    /**
     * Withdraws money from an account.
     * @param funds The amount of money to withdraw as a string.
     * @param owner The owner of the account from which the withdrawal is made.
     */
    public static void Withdrawal(String funds, String owner){
        double fund = Double.parseDouble(funds);
        Account own = getAccount(owner);
        if (own == null){
            System.out.println("Error: Account " + owner + " does not exist.");
        } else {
            own.makeWithdraw(fund); // Perform the withdrawal
        }
    }

    /**
     * Activates an account.
     * @param owner The owner of the account to activate.
     */
    public static void Activate(String owner){
        Account own = getAccount(owner);
        if (own == null){
            System.out.println("Error: Account " + owner + " does not exist.");
        } else {
            if (own.getState()){
                System.out.println("Error: Account " + owner + " is already activated.");
            } else {
                own.setState(true); // Change the state to active
            }
        }
    }

    /**
     * Deactivates an account.
     * @param owner The owner of the account to deactivate.
     */
    public static void Deactivate(String owner){
        Account own = getAccount(owner);
        if (own == null){
            System.out.println("Error: Account " + owner + " does not exist.");
        } else {
            if (!own.getState()){
                System.out.println("Error: Account " + owner + " is already deactivated.");
            } else {
                own.setState(false); // Change the state to inactive
            }
        }
    }

    /**
     * Displays details of an account.
     * @param owner The owner of the account whose details are to be displayed.
     */
    public static void View(String owner){
        Account own = getAccount(owner);
        if (own == null){
            System.out.println("Error: Account " + owner + " does not exist.");
        } else {
            own.makeView(); // Show account details
        }
    }

    /**
     * Retrieves an account by the owner's name.
     * @param owner The owner's name.
     * @return The account if found, null otherwise.
     */
    public static Account getAccount(String owner){
        for (Account account: accounts){
            if (account.owner.equals(owner)){
                return account;
            }
        }
        return null;
    }

    /**
     * Formats a number to a string with three decimal places.
     * @param n The number to format.
     * @return The formatted string.
     */
    public static String format(double n){
        return String.format("%.3f",n).replace(",", ".");
    }
}

/**
 Interface for fee strategies used in different account types. Used for Strategy pattern for fee.
 */
interface FeeStrategies {
    void makeTransfer(double fund, Account getter);
    void makeWithdraw(double fund);
}

/**
 * Base class for all account types, encapsulating common properties and operations of bank accounts.
 */
class Account{
    String owner;
    private final double initial_deposit;
    private boolean active;
    private double deposit;
    protected ArrayList<String> transactions;

    /**
     * Constructor to initialize an account with an owner and an initial deposit.
     * The account is activated by default and the initial deposit is added to the transaction history.
     *
     * @param owner           The name of the account owner.
     * @param initial_deposit The initial amount deposited into the account.
     */
    public Account(String owner, double initial_deposit) {
        this.owner = owner;
        this.initial_deposit = initial_deposit;
        this.active = true;
        this.deposit = initial_deposit;
        this.transactions = new ArrayList<>();
        this.transactions.add("Initial Deposit $" + format(initial_deposit));
    }

    /**
     * Deposits a specified amount into the account and logs the transaction.
     *
     * @param fund The amount to be deposited.
     */
    public void makeDeposit(double fund){
        deposit += fund;
        this.transactions.add("Deposit $" + format(fund));
        // Feedback to user about successful deposit
        System.out.println(this.getOwner() + " successfully deposited $" + format(fund) + ". New Balance: $" + format(this.getDeposit()) + ".");
    }

    /**
     * Placeholder for transfer method, intended to be overridden in subclasses to implement account transfers.
     *
     * @param fund   The amount to be transferred.
     * @param getter The account to which the funds are transferred.
     */
    public void makeTransfer(double fund, Account getter){}

    /**
     * Placeholder for withdrawal method, intended to be overridden in subclasses to implement withdrawals.
     *
     * @param fund The amount to be withdrawn.
     */
    public void makeWithdraw(double fund){}

    /**
     * Placeholder for view method, intended to be overridden in subclasses to display account details.
     */
    public void makeView(){}

    /**
     * Returns the name of the account owner.
     *
     * @return The owner of the account.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Returns the current activation state of the account.
     *
     * @return True if the account is active, false otherwise.
     */
    public boolean getState() {
        return active;
    }

    /**
     * Sets the activation state of the account and provides feedback about the state change.
     *
     * @param state The new activation state of the account.
     */
    public void setState(boolean state) {
        this.active = state;
        // Feedback to user about state change
        if (state){
            System.out.println(this.getOwner() + "'s account is now activated.");
        } else {
            System.out.println(this.getOwner() + "'s account is now deactivated.");
        }
    }

    /**
     * Returns the current deposit balance of the account.
     *
     * @return The current balance.
     */
    public double getDeposit() {
        return deposit;
    }

    /**
     * Sets the current deposit balance.
     *
     * @param deposit The new deposit amount.
     */
    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    /**
     * Returns the initial deposit amount.
     *
     * @return The amount of the initial deposit.
     */
    public double getInitial_deposit() {
        return initial_deposit;
    }

    /**
     * Returns the list of all transactions associated with the account.
     *
     * @return A list of transaction descriptions.
     */
    public ArrayList<String> getTransactions() {
        return transactions;
    }

    /**
     * Sets the list of transactions for the account.
     *
     * @param transactions The new list of transactions.
     */
    public void setTransactions(ArrayList<String> transactions) {
        this.transactions = transactions;
    }

    /**
     * Formats a numerical amount to a string with three decimal places.
     *
     * @param n The number to be formatted.
     * @return The formatted string with 3 sings after the point.
     */
    public static String format(double n){
        return String.format("%.3f",n).replace(",", ".");
    }
}

/**
 * Specialized SavingsAccount class that extends Account and implements the FeeStrategies interface.
 * This account type handles transactions with a fee deduction.
 */
class SavingsAccount extends Account implements FeeStrategies {
    String type = "Savings";
    double fee = 0.015; // Transaction fee rate
    double sum_without_fee = 1 - fee; // Amount after fee deduction

    /**
     * Constructor to initialize a SavingsAccount with an owner and an initial deposit.
     * @param owner The owner of the account.
     * @param initial_deposit The initial deposit to start the account with.
     */
    public SavingsAccount(String owner, double initial_deposit) {
        super(owner, initial_deposit);
    }

    /**
     * Implements the transfer method with a transaction fee deduction.
     * Ensures that the account is active and has sufficient funds before proceeding with the transfer.
     * @param fund The amount of money to transfer.
     * @param getter The recipient account.
     */
    @Override
    public void makeTransfer(double fund, Account getter) {
        if (this.getState()){ // Check if the account is active
            if (this.getDeposit() >= fund){ // Check for sufficient funds
                this.setDeposit(this.getDeposit() - fund);
                getter.setDeposit(getter.getDeposit() + fund * sum_without_fee);
                this.transactions.add("Transfer $" + format(fund));
                // Feedback to user about successful transfer and fee deduction
                System.out.println(this.owner + " successfully transferred $" + format(fund * sum_without_fee) + " to " + getter.owner + ". New Balance: $" + format(this.getDeposit()) + ". Transaction Fee: $" + format(fund * fee) + " (1.5%) in the system.");
            } else {
                System.out.println("Error: Insufficient funds for " + owner + ".");
            }
        } else {
            System.out.println("Error: Account " + this.getOwner() + " is inactive.");
        }
    }

    /**
     * Implements the withdrawal method with a transaction fee deduction.
     * Ensures that the account is active and has sufficient funds before allowing withdrawal.
     * @param fund The amount to withdraw.
     */
    @Override
    public void makeWithdraw(double fund) {
        if (this.getState()){ // Check if the account is active
            if (this.getDeposit() >= fund){ // Check for sufficient funds
                this.setDeposit(this.getDeposit() - fund);
                this.transactions.add("Withdrawal $" + format(fund));
                // Feedback to user about successful withdrawal and fee deduction
                System.out.println(this.owner + " successfully withdrew $" + format(fund * sum_without_fee) + ". New Balance: $" + format(this.getDeposit()) + ". Transaction Fee: $" + format(fund * fee) + " (1.5%) in the system." );
            } else {
                System.out.println("Error: Insufficient funds for " + owner + ".");
            }
        } else {
            System.out.println("Error: Account " + owner + " is inactive.");
        }

    }

    /**
     * Displays the account details including transaction history.
     * Provides a comprehensive view of the account's transactions, type, balance, and state.
     */
    @Override
    public void makeView(){
        StringBuilder res = new StringBuilder();
        res.append(this.owner).append("'s Account: ");
        res.append("Type: ").append(type).append(", ");
        res.append("Balance: $").append(format(this.getDeposit())).append(", ");
        if (this.getState()){
            res.append("State: ").append("Active").append(", ");
        } else {
            res.append("State: ").append("Inactive").append(", ");
        }
        res.append("Transactions: ").append("[");
        for (int i = 0; i < transactions.size(); i++){
            if (i < transactions.size() - 1){
                res.append(transactions.get(i)).append(", ");
            } else {
                res.append(transactions.get(i));
            }
        }
        res.append("].");
        System.out.println(res);
    }
}

/**
 * Specialized CheckingAccount class that extends Account and implements the FeeStrategies interface.
 * This account type handles transactions with a fee deduction.
 */
class CheckingAccount extends Account implements FeeStrategies{
    String type = "Checking";
    double fee = 0.020; // Transaction fee rate
    double sum_without_fee = 1 - fee; // Amount after fee deduction

    /**
     * Constructor to initialize a CheckingAccount with an owner and an initial deposit.
     * @param owner The owner of the account.
     * @param initial_deposit The initial deposit to start the account with.
     */
    public CheckingAccount(String owner, double initial_deposit) {
        super(owner, initial_deposit);
    }

    /**
     * Implements the transfer method with a transaction fee deduction.
     * Ensures that the account is active and has sufficient funds before proceeding with the transfer.
     * @param fund The amount of money to transfer.
     * @param getter The recipient account.
     */
    @Override
    public void makeTransfer(double fund, Account getter) {
        if (this.getState()){ // Check if the account is active
            if (this.getDeposit() >= fund){ // Check for sufficient funds
                this.setDeposit(this.getDeposit() - fund);
                getter.setDeposit(getter.getDeposit() + fund * sum_without_fee);
                this.transactions.add("Transfer $" + format(fund));
                // Feedback to user about successful transfer and fee deduction
                System.out.println(this.owner + " successfully transferred $" + format(fund * sum_without_fee) + " to " + getter.owner + ". New Balance: $" + format(this.getDeposit()) + ". Transaction Fee: $" + format(fund * fee) + " (2.0%) in the system.");
            } else {
                System.out.println("Error: Insufficient funds for " + owner + ".");
            }
        } else {
            System.out.println("Error: Account " + this.getOwner() + " is inactive.");
        }
    }

    /**
     * Implements the withdrawal method with a transaction fee deduction.
     * Ensures that the account is active and has sufficient funds before allowing withdrawal.
     * @param fund The amount to withdraw.
     */
    @Override
    public void makeWithdraw(double fund) {
        if (this.getState()){ // Check if the account is active
            if (this.getDeposit() >= fund){ // Check for sufficient funds
                this.setDeposit(this.getDeposit() - fund);
                this.transactions.add("Withdrawal $" + format(fund));
                // Feedback to user about successful withdrawal and fee deduction
                System.out.println(this.owner + " successfully withdrew $" + format(fund * sum_without_fee) + ". New Balance: $" + format(this.getDeposit()) + ". Transaction Fee: $" + format(fund * fee) + " (2.0%) in the system." );
            } else {
                System.out.println("Error: Insufficient funds for " + owner + ".");
            }
        } else {
            System.out.println("Error: Account " + owner + " is inactive.");
        }

    }

    /**
     * Displays the account details including transaction history.
     * Provides a comprehensive view of the account's transactions, type, balance, and state.
     */
    @Override
    public void makeView(){
        StringBuilder res = new StringBuilder();
        res.append(this.owner).append("'s Account: ");
        res.append("Type: ").append(type).append(", ");
        res.append("Balance: $").append(format(this.getDeposit())).append(", ");
        if (this.getState()){
            res.append("State: ").append("Active").append(", ");
        } else {
            res.append("State: ").append("Inactive").append(", ");
        }
        res.append("Transactions: ").append("[");
        for (int i = 0; i < transactions.size(); i++){
            if (i < transactions.size() - 1){
                res.append(transactions.get(i)).append(", ");
            } else {
                res.append(transactions.get(i));
            }
        }
        res.append("].");
        System.out.println(res);
    }
}

/**
 * Specialized BusinessAccount class that extends Account and implements the FeeStrategies interface.
 * This account type handles transactions with a fee deduction.
 */
class BusinessAccount extends Account implements FeeStrategies{
    String type = "Business";
    double fee = 0.025; // Transaction fee rate
    double sum_without_fee = 1 - fee; // Amount after fee deduction

    /**
     * Constructor to initialize a BusinessAccount with an owner and an initial deposit.
     * @param owner The owner of the account.
     * @param initial_deposit The initial deposit to start the account with.
     */
    public BusinessAccount(String owner, double initial_deposit) {
        super(owner, initial_deposit);
    }

    /**
     * Implements the transfer method with a transaction fee deduction.
     * Ensures that the account is active and has sufficient funds before proceeding with the transfer.
     * @param fund The amount of money to transfer.
     * @param getter The recipient account.
     */
    @Override
    public void makeTransfer(double fund, Account getter) {
        if (this.getState()){ // Check if the account is active
            if (this.getDeposit() >= fund){ // Check for sufficient funds
                this.setDeposit(this.getDeposit() - fund);
                getter.setDeposit(getter.getDeposit() + fund * sum_without_fee);
                this.transactions.add("Transfer $" + format(fund));
                // Feedback to user about successful transfer and fee deduction
                System.out.println(this.owner + " successfully transferred $" + format(fund * sum_without_fee) + " to " + getter.owner + ". New Balance: $" + format(this.getDeposit()) + ". Transaction Fee: $" + format(fund * fee) + " (2.5%) in the system.");
            } else {
                System.out.println("Error: Insufficient funds for " + owner + ".");
            }
        } else {
            System.out.println("Error: Account " + this.getOwner() + " is inactive.");
        }
    }

    /**
     * Implements the withdrawal method with a transaction fee deduction.
     * Ensures that the account is active and has sufficient funds before allowing withdrawal.
     * @param fund The amount to withdraw.
     */
    @Override
    public void makeWithdraw(double fund) {
        if (this.getState()){ // Check if the account is active
            if (this.getDeposit() >= fund){ // Check for sufficient funds
                this.setDeposit(this.getDeposit() - fund);
                this.transactions.add("Withdrawal $" + format(fund));
                // Feedback to user about successful withdrawal and fee deduction
                System.out.println(this.owner + " successfully withdrew $" + format(fund * sum_without_fee) + ". New Balance: $" + format(this.getDeposit()) + ". Transaction Fee: $" + format(fund * fee) + " (2.5%) in the system." );
            } else {
                System.out.println("Error: Insufficient funds for " + owner + ".");
            }
        } else {
            System.out.println("Error: Account " + owner + " is inactive.");
        }

    }

    /**
     * Displays the account details including transaction history.
     * Provides a comprehensive view of the account's transactions, type, balance, and state.
     */
    @Override
    public void makeView(){
        StringBuilder res = new StringBuilder();
        res.append(this.owner).append("'s Account: ");
        res.append("Type: ").append(type).append(", ");
        res.append("Balance: $").append(format(this.getDeposit())).append(", ");
        if (this.getState()){
            res.append("State: ").append("Active").append(", ");
        } else {
            res.append("State: ").append("Inactive").append(", ");
        }
        res.append("Transactions: ").append("[");
        for (int i = 0; i < transactions.size(); i++){
            if (i < transactions.size() - 1){
                res.append(transactions.get(i)).append(", ");
            } else {
                res.append(transactions.get(i));
            }
        }
        res.append("].");
        System.out.println(res);
    }
}
