import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

class Transaction {
    String transactionType;
    float amount;
    String timestamp;

    public Transaction(String transactionType, float amount, String timestamp) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.timestamp = timestamp;
    }
}

class Customer {
    int accountNumber;
    String name;
    float balance;
    List<Transaction> transactions; // New list to store transaction history

    public Customer(int accountNumber, String name, float balance) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }
}

class Node {
    Customer customer;
    Node left;
    Node right;

    public Node(Customer customer) {
        this.customer = customer;
        this.left = null;
        this.right = null;
    }
}

public class BankManagementSystemGUI extends JFrame {
    private Node root = null;
    private JTextArea displayArea;
    private static final String DATA_FILE = "customer_data.txt";

    public BankManagementSystemGUI() {
        setTitle("Bank Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel createPanel = createAccountPanel();
        JPanel updatePanel = updateAccountPanel();
        JPanel displayPanel = displayCustomersPanel();
        JPanel removePanel = removeAccountPanel();
        JPanel depositPanel = depositPanel();
        JPanel withdrawPanel = withdrawPanel();

        tabbedPane.addTab("Create Account", createPanel);
        tabbedPane.addTab("Update Account", updatePanel);
        tabbedPane.addTab("Display Customers", displayPanel);
        tabbedPane.addTab("Remove Account", removePanel);
        tabbedPane.addTab("Deposit", depositPanel);
        tabbedPane.addTab("Withdraw", withdrawPanel);

        displayArea = new JTextArea();
        displayArea.setEditable(false);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.NORTH);
        getContentPane().add(displayArea, BorderLayout.CENTER);

        loadCustomerData();
    }

    private JPanel createAccountPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        panel.add(new JLabel("Account Number:"));
        JTextField accountNumberField = new JTextField();
        panel.add(accountNumberField);

        panel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Balance:"));
        JTextField balanceField = new JTextField();
        panel.add(balanceField);

        JButton createButton = new JButton("Create Account");
        panel.add(createButton);
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createAccount(accountNumberField, nameField, balanceField);
            }
        });

        return panel;
    }

    private JPanel updateAccountPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        panel.add(new JLabel("Enter Account Number:"));
        JTextField accountNumberField = new JTextField();
        panel.add(accountNumberField);

        panel.add(new JLabel("New Name:"));
        JTextField nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("New Balance:"));
        JTextField balanceField = new JTextField();
        panel.add(balanceField);

        JButton updateButton = new JButton("Update Account");
        panel.add(updateButton);
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateAccount(accountNumberField, nameField, balanceField);
            }
        });

        return panel;
    }

    private JPanel displayCustomersPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton displayButton = new JButton("Display Customers");
        panel.add(displayButton);
        displayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayCustomers(root);
            }
        });

        return panel;
    }

    private JPanel removeAccountPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));

        panel.add(new JLabel("Enter Account Number:"));
        JTextField accountNumberField = new JTextField();
        panel.add(accountNumberField);

        JButton removeButton = new JButton("Remove Account");
        panel.add(removeButton);
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeAccount(accountNumberField);
            }
        });

        return panel;
    }

    private JPanel depositPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        panel.add(new JLabel("Account Number:"));
        JTextField accountNumberField = new JTextField();
        panel.add(accountNumberField);

        panel.add(new JLabel("Amount:"));
        JTextField amountField = new JTextField();
        panel.add(amountField);

        JButton depositButton = new JButton("Deposit");
        panel.add(depositButton);
        depositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deposit(accountNumberField, amountField);
            }
        });

        return panel;
    }

    private JPanel withdrawPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        panel.add(new JLabel("Account Number:"));
        JTextField accountNumberField = new JTextField();
        panel.add(accountNumberField);

        panel.add(new JLabel("Amount:"));
        JTextField amountField = new JTextField();
        panel.add(amountField);

        JButton withdrawButton = new JButton("Withdraw");
        panel.add(withdrawButton);
        withdrawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                withdraw(accountNumberField, amountField);
            }
        });

        return panel;
    }

    private void createAccount(JTextField accountNumberField, JTextField nameField, JTextField balanceField) {
        try {
            String accountNumberText = accountNumberField.getText();
            String name = nameField.getText();
            String balanceText = balanceField.getText();

            if (accountNumberText.isEmpty() || name.isEmpty() || balanceText.isEmpty()) {
                displayArea.append("Please fill in all fields.\n");
                return;
            }

            int accountNumber = Integer.parseInt(accountNumberText);
            float balance = Float.parseFloat(balanceText);

            if (findCustomer(root, accountNumber) != null) {
                displayArea.append("An account with the same account number already exists.\n");
            } else if (accountNumber >= 100000000 && accountNumber < 1000000000) {
                Customer customer = new Customer(accountNumber, name, balance);
                root = insert(root, customer);
                displayArea.append("Account created successfully.\n");

                // Display all account details
                displayCustomers(root);

                // Save customer data after creating the account
                saveCustomerData();
            } else {
                displayArea.append("Invalid account number.\n");
            }
        } catch (NumberFormatException e) {
            displayArea.append("Invalid input. Please enter valid data.\n");
        }
    }

    private Node insert(Node root, Customer customer) {
        if (root == null) {
            return new Node(customer);
        }
        if (customer.accountNumber < root.customer.accountNumber) {
            root.left = insert(root.left, customer);
        } else if (customer.accountNumber > root.customer.accountNumber) {
            root.right = insert(root.right, customer);
        }
        return root;
    }

    private Customer findCustomer(Node root, int accountNumber) {
        if (root == null) {
            return null;
        }
        if (accountNumber < root.customer.accountNumber) {
            return findCustomer(root.left, accountNumber);
        } else if (accountNumber > root.customer.accountNumber) {
            return findCustomer(root.right, accountNumber);
        } else {
            return root.customer;
        }
    }

    private void updateAccount(JTextField accountNumberField, JTextField nameField, JTextField balanceField) {
        try {
            int accountNumber = Integer.parseInt(accountNumberField.getText());
            String name = nameField.getText();
            float balance = Float.parseFloat(balanceField.getText());

            root = update(root, accountNumber, name, balance);
        } catch (NumberFormatException e) {
            displayArea.append("Invalid input. Please enter valid data.\n");
        }
    }

    private Node update(Node root, int accountNumber, String name, float balance) {
        if (root == null) {
            displayArea.append("Account not found.\n");
            return root;
        }
        if (accountNumber < root.customer.accountNumber) {
            root.left = update(root.left, accountNumber, name, balance);
        } else if (accountNumber > root.customer.accountNumber) {
            root.right = update(root.right, accountNumber, name, balance);
        } else {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Transaction transaction = new Transaction("Update", balance - root.customer.balance, timestamp);
            root.customer.transactions.add(transaction);

            root.customer.name = name;
            root.customer.balance = balance;
            displayArea.append("Account updated successfully.\n");

            // Save customer data after updating the account
            saveCustomerData();
        }
        return root;
    }

    private void displayCustomers(Node root) {
        displayArea.setText("");
        displayCustomersInOrder(root);
    }

    private void displayCustomersInOrder(Node root) {
        if (root != null) {
            displayCustomersInOrder(root.left);
            displayArea.append("Account Number: " + root.customer.accountNumber + "\n");
            displayArea.append("Name: " + root.customer.name + "\n");
            displayArea.append("Balance: " + root.customer.balance + "\n");
            displayArea.append("Transaction History:\n");

            // Display transaction history
            for (Transaction transaction : root.customer.transactions) {
                displayArea.append("Type: " + transaction.transactionType + "\n");
                displayArea.append("Amount: " + transaction.amount + "\n");
                displayArea.append("Timestamp: " + transaction.timestamp + "\n");
            }

            displayArea.append("-------------------\n");
            displayCustomersInOrder(root.right);
        }
    }

    private void removeAccount(JTextField accountNumberField) {
        try {
            int accountNumber = Integer.parseInt(accountNumberField.getText());
            root = remove(root, accountNumber);
        } catch (NumberFormatException e) {
            displayArea.append("Invalid input. Please enter valid data.\n");
        }
    }

    private Node remove(Node root, int accountNumber) {
        if (root == null) {
            displayArea.append("Account not found.\n");
            return root;
        }
        if (accountNumber < root.customer.accountNumber) {
            root.left = remove(root.left, accountNumber);
        } else if (accountNumber > root.customer.accountNumber) {
            root.right = remove(root.right, accountNumber);
        } else {
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }
            root.customer = findMin(root.right).customer;
            root.right = remove(root.right, root.customer.accountNumber);
        }
        displayArea.append("Account removed successfully.\n");

        // Save customer data after removing the account
        saveCustomerData();

        return root;
    }

    private Node findMin(Node root) {
        while (root.left != null) {
            root = root.left;
        }
        return root;
    }

    private void deposit(JTextField accountNumberField, JTextField amountField) {
        try {
            int accountNumber = Integer.parseInt(accountNumberField.getText());
            float amount = Float.parseFloat(amountField.getText());

            root = deposit(root, accountNumber, amount);
        } catch (NumberFormatException e) {
            displayArea.append("Invalid input. Please enter valid data.\n");
        }
    }

    private Node deposit(Node root, int accountNumber, float amount) {
        if (root == null) {
            displayArea.append("Account not found.\n");
            return root;
        }
        if (accountNumber < root.customer.accountNumber) {
            root.left = deposit(root.left, accountNumber, amount);
        } else if (accountNumber > root.customer.accountNumber) {
            root.right = deposit(root.right, accountNumber, amount);
        } else {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Transaction transaction = new Transaction("Deposit", amount, timestamp);
            root.customer.transactions.add(transaction);

            root.customer.balance += amount;
            displayArea.append("Deposit completed successfully.\n");

            // Save customer data after deposit
            saveCustomerData();
        }
        return root;
    }

    private void withdraw(JTextField accountNumberField, JTextField amountField) {
        try {
            int accountNumber = Integer.parseInt(accountNumberField.getText());
            float amount = Float.parseFloat(amountField.getText());

            root = withdraw(root, accountNumber, amount);
        } catch (NumberFormatException e) {
            displayArea.append("Invalid input. Please enter valid data.\n");
        }
    }

    private Node withdraw(Node root, int accountNumber, float amount) {
        if (root == null) {
            displayArea.append("Account not found.\n");
            return root;
        }
        if (accountNumber < root.customer.accountNumber) {
            root.left = withdraw(root.left, accountNumber, amount);
        } else if (accountNumber > root.customer.accountNumber) {
            root.right = withdraw(root.right, accountNumber, amount);
        } else {
            if (root.customer.balance >= amount) {
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                Transaction transaction = new Transaction("Withdrawal", -amount, timestamp);
                root.customer.transactions.add(transaction);

                root.customer.balance -= amount;
                displayArea.append("Withdrawal completed successfully.\n");

                // Save customer data after withdrawal
                saveCustomerData();
            } else {
                displayArea.append("Insufficient balance for withdrawal.\n");
            }
        }
        return root;
    }

    private void saveCustomerData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            saveCustomersInOrder(root, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCustomersInOrder(Node root, PrintWriter writer) {
        if (root != null) {
            saveCustomersInOrder(root.left, writer);
            writer.println(root.customer.accountNumber);
            writer.println(root.customer.name);
            writer.println(root.customer.balance);
            writer.println("Transaction History:");

            for (Transaction transaction : root.customer.transactions) {
                writer.println(transaction.transactionType);
                writer.println(transaction.amount);
                writer.println(transaction.timestamp);
            }

            writer.println("**------------------------**");
            saveCustomersInOrder(root.right, writer);
        }
    }

    private void loadCustomerData() {
        try (Scanner scanner = new Scanner(new File(DATA_FILE))) {
            root = null; // Reset the tree when loading data
            while (scanner.hasNextInt()) {
                int accountNumber = scanner.nextInt();
                String name = scanner.next();
                float balance = scanner.nextFloat();
                Customer customer = new Customer(accountNumber, name, balance);

                // Load transaction history
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.equals("Transaction History:")) {
                        break;
                    }
                }
                while (scanner.hasNextLine()) {
                    String transactionType = scanner.nextLine();
                    float amount = scanner.nextFloat();
                    String timestamp = scanner.nextLine().trim();
                    customer.transactions.add(new Transaction(transactionType, amount, timestamp));

                    if (scanner.hasNextLine()) {
                        String delimiter = scanner.nextLine();
                        if (!delimiter.equals("**------------------------**")) {
                            break;
                        }
                    }
                }

                root = insert(root, customer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BankManagementSystemGUI().setVisible(true);
            }
        });
    }
}