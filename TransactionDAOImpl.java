package dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import model.Transaction;
import util.DBConnection;

public class TransactionDAOImpl implements TransactionDAO{

	@Override
	public void addTransaction(Transaction transaction) {
		// TODO Auto-generated method stub
		String sql = "INSERT INTO transactions (from_account, to_account, amount, type) VALUES (?, ?, ?, ?)";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, transaction.getFromAccount());
            ps.setInt(2, transaction.getToAccount());
            ps.setDouble(3, transaction.getAmount());
            ps.setString(4, transaction.getType());

            int rows = ps.executeUpdate();

            System.out.println(rows + " transaction added successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	@Override
	public void addTransaction(Transaction transaction, Connection con) {

	    String sql = "INSERT INTO transactions (from_account, to_account, amount, type) VALUES (?, ?, ?, ?)";

	    try (PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setInt(1, transaction.getFromAccount());
	        ps.setInt(2, transaction.getToAccount());
	        ps.setDouble(3, transaction.getAmount());
	        ps.setString(4, transaction.getType());

	        ps.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	@Override
	public void getTransactionsByAccount(int accountId) {

	    String sql = "SELECT * FROM transactions WHERE from_account = ? OR to_account = ?";

	    try (
	        Connection con = DBConnection.getConnection();
	        PreparedStatement ps = con.prepareStatement(sql)
	    ) {

	        ps.setInt(1, accountId);
	        ps.setInt(2, accountId);

	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {

	            int id = rs.getInt("id");
	            int fromAccount = rs.getInt("from_account");
	            int toAccount = rs.getInt("to_account");
	            double amount = rs.getDouble("amount");
	            String type = rs.getString("type");
	            Timestamp transactionDate = rs.getTimestamp("transaction_date");

	            System.out.println("Transaction ID: " + id);
	            System.out.println("From Account: " + fromAccount);
	            System.out.println("To Account: " + toAccount);
	            System.out.println("Amount: " + amount);
	            System.out.println("Type: " + type);
	            System.out.println("Date: " + transactionDate);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	@Override
	public List<Transaction> getTransactionHistory(int accountId) {

	    List<Transaction> transactions = new ArrayList<>();
	    String sql = "SELECT * FROM transactions WHERE from_account = ? OR to_account = ? ORDER BY transaction_date DESC";

	    try (
	        Connection con = DBConnection.getConnection();
	        PreparedStatement ps = con.prepareStatement(sql)
	    ) {
	        ps.setInt(1, accountId);
	        ps.setInt(2, accountId);

	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            Transaction t = new Transaction(
	                rs.getInt("id"),
	                rs.getInt("from_account"),
	                rs.getInt("to_account"),
	                rs.getDouble("amount"),
	                rs.getString("type"),
	                rs.getTimestamp("transaction_date")
	            );
	            transactions.add(t);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return transactions;
	}

}
