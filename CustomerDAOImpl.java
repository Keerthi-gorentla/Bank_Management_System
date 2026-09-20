package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

import model.Customer;
import model.Transaction;
import util.DBConnection;

public class CustomerDAOImpl implements CustomerDAO {

    @Override
    public void addCustomer(Customer customer) {
        String sql = "INSERT INTO customer (id, name, balance, username, password) VALUES (?, ?, ?, ?, ?)";

        String hashedPassword = BCrypt.hashpw(customer.getPassword(), BCrypt.gensalt());

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, customer.getId());
            ps.setString(2, customer.getName());
            ps.setDouble(3, customer.getBalance());
            ps.setString(4, customer.getUsername());
            ps.setString(5, hashedPassword);

            int rows = ps.executeUpdate();
            System.out.println(rows + " customer added successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Customer getCustomerById(int id) {
        String sql = "SELECT * FROM customer WHERE id = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Customer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("balance"),
                    rs.getString("username"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Customer login(String username, String password) {
        String sql = "SELECT * FROM customer WHERE username = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");

                if (BCrypt.checkpw(password, storedHash)) {
                    return new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("balance"),
                        rs.getString("username"),
                        storedHash
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // login failed
    }

    @Override
    public void updateBalance(int id, double balance) {
        String sql = "UPDATE customer SET balance = ? WHERE id = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setDouble(1, balance);
            ps.setInt(2, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCustomer(int id) {
        String sql = "DELETE FROM customer WHERE id = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Customer deleted successfully.");
            } else {
                System.out.println("Customer not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deposit(int id, double amount) {

        String sql = "UPDATE customer SET balance = balance + ? WHERE id = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setDouble(1, amount);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            System.out.println(rows + " deposit successful.");

            TransactionDAO transactionDAO = new TransactionDAOImpl();

            Transaction transaction = new Transaction(
                0,
                0,
                id,
                amount,
                "DEPOSIT",
                null
            );

            transactionDAO.addTransaction(transaction);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void withdraw(int id, double amount) {

        Customer customer = getCustomerById(id);

        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }

        double currentBalance = customer.getBalance();

        if (currentBalance >= amount) {

            String sql = "UPDATE customer SET balance = balance - ? WHERE id = ?";

            try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
            ) {
                ps.setDouble(1, amount);
                ps.setInt(2, id);

                int rows = ps.executeUpdate();

                System.out.println(rows + " withdrawal successful.");

                TransactionDAO transactionDAO = new TransactionDAOImpl();

                Transaction transaction = new Transaction(
                    0,
                    id,
                    0,
                    amount,
                    "WITHDRAW",
                    null
                );

                transactionDAO.addTransaction(transaction);

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Insufficient balance.");
        }
    }

    @Override
    public void transfer(int fromId, int toId, double amount) {

        Connection con = DBConnection.getConnection();

        try {
            con.setAutoCommit(false);

            String lockSql = "SELECT * FROM customer WHERE id = ? FOR UPDATE";
            double senderBalance;
            double receiverBalance;

            try (PreparedStatement ps = con.prepareStatement(lockSql)) {
                ps.setInt(1, fromId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    System.out.println("Sender not found.");
                    con.rollback();
                    return;
                }
                senderBalance = rs.getDouble("balance");
            }

            try (PreparedStatement ps = con.prepareStatement(lockSql)) {
                ps.setInt(1, toId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    System.out.println("Receiver not found.");
                    con.rollback();
                    return;
                }
                receiverBalance = rs.getDouble("balance");
            }

            if (senderBalance < amount) {
                System.out.println("Insufficient balance.");
                con.rollback();
                return;
            }

            String updateSql = "UPDATE customer SET balance = ? WHERE id = ?";

            try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                ps.setDouble(1, senderBalance - amount);
                ps.setInt(2, fromId);
                ps.executeUpdate();

                ps.setDouble(1, receiverBalance + amount);
                ps.setInt(2, toId);
                ps.executeUpdate();
            }

            TransactionDAO transactionDAO = new TransactionDAOImpl();

            Transaction transaction = new Transaction(
                0, fromId, toId, amount, "TRANSFER", null
            );

            transactionDAO.addTransaction(transaction, con);

            con.commit();

            System.out.println(amount + " transferred successfully.");
            System.out.println("Sender balance: " + (senderBalance - amount));
            System.out.println("Receiver balance: " + (receiverBalance + amount));

        } catch (SQLException e) {

            try {
                con.rollback();
                System.out.println("Transfer failed. Transaction rolled back.");
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }

            e.printStackTrace();

        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getAllCustomers() {

        String sql = "SELECT * FROM customer";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {

                int id = rs.getInt("id");
                String name = rs.getString("name");
                double balance = rs.getDouble("balance");

                System.out.println("ID: " + id);
                System.out.println("Name: " + name);
                System.out.println("Balance: " + balance);
                System.out.println("-------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}