package dao;

import java.sql.Connection;
import java.util.List;

import model.Transaction;

public interface TransactionDAO {

	void addTransaction(Transaction transaction);
	
	void addTransaction(Transaction transaction, Connection con);
	
	void getTransactionsByAccount(int accountId);
	
	List<Transaction> getTransactionHistory(int accountId);
}
