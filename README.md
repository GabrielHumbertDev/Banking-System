# Banking Management System

[![Java Version](https://img.shields.io/badge/Java-8-blue.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.x-red.svg)](https://maven.apache.org/)
[![JUnit](https://img.shields.io/badge/JUnit-5.7.0-green.svg)](https://junit.org/junit5/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A comprehensive Java-based banking management system featuring customer account management, transaction processing, automated email alerts, and safety deposit box allocation with concurrent access control.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Core Components](#core-components)
- [Design Patterns](#design-patterns)
- [Testing](#testing)
- [Usage Examples](#usage-examples)
- [Contributing](#contributing)
- [License](#license)

---

## 🎯 Overview

This Banking Management System is an enterprise-grade Java application designed to manage banking operations including:

- **Customer Management**: Support for both individual (Person) and corporate (Company) customers
- **Account Management**: Checking and Savings accounts with distinct business rules
- **Transaction Processing**: Deposits, withdrawals with automatic validation
- **Real-time Alerts**: Email notifications for transactions, minimum balance violations, and deposit box operations
- **Safety Deposit Box Management**: Thread-safe allocation system with concurrent access control
- **Fee Calculation**: Dynamic fee calculation based on account balance tiers

The system follows industry-standard design patterns and SOLID principles, ensuring maintainability, scalability, and extensibility.

---

## ✨ Features

### 👥 Customer & Account Management
- **Multiple Customer Types**:
  - Individual customers (Person)
  - Corporate customers (Company) with special fee structures
- **Account Types**:
  - **Checking Accounts**: With check number management and minimum balance enforcement
  - **Savings Accounts**: With interest calculation and overdraft protection
- **Automatic ID Generation**: Sequential, unique IDs for customers and accounts
- **Input Validation**: Comprehensive validation for all user inputs

### 💰 Transaction Processing
- **Deposit Operations**: Secure deposit processing with validation
- **Withdrawal Operations**: 
  - Overdraft prevention for savings accounts
  - Automatic minimum balance checking for checking accounts
- **Balance Corrections**: Administrative balance adjustment capability
- **Transaction Validation**: Blocks negative, zero, and infinite amounts

### 📧 Real-time Email Alerts
- **Transaction Alerts**: 
  - Instant email notifications for deposits
  - Instant email notifications for withdrawals
  - Standardized message format with transaction details
- **Minimum Balance Alerts**:
  - Automatic detection of balance violations
  - Fine application ($25 fine when below $50 minimum)
  - Email notification with fine details
- **Deposit Box Alerts**:
  - Allocation confirmation emails
  - Release/revocation notification emails
- **Delivery Failure Handling**: Graceful handling of email delivery issues

### 🔒 Safety Deposit Box Management
- **Thread-Safe Allocation**: Concurrent access control using synchronized methods
- **Wait/Notify Pattern**: Efficient thread management for box requests
- **Capacity Management**: Configurable maximum number of boxes
- **Status Tracking**: Real-time tracking of box allocation status

### 💵 Fee Management
- **Tiered Fee Structure**:
  - Balance ≤ $100: $20 fee
  - Balance $101-$500: $15 fee
  - Balance $501-$1000: $10 fee
  - Balance $1001-$2000: $5 fee
  - Balance > $2000: $0 fee
- **Customer-Specific Charging**:
  - Person: Standard charge from all accounts
  - Company: Double charge on savings accounts

---

## 🛠️ Technology Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 8 | Core programming language |
| **Maven** | 3.x | Build automation and dependency management |
| **JUnit 5** | 5.7.0 | Unit testing framework |
| **Mockito** | 3.12.4 | Mocking framework for unit tests |
| **Mockito Inline** | 3.12.4 | Static method mocking |
| **Jakarta Mail** | 2.1.2 | Email functionality |

---

## 📁 Project Structure

```
banking-system/
│
├── pom.xml                                    # Maven configuration
│
├── src/main/java/com/github/gabrielhumbertdev/
│   │
│   ├── dao/                                   # Data Access Layer
│   │   ├── AccountReaderDAO.java             # Interface for reading accounts
│   │   └── AccountWriterDAO.java             # Interface for account write operations
│   │
│   ├── model/                                 # Domain Model Layer
│   │   ├── account/
│   │   │   ├── Account.java                  # Abstract base account class
│   │   │   ├── CheckingAccount.java          # Checking account with minimum balance
│   │   │   └── SavingsAccount.java           # Savings account with interest
│   │   │
│   │   ├── customer/
│   │   │   ├── Customer.java                 # Abstract base customer class
│   │   │   └── Person.java                   # Individual customer
│   │   │
│   │   ├── depositbox/
│   │   │   ├── SafetyDepositBox.java         # Base deposit box
│   │   │   └── SmallSafetyDepositBox.java    # Small deposit box variant
│   │   │
│   │   └── organisation/
│   │       └── Company.java                  # Corporate customer
│   │
│   ├── runner/
│   │   └── Runner.java                       # Main application entry point
│   │
│   ├── service/                               # Business Logic Layer
│   │   ├── AccountController.java            # Main orchestration controller
│   │   ├── AccountService.java               # Account service interface
│   │   ├── AccountServiceImpl.java           # Account service implementation
│   │   ├── AlertService.java                 # Transaction alert service
│   │   ├── FeeCalculatorService.java         # Fee calculator interface
│   │   ├── FeeCalculatorServiceImpl.java     # Fee calculator implementation
│   │   ├── SafetyDepositBoxAlertService.java # Deposit box alert service
│   │   └── SafetyDepositBoxService.java      # Deposit box management (Singleton)
│   │
│   └── util/
│       └── EmailUtil.java                    # Jakarta Mail email utility
│
└── src/test/java/com/github/gabrielhumbertdev/service/
    ├── AccountControllerMinimumBalanceTest.java      # Minimum balance tests
    ├── AccountServiceImplTest.java                   # Service layer tests
    ├── AlertServiceMinimumBalanceAlertTest.java      # Alert tests
    ├── EmailUtilComprehensiveTest.java               # Email utility tests
    ├── FeeCalculatorServiceImplTest.java             # Fee calculation tests
    ├── SafetyDepositBoxServiceAlertTest.java         # Box alert tests
    └── SafetyDepositBoxServiceTest.java              # Concurrent box tests
```

### Package Organization

```
com.github.gabrielhumbertdev
├── dao                    # Data Access Objects (Interfaces)
├── model                  # Domain entities
│   ├── account           # Account-related entities
│   ├── customer          # Customer-related entities
│   ├── depositbox        # Deposit box entities
│   └── organisation      # Organization entities
├── runner                # Application entry points
├── service               # Business logic services
└── util                  # Utility classes
```

---

## 🏗️ Architecture

The system follows a **layered architecture** pattern with clear separation of concerns:

```
┌─────────────────────────────────────────────┐
│         Presentation/Runner Layer           │
│  (Runner.java, AccountController.java)      │
│  - User interaction                         │
│  - Request orchestration                    │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│            Service Layer                    │
│  (AccountService, AlertService, etc.)       │
│  - Business logic                           │
│  - Transaction management                   │
│  - Alert generation                         │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│            DAO Layer                        │
│  (AccountReaderDAO, AccountWriterDAO)       │
│  - Data persistence abstraction             │
│  - CRUD operations                          │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│         Model/Domain Layer                  │
│  (Account, Customer, SafetyDepositBox)      │
│  - Domain entities                          │
│  - Business rules                           │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│            Utility Layer                    │
│  (EmailUtil - Jakarta Mail)                 │
│  - Cross-cutting concerns                   │
│  - Technical services                       │
└─────────────────────────────────────────────┘
```

### Data Flow

1. **User Request** → Runner/Controller
2. **Request Processing** → Service Layer (business logic)
3. **Data Access** → DAO Layer (persistence)
4. **Domain Operations** → Model Layer (business rules)
5. **Notifications** → Utility Layer (email alerts)

---

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK) 8** or higher
- **Apache Maven 3.x**
- **Git** (for cloning the repository)
- **SMTP Server** (for email functionality)

### Installation

1. **Clone the repository**:
```bash
git clone https://github.com/GabrielHumbertDev/banking-system.git
cd banking-system
```

2. **Build the project**:
```bash
mvn clean install
```

3. **Run tests**:
```bash
mvn test
```

4. **Run the application**:
```bash
mvn exec:java -Dexec.mainClass="com.fdmgroup.epic3.runner.Runner"
```

### Configuration

#### Email Configuration

Edit `EmailUtil.java` to configure your SMTP server:

```java
// SMTP configuration
Properties props = new Properties();
props.put("mail.smtp.host", "your-smtp-server.com");  // Your SMTP host
props.put("mail.smtp.port", "587");                   // SMTP port
props.put("mail.smtp.auth", "true");
props.put("mail.smtp.starttls.enable", "true");

// Authentication
return new PasswordAuthentication(
    "your-email@example.com",    // Your email
    "your-password"               // Your password
);
```

---

## 🔧 Core Components

### 1. Account Hierarchy

#### Account (Abstract Base Class)
- Manages balance, account ID generation
- Provides deposit/withdraw/correctBalance operations
- Validates positive amounts and finite values

#### CheckingAccount
```java
- Minimum Balance: $50.00
- Fine for Violation: $25.00
- Check Number Management: Sequential issuance
- Overdraft: Allowed (with potential fine)
```

#### SavingsAccount
```java
- Overdraft Protection: Prevents negative balance
- Interest Rate: Configurable percentage
- Interest Calculation: addInterest() method
```

### 2. Customer Hierarchy

#### Customer (Abstract Base Class)
- Stores customer ID, name, address, email
- Manages list of accounts
- Abstract method: `chargeAllAccounts(double amount)`

#### Person
- Charges the same amount from each account

#### Company
- Charges 1x from checking accounts
- Charges 2x from savings accounts

### 3. Service Layer

#### AccountController
Central orchestrator for customer and account management:
- Create/remove customers and accounts
- Process deposits and withdrawals
- Apply minimum balance fines
- Trigger email alerts

#### AlertService
Manages all email notifications:
- Transaction alerts (deposits/withdrawals)
- Minimum balance violation alerts
- Uses streams/lambdas for customer lookup
- Handles email delivery failures

#### SafetyDepositBoxService (Singleton)
Thread-safe deposit box management:
- Synchronized allocation/release methods
- Wait/notify pattern for concurrent access
- Configurable box capacity

#### FeeCalculatorService
Calculates maintenance fees based on balance tiers

### 4. Utility Layer

#### EmailUtil
Jakarta Mail wrapper for SMTP email delivery:
- Configurable SMTP settings
- Authentication support
- Exception handling

---

## 🎨 Design Patterns

The system implements several industry-standard design patterns:

### 1. Singleton Pattern
**Class**: `SafetyDepositBoxService`
```java
private static SafetyDepositBoxService safetyDepositBoxService;

public static synchronized SafetyDepositBoxService getInstance() {
    if (safetyDepositBoxService == null) {
        safetyDepositBoxService = new SafetyDepositBoxService();
    }
    return safetyDepositBoxService;
}
```
**Purpose**: Ensures single instance for deposit box management across the application

### 2. Template Method Pattern
**Classes**: `Customer` → `Person`, `Company`
```java
// Abstract method in Customer
public abstract void chargeAllAccounts(double amount);

// Person implementation
@Override
public void chargeAllAccounts(double amount) {
    for (Account a : getAccounts()) {
        a.withdraw(amount);
    }
}

// Company implementation  
@Override
public void chargeAllAccounts(double amount) {
    for (Account a : getAccounts()) {
        if (a instanceof SavingsAccount) {
            a.withdraw(amount * 2);
        } else {
            a.withdraw(amount);
        }
    }
}
```
**Purpose**: Defines algorithm skeleton in base class, allows subclasses to override specific steps

### 3. DAO (Data Access Object) Pattern
**Interfaces**: `AccountReaderDAO`, `AccountWriterDAO`
```java
public interface AccountReaderDAO {
    List<Account> readAccounts();
}

public interface AccountWriterDAO {
    Account createAccount(Account account);
    void deleteAccount(Account account);
}
```
**Purpose**: Abstracts data persistence layer, enabling different storage implementations

### 4. Service Layer Pattern
**Classes**: `AccountService`, `AlertService`, `FeeCalculatorService`
```java
public class AccountServiceImpl implements AccountService {
    private AccountReaderDAO accReaderDao;
    private AccountWriterDAO accWriterDao;
    
    // Service methods delegate to DAOs
}
```
**Purpose**: Encapsulates business logic, provides transaction boundaries

### 5. Strategy Pattern
**Potential**: Different fee calculation strategies
```java
public interface FeeCalculatorService {
    double calculateFee(double balance);
}
// Can be extended with different strategies
```
**Purpose**: Enables algorithm selection at runtime

### 6. Monitor Pattern (Concurrency)
**Class**: `SafetyDepositBoxService`
```java
public synchronized SafetyDepositBox allocateSafetyDepositBox() {
    // ... code ...
    wait(); // Thread waits for box availability
}

public synchronized void releaseSafetyDepositBox(SafetyDepositBox box) {
    box.setAllotted(false);
    notifyAll(); // Wake up waiting threads
}
```
**Purpose**: Thread-safe resource management with wait/notify

---

## 🧪 Testing

### Test Structure

The project includes comprehensive unit tests using **JUnit 5** and **Mockito**:

```
src/test/java/com/github/gabrielhumbertdev/service/
├── AccountControllerMinimumBalanceTest.java      # 7 test cases
├── AccountServiceImplTest.java                   # 3 test cases
├── AlertServiceMinimumBalanceAlertTest.java      # 3 test cases
├── EmailUtilComprehensiveTest.java               # 30 test cases
├── FeeCalculatorServiceImplTest.java             # 5 test cases
├── SafetyDepositBoxServiceAlertTest.java         # 5 test cases
└── SafetyDepositBoxServiceTest.java              # 2 test cases
```

### Running Tests

**Run all tests**:
```bash
mvn test
```

**Run specific test class**:
```bash
mvn test -Dtest=AccountControllerMinimumBalanceTest
```

**Run with coverage**:
```bash
mvn clean test jacoco:report
```

### Test Coverage Summary

- **Total Test Cases**: 55
- **Email Functionality**: 30 tests
- **Transaction Processing**: 7 tests
- **Alert System**: 8 tests
- **Fee Calculation**: 5 tests
- **Concurrency**: 2 tests
- **Service Layer**: 3 tests

### Test Coverage Highlights

- **Email Mocking**: All tests use `MockedStatic<EmailUtil>` to prevent actual email sending
- **Boundary Testing**: Comprehensive boundary condition tests for minimum balance
- **Concurrency Testing**: Thread-safe operation verification
- **Null Safety**: Extensive null parameter validation tests
- **Edge Cases**: Tests for all edge cases and error conditions

### Example Test

```java
@Test
public void withdraw_belowMinimumBalance_appliesFine() throws Exception {
    // Arrange
    Person customer = new Person("John Doe", "London");
    customer.setEmail("john@test.com");
    
    CheckingAccount checking = new CheckingAccount();
    checking.setBalance(80.0);
    
    // Mock EmailUtil
    try (MockedStatic<EmailUtil> emailMock = Mockito.mockStatic(EmailUtil.class)) {
        emailMock.when(() -> EmailUtil.sendEmail(anyString(), anyString(), anyString()))
                 .thenAnswer(inv -> null);
        
        // Act
        controller.withdraw(customer, checking, 40.0);
        
        // Assert
        // 80 - 40 = 40 (below 50 minimum)
        // 40 - 25 = 15 (after fine)
        assertEquals(15.0, checking.getBalance(), 0.01);
    }
}
```

---

## 💡 Usage Examples

### Example 1: Creating Customers and Accounts

```java
AccountController controller = new AccountController();

// Create a person customer
Customer person = controller.createCustomer("John Doe", "123 Main St", "person");
person.setEmail("john.doe@email.com");

// Create a company customer
Customer company = controller.createCustomer("Acme Corp", "456 Business Ave", "company");
company.setEmail("info@acmecorp.com");

// Create accounts
Account personChecking = controller.createAccount(person, "checking");
Account personSavings = controller.createAccount(person, "savings");
Account companyChecking = controller.createAccount(company, "checking");
```

### Example 2: Processing Transactions with Alerts

```java
// Deposit with automatic email alert
controller.deposit(person, personChecking, 500.00);
// → Email sent to john.doe@email.com with transaction details

// Withdrawal with automatic email alert
controller.withdraw(person, personChecking, 100.00);
// → Email sent to john.doe@email.com with transaction details
```

### Example 3: Minimum Balance Fine Scenario

```java
CheckingAccount checking = new CheckingAccount();
checking.setBalance(80.0); // Above minimum ($50)

Customer customer = new Person("Jane Smith", "789 Oak Lane");
customer.setEmail("jane@email.com");

// Withdraw enough to go below minimum
controller.withdraw(customer, checking, 40.0);
// Balance: 80 - 40 = 40 (below minimum)
// Fine applied: 40 - 25 = 15
// Two emails sent:
//   1. Withdrawal transaction alert
//   2. Minimum balance fine alert
```

### Example 4: Company Fee Charging

```java
Company company = new Company("Tech Solutions", "321 Innovation Dr");

Account checking = new CheckingAccount();
checking.setBalance(1000.0);

Account savings = new SavingsAccount();
savings.setBalance(500.0);

company.addAccount(checking);
company.addAccount(savings);

// Charge $100 fee
company.chargeAllAccounts(100.0);
// Checking: 1000 - 100 = 900 (1x charge)
// Savings: 500 - 200 = 300 (2x charge for company)
```

### Example 5: Safety Deposit Box Allocation

```java
SafetyDepositBoxService boxService = SafetyDepositBoxService.getInstance();

Customer customer = new Person("Bob Wilson", "555 Elm St");
customer.setEmail("bob@email.com");

// Allocate box (thread-safe)
SafetyDepositBox box = boxService.allocateSafetyDepositBox(customer);
// → Email sent: "Safety Deposit Box Allocated"

// Later, release box
boxService.releaseSafetyDepositBox(customer, box);
// → Email sent: "Safety Deposit Box Released"
```

### Example 6: Fee Calculator

```java
FeeCalculatorService feeCalc = new FeeCalculatorServiceImpl();

double fee1 = feeCalc.calculateFee(50.0);    // $20 (≤ $100)
double fee2 = feeCalc.calculateFee(300.0);   // $15 ($101-$500)
double fee3 = feeCalc.calculateFee(750.0);   // $10 ($501-$1000)
double fee4 = feeCalc.calculateFee(1500.0);  // $5 ($1001-$2000)
double fee5 = feeCalc.calculateFee(3000.0);  // $0 (> $2000)
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Maintain consistent indentation (4 spaces)

### Git Workflow
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Testing Requirements
- All new features must include unit tests
- Maintain or improve code coverage
- All tests must pass before submitting PR

### Code Review Process
1. Code will be reviewed by maintainers
2. Address any requested changes
3. Once approved, code will be merged

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Gabriel Gomes**  
- GitHub: [@GabrielHumbertDev](https://github.com/GabrielHumbertDev)  
- Email: gabrielhumbert@outlook.com

---

## 🙏 Acknowledgments

- Jakarta Mail team for excellent email library
- JUnit and Mockito teams for testing frameworks
- The open-source community for inspiration and best practices

---

## 🗺️ Roadmap

### Planned Features
- [ ] Database persistence layer implementation
- [ ] RESTful API endpoints
- [ ] Web-based user interface
- [ ] Additional account types (Money Market, CD)
- [ ] Transaction history tracking
- [ ] Account statements generation
- [ ] Mobile app integration
- [ ] Two-factor authentication
- [ ] Audit logging
- [ ] Performance monitoring

### Known Issues
- EmailUtil requires SMTP configuration before use
- DAO implementations not yet provided
- No persistent storage (in-memory only)

---

## 📚 Additional Resources

- [Java 8 Documentation](https://docs.oracle.com/javase/8/docs/)
- [Maven Guide](https://maven.apache.org/guides/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Jakarta Mail Guide](https://eclipse-ee4j.github.io/mail/)
- [SOLID Principles](https://www.digitalocean.com/community/conceptual_articles/s-o-l-i-d-the-first-five-principles-of-object-oriented-design)
- [Design Patterns](https://refactoring.guru/design-patterns)

---

**Built with ❤️ using Java and Maven**
