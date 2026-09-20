package app;

import java.util.Scanner;

import dao.CustomerDAO;
import dao.CustomerDAOImpl;
import dao.TransactionDAO;
import dao.TransactionDAOImpl;
import model.Customer;


public class Main {

	

	public static void main(String[] args) {
		
		Scanner scan = new Scanner(System.in);
		// TODO Auto-generated method stub
		CustomerDAO dao = new CustomerDAOImpl();
		
		System.out.println("=================================");
		System.out.println("     WELCOME TO BANK SYSTEM");
		System.out.println("=================================");

		System.out.print("Enter username: ");
		String username = scan.nextLine();

		System.out.print("Enter password: ");
		String password = scan.nextLine();

		Customer loggedInCustomer = dao.login(username, password);

		if (loggedInCustomer == null) {
		    System.out.println("Invalid username or password. Exiting.");
		    scan.close();
		    return;
		}

		System.out.println("Login successful. Welcome, " + loggedInCustomer.getName() + "!");

		
		while (true) {

		    System.out.println("\n===== BANK MANAGEMENT SYSTEM =====");
		    System.out.println("1. Add Customer");
		    System.out.println("2. Get Customer");
		    System.out.println("3. Deposit");
		    System.out.println("4. Withdraw");
		    System.out.println("5. Transfer");
		    System.out.println("6. View Transactions");
		    System.out.println("7. View All Customers");
		    System.out.println("8. Delete Customer");
		    System.out.println("9. Exit");

		    System.out.print("Enter your choice: ");

		    int choice;

		    try {
		        choice = scan.nextInt();
		    } catch (java.util.InputMismatchException e) {
		        System.out.println("Invalid input. Please enter a number.");
		        scan.nextLine(); // clear the bad input so it doesn't loop forever
		        continue; // go back to the top of the menu
		    }
		    switch (choice) {
		    
		    case 1 : {
		        System.out.print("Enter customer ID: ");
		           int id = scan.nextInt();
		           scan.nextLine(); // clears the leftover newline

		           System.out.print("Enter customer name: ");
		           String name = scan.nextLine();

		           System.out.print("Enter initial balance: ");
		           double balance = scan.nextDouble();
		           scan.nextLine(); // clear leftover newline again

		           System.out.print("Choose a username: ");
		           String newUsername = scan.nextLine();

		           System.out.print("Choose a password: ");
		           String newPassword = scan.nextLine();

		           Customer customer = new Customer(id, name, balance, newUsername, newPassword);

		           dao.addCustomer(customer);
		           
		           break;
		   }
			        
		    case 2:  {
		    	
		        System.out.print("Enter customer ID: ");
		        int id = scan.nextInt();

		        Customer customer1 = dao.getCustomerById(id);

		        if (customer1 != null) {
		            System.out.println("Customer ID: " + customer1.getId());
		            System.out.println("Name: " + customer1.getName());
		            System.out.println("Balance: " + customer1.getBalance());
		        } else {
		            System.out.println("Customer not found.");
		        }

		        break;
		    }
		    	
		    
		    case 3:{
		        System.out.print("Enter deposit amount: ");
		        double amount = scan.nextDouble();

		        if (amount <= 0) {
		            System.out.println("Deposit amount must be greater than 0.");
		        } else {
		            dao.deposit(loggedInCustomer.getId(), amount);
		        }

		        break;
		    }   
		    case 4:{
		        System.out.print("Enter withdrawal amount: ");
		        double amount = scan.nextDouble();

		        if (amount <= 0) {
		            System.out.println("Withdrawal amount must be greater than 0.");
		        } else {
		            dao.withdraw(loggedInCustomer.getId(), amount);
		        }

		        break;
		    }  
		    case 5:{
		        int fromId = loggedInCustomer.getId();

		        System.out.print("Enter receiver ID: ");
		        int toId = scan.nextInt();

		        System.out.print("Enter transfer amount: ");
		        double amount = scan.nextDouble();

		        if (fromId == toId) {
		            System.out.println("Sender and receiver cannot be the same.");
		        } else if (amount <= 0) {
		            System.out.println("Transfer amount must be greater than 0.");
		        } else {
		            dao.transfer(fromId, toId, amount);
		        }

		        break;
		    }
		    
		case 6:{
		    System.out.print("Enter customer ID: ");
		    int id = scan.nextInt();

		    TransactionDAO transactionDAO = new TransactionDAOImpl();
		    transactionDAO.getTransactionsByAccount(id);

		    break;
		}
		    
		case 7:{
		    dao.getAllCustomers();
		    break;
		}
		    
		case 8:{
		    System.out.print("Enter customer ID: ");
		    int id = scan.nextInt();

		    dao.deleteCustomer(id);

		    break;
		}
		    
		case 9: {
		    System.out.println("Thank you for using Bank Management System!");
		    scan.close();
		    return;
		}
		    
		default:{
		    System.out.println("Invalid choice. Please try again.");
		    break;
		}
	    }

     }
   }
}





