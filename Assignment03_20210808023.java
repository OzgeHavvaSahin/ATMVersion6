// @author: Ozge Havva Sahin @version: 06\04\2022

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
public class Assignment03_20210808023{
      public static void main(String[] args) throws InvalidAmountException, AccountNotFoundException, IOException { 
        Bank b = new Bank("My Bank", "My Bank's Address");
        b.addCompany(1, "Company 1");
        b.getCompany(1).openAccount("1234", 0.05);
        b.addAccount(b.getCompany(1).getAccount("1234"));
        b.getAccount("1234").deposit(500000);
        b.getCompany(1).getAccount("1234").deposit(500000);
        b.getCompany(1).openAccount("1235", 0.03);
        b.addAccount(b.getCompany(1).getAccount("1235"));
        b.getCompany(1).getAccount("1235").deposit(25000);
        b.addCompany(2, "Company 2");
        b.getCompany(2).openAccount("2345", 0.03);
        b.addAccount(b.getCompany(2).getAccount("2345"));
        b.getCompany(2).getAccount("2345").deposit(350);
        b.addCustomer(1, "Customer" , "1");
        b.addCustomer(2, "Customer", "2");
        Customer c = b.getCustomer(1);
        c.openAccount("3456");
        c.openAccount("3457");
        c.getAccount("3456").deposit(150);
        c.getAccount("3457").deposit(250);
        c = b.getCustomer(2);
        c.openAccount("4567");
        c.getAccount("4567").deposit(1000);
        b.addAccount(c.getAccount("4567"));
        c = b.getCustomer(1);
        b.addAccount(c.getAccount("3456"));
        b.addAccount(c.getAccount("3457"));
        System.out.println(b.toString());
        
          Collection<Transaction> ts = new ArrayList<Transaction>();
          ts.add(new Transaction(3, "4567", 4000));
          ts.add(new Transaction(1, "4567", 3100));
         b.processTransactions(ts, "exampleOut.log");
          System.out.println(b);
          

      }
        
       
}


class Bank{
    private String name;
    private String address;
    private ArrayList<Company> companies = new ArrayList<Company>();
    private ArrayList<Customer> customers = new ArrayList<Customer>();
    private ArrayList<Account> accounts =new ArrayList<Account>();
    
    public Bank(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }
   
    public void setName(String name) {
        this.name = name;
    }
   
    public String getAddress() {
        return address;
    }
   
    public void setAddress(String address) {
        this.address = address;
    }
    
    public void addCustomer(int id, String name,String surname){
        customers.add(new Customer(id, name, surname));
    }

    public void addCompany(int id, String name){
        companies.add(new Company(id, name));
    }
   
    public void addAccount(Account account){
        accounts.add(account);
    }
    
    public Customer getCustomer(int id) throws CustomerNotFoundException{
        for (int i = 0; i < customers.size(); i++) {
            if(customers.get(i).getId()==id){
                return customers.get(i);
            }
        }       
        throw new CustomerNotFoundException(id);
    } 
    
    public Customer getCustomer(String name, String surname) throws CustomerNotFoundException{
        for (int i = 0; i < customers.size(); i++) {
            if(customers.get(i).getName().equals(name) && customers.get(i).getSurname().equals(surname)){
                return customers.get(i);
            }
        }  
        throw new CustomerNotFoundException(name,surname);
    }

    public Company getCompany(int id) throws CompanyNotFoundException{
        for (int i = 0; i < companies.size(); i++) {
            if(companies.get(i).getId()==id){
                return companies.get(i);
            }
        }
        throw new CompanyNotFoundException(id);
    }

    public Company getCompany(String name) throws CompanyNotFoundException{
        for (int i = 0; i < companies.size(); i++) {
            if(companies.get(i).getName().equals(name)){
                return companies.get(i);
            }
        }
        throw new CompanyNotFoundException(name);
    }

    public Account getAccount(String acctNum) throws AccountNotFoundException{
        for (int i = 0; i < accounts.size(); i++) {
            if(accounts.get(i).getAcctNum().equals(acctNum)){
                return accounts.get(i);
            }
        }
        throw new AccountNotFoundException(acctNum);
    }

    public void closeAccount(String acctNum) throws RuntimeException{
        for (int i = 0; i < accounts.size() ; i++) {
            if(accounts.get(i).getAcctNum().equals(acctNum)){
                if(accounts.get(i).getBalance()>0){
                    throw new BalanceRemainingException(accounts.get(i).getBalance());
                }
                accounts.remove(i);
                return;
            } 
        }
        throw new AccountNotFoundException(acctNum);

    }

    public void transferFunds(String accountFrom,String accountTo,double amount) throws RuntimeException{
        try {
            getAccount(accountFrom);
            getAccount(accountTo);
        } catch (AccountNotFoundException e) {
            throw e;
        }
        if(amount <= 0){
            throw new InvalidAmountException(amount);
        }
        if(getAccount(accountFrom).getBalance()<amount){
            throw new InvalidAmountException(amount);
        }

        getAccount(accountFrom).withdrawal(amount);
        getAccount(accountTo).deposit(amount);
        
    }
    
    public String toString()
	    {
	    	
	    	String firstline = name + "\t" + address;
	    	String company = "";
            String custumer = "";
	    	for (int i = 0; i < companies.size(); i++) {
	    		Company c = companies.get(i);
	    		company += "\t" + c.getName() +"\n";
	    		company += c.getBusinessAccount() +"\n";
			}
            for (int i = 0; i < customers.size(); i++) {
	    		Customer c = customers.get(i);
	    		custumer += "\t" + c.getName();
                custumer += " " + c.getSurname()+"\n";
	    		custumer += c.getPersonalAccount()+"\n";
			}
	    	firstline += "\n" + company + "\n" + custumer;
			return firstline;
	    	
	    }

        public void processTransactions(Collection<Transaction> transactions, String outfile)throws InvalidAmountException,IOException,AccountNotFoundException{
            ArrayList<Transaction> arrList= new ArrayList<>();
            for (Transaction transaction : transactions) {
                arrList.add(transaction);
                
            }
            Collections.sort(arrList,Comparator.comparing(Transaction::getType));
    
            File file = new File(outfile);
            FileWriter fw= new FileWriter(file);
            PrintWriter pw = new PrintWriter(fw);
            for (int i = 0; i < arrList.size(); i++) {
                try {
                    if(arrList.get(i).getType() == 1){
                        for (Account acc : accounts) {
                            if(acc.getAcctNum().equals(arrList.get(i).getTo())){
                                acc.deposit(arrList.get(i).getAmount());
                            }
                            
                        }
                    }else if(arrList.get(i).getType() == 2){
                        transferFunds(arrList.get(i).getFrom(), arrList.get(i).getTo(), arrList.get(i).getAmount());
                    }else if(arrList.get(i).getType() == 3){
                        for (Account acco : accounts) {
                            if(acco.getAcctNum().equals(arrList.get(i).getFrom())){
                                acco.withdrawal(arrList.get(i).getAmount());
                            }
                            
                        }
                    }
                } catch (Exception exception) {
                    pw.print("\nERROR:" + exception.getClass().getSimpleName() + ": " + arrList.get(i).getType() + "\t" + arrList.get(i).getTo() + "\t" + arrList.get(i).getFrom()+ arrList.get(i).getAmount() );
                }
            }
            pw.close();
        }
    }

class Account{
    private String acctNum;
    private double balance;
    public Account(String acctNum){
        this.acctNum = acctNum;
        this.balance = 0;
    }
    public Account(String acctNum , double balance)throws InvalidAmountException{
        this.acctNum = acctNum;
        if(balance < 0){
            throw new InvalidAmountException(balance);
        }else{
            this.balance = balance;
        }
    
    }
    public String getAcctNum() {
        return acctNum;
    }
    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
    public void deposit(double amount)throws InvalidAmountException{
        if(amount < 0){
            throw new InvalidAmountException(amount);
        }else{
            this.balance += amount; 
        }
    }
    public void setAcctNum(String acctNum) {
        this.acctNum = acctNum;
    }
    public void withdrawal(double amount) throws InvalidAmountException{ 
        if(amount < 0 && amount >= balance){
            throw new InvalidAmountException(amount);
        }else{
            this.balance -= amount;
        }
    }
    public String toString(){
        return "Account " + acctNum + " has " + balance ;
    }
}

class PersonalAccount extends Account{
    private String name,surname,PIN;
    public PersonalAccount(String acctNum, String name,String surname){
        super(acctNum);
        this.name = name;
        this.surname = surname;
        Random rand = new Random();
        this.PIN = Integer.toString(rand.nextInt(1000,9999));
    }
    public PersonalAccount(String acctNum, String name,String surname,double balance){
        this(acctNum, name, surname);
        setBalance(balance);
    }
    public String toString(){
        return "Account " + getAcctNum() + " belonging to " + name + " " + surname.toUpperCase() + " has " + getBalance(); 
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getPIN() {
        return PIN;
    }
    public void setPIN(String pIN) {
        this.PIN = pIN;
    }
}

class BusinessAccount extends Account{
    private double rate;
    public BusinessAccount( String acctNum,double rate) throws InvalidAmountException{
        super(acctNum);
        if(rate < 0 ){
            throw new InvalidAmountException(rate);
        }else{
            this.rate=rate;
        }
    }
    public BusinessAccount( String acctNum,double balance,double rate)throws InvalidAmountException{
        super(acctNum,balance);
        if(rate < 0 ){
            throw new InvalidAmountException(rate);
        }else{
            this.rate=rate;
        }
    }
    public double getRate() {
        return rate;
    }
    public void setRate(double rate) {
        if(rate < 0 ){
            throw new InvalidAmountException(rate);
        }else{
            this.rate=rate;
        }
    }
    public double calculateInterest(){
        return getBalance()*this.rate;
    }

}

class Customer {
    private int id;
    private String name,surname;
    private ArrayList <PersonalAccount> personalAccounts= new ArrayList<PersonalAccount>();
    
    public Customer(){
        this.id=1234;
        this.name="ali";
        this.surname="aba";
        this.personalAccounts= new ArrayList<PersonalAccount>();
    }
    
    public Customer(int id, String name, String surname) throws InvalidAmountException {
        if(id < 0 ){
            throw new InvalidAmountException(id);
        }else{
            this.id= id;
        }
        this.name = name;
        this.surname = surname;
    }
    
    public String getName() {
        return name;
    }
    
    public int getId() {
        return id;
    }
   
    public void setId(int id) throws InvalidAmountException {
        if(id < 0 ){
            throw new InvalidAmountException(id);
        }else{
            this.id= id;
        }
    }
   
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSurname() {
        return surname;
    }
   
    public void setSurname(String surname) {
        this.surname = surname;
    }
    
    public void openAccount(String acctNum){
        personalAccounts.add(new PersonalAccount(acctNum,name,surname)); 
    }
    
    public PersonalAccount getAccount(String acctNum) throws AccountNotFoundException {
        for (int i = 0; i < personalAccounts.size(); i++) {
           if(personalAccounts.get(i).getAcctNum().equals(acctNum)){
               return personalAccounts.get(i);
           }
        }       
        throw new AccountNotFoundException(acctNum);
         
    }
   
    public void closeAccount(String acctNum)throws RuntimeException{
        for (int i = 0; i < personalAccounts.size(); i++) {
            if(personalAccounts.get(i).getAcctNum().equals(acctNum)){
                if(personalAccounts.get(i).getBalance()>0){
                    throw new BalanceRemainingException(personalAccounts.get(i).getBalance());
                }else{
                    personalAccounts.remove(i);
                }
                
            }else{
                throw new AccountNotFoundException(acctNum);
            }
         }
    }

    public String getPersonalAccount() {
        String accounts = "";
        for (int i = 0; i < personalAccounts.size(); i++) {
            accounts += "\t\t"+ personalAccounts.get(i).getAcctNum() + "\t"+ personalAccounts.get(i).getBalance()+ "\n";
        }
        return accounts;
    }
   
    public String toString(){
        return name + " " + surname.toUpperCase();
    }
        
 }

class AccountNotFoundException extends RuntimeException{
    private String acctNum;
    public AccountNotFoundException(String acctNum){
        this.acctNum = acctNum;
        System.out.println(toString());  
    }
    public String toString(){
        return "AccountNotFoundException: " + acctNum;
    }
}

class BalanceRemainingException extends RuntimeException{
    private double balance;
    
    public BalanceRemainingException(double balance){
        this.balance=balance;
        System.out.println(toString());
    }
    
    public double getBalance() {
        return balance;
    }

    public String toString(){
        return "BalanceRemainingException: " + balance;
    } 
}

class Company{
    private int id;
    private String name;
    ArrayList <BusinessAccount> ba = new ArrayList<BusinessAccount>();

    public Company(int id,String name ) throws InvalidAmountException{
        this(name);
        if(id < 0 ){
            throw new InvalidAmountException(id);
        }else{
            this.id= id;
        }
    }    

    public Company(String name){
        this.name = name;
    }
   
    public int getId() {
        return id;
    }

    public void setId(int id) throws InvalidAmountException {
        if(id < 0 ){
            throw new InvalidAmountException(id);
        }else{
            this.id= id;
        }
    }

    public String getName() {
        return name;
    }
   
    public void setName(String name) {
        this.name = name;
    }
   
   public void openAccount( String acctNum, double rate){
        BusinessAccount b = new BusinessAccount(acctNum, rate);
        ba.add(b);
    }
   
    public BusinessAccount getAccount(String acctNum){
        for (int i = 0; i < ba.size(); i++) {
            if(ba.get(i).getAcctNum().equals(acctNum)){
                return ba.get(i);
            }
        }     
        throw new AccountNotFoundException(acctNum);
        
        
    }
    
    public void closeAccount(String acctNum){
        for (int i = 0; i < ba.size(); i++) {
            if(ba.get(i).getAcctNum().equals(acctNum)){
                if(ba.get(i).getBalance()>0){
                    throw new BalanceRemainingException(ba.get(i).getBalance());
                }
            ba.remove(i);    
            }        
        }
        throw new AccountNotFoundException(acctNum);
    }
    
    public String getBusinessAccount() {
        String accounts = "";
        for (int i = 0; i < ba.size(); i++) {
            BusinessAccount temp = ba.get(i);
            accounts += "\t\t"+ temp.getAcctNum() + "\t"+ temp.getRate() +"\t" + temp.getBalance() + "\n";
        }
        return accounts;
    }

    public String toString(){ 
        return name;
    }
}

class CustomerNotFoundException extends RuntimeException{
    private int id;
    private String name,surname;
    public CustomerNotFoundException(int id){
        this.name = null;
        this.surname= null;
        this.id=id;
        System.out.println(toString());
    }
    public CustomerNotFoundException(String name, String surname){
        this.name = name;
        this.surname= surname;
        this.id=0;
        System.out.println(toString());
    }
    public String toString(){
        if(name == null && surname == null){
            return "CustomerNotFoundException:name " + name + " " + surname;
        }
        if(id !=0){
             return "CustomerNotFoundException:id "+ id;
        }
        return null;
    }
}

class CompanyNotFoundException extends RuntimeException{
    private int id;
    private String name;

    public CompanyNotFoundException(int id){
        this.id=id;
        this.name=null;
       System.out.println(toString());

    }

    public CompanyNotFoundException(String name){
        this.id=-1;
        this.name=name;
       System.out.println(toString());

    }

    public String toString(){
        if(name != null){
            return "CompanyNotFoundException:name-"+name;
        }
        return "CompanyNotFoundException:id-"+id;
    }
}

class InvalidAmountException extends RuntimeException{
    private double amount;
    
    public InvalidAmountException(double amount){
        this.amount=amount;
        System.out.println(toString());
    }

    public String toString(){
        return "InvalidAmountException:" + amount;
    }
}

class Transaction implements Comparable <Transaction>{
    private int type;
    private String to;
    private String from;
    private double amount;
    
    public Transaction(int type, String to, String from, double amount) throws InvalidParameterException {
        if(type<=0 && type >3 ){
            throw new InvalidParameterException();
        }else{
            this.type = type;
        }

        this.to = to;
        this.from = from;
        this.amount = amount;
    }

    public Transaction(int type, String s, double amount) throws InvalidParameterException {
        if(type == 1 || type == 3 ){
            this.type = type;
        }else{
            throw new InvalidParameterException();
        }
        if(type == 1){
            this.to = s;
        }

        if(type == 3){
            this.from = s;
        }
        this.amount = amount;
    }


    public int getType() {
        return type;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public double getAmount() {
        return amount;
    }

    public int compareTo(Transaction tra) {
        if(this.type == 1 || this.type == 2){
            return this.to.compareTo(tra.getTo());
        }if(this.type == 3){
            return this.from.compareTo(tra.getFrom());
        }
        return 0;
    }

    @Override
    public String toString() {
        if(type == 1){
            return "type : " + type + "to : " + to + "amount : " + amount;
        }else if(type == 2){
            return "type : " + type + "to : " + to + "from : " +from + "amount : " + amount;
        }else if(type == 3){
            return "type : " + type + "from : " + from + "amount : " + amount;
        }
        return null;
    }


    
    
}