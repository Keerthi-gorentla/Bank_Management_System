package model;

import java.sql.Timestamp;

public class Transaction {
	
	private int id;
    private int fromAccount;
    private int toAccount;
    private double amount;
    private String type;
    private Timestamp transactionDate;
    
    
    public Transaction(int id, int fromAccount, int toAccount, double amount, String type, Timestamp transactionDate) {
        this.id = id;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.type = type;
        this.transactionDate = transactionDate;
 
    }

    public int getId() {
		return id;
	}


	public int getFromAccount() {
		return fromAccount;
	}


	public int getToAccount() {
		return toAccount;
	}


	public double getAmount() {
		return amount;
	}


	public String getType() {
		return type;
	}
	
	public Timestamp getTransactionDate() {
	    return transactionDate;
	}


	
}
