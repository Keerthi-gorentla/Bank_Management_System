package model;

public class Customer {

    private int id;
    private String name;
    private double balance;
    private String username;
    private String password; // stores the HASHED password

    public Customer(int id, String name, double balance, String username, String password) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.username = username;
        this.password = password;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getBalance() { return balance; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}