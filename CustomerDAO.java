package dao;

import model.Customer;

public interface CustomerDAO {
	
	void addCustomer(Customer customer);
	
	Customer login(String username, String password);
	
	Customer getCustomerById(int id);
	
    void updateBalance(int id, double balance);
    
    void deleteCustomer(int id);
    
    void deposit(int id, double amount);
    
    void withdraw(int id, double amount);
    
    void transfer(int fromId, int toId, double amount);
    
    void getAllCustomers();

}
