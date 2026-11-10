We will build it with maven version 25.
src/main/java/com/atm/
├── Main.java                        <-- نقطة البداية البسيطة جداً
├── model/                           <-- الكلاسات الأساسية (الكيانات)
│   ├── User.java (Abstract)
│   ├── Customer.java
│   ├── Admin.java
│   ├── Account.java
│   ├── Card.java
│   ├── Session.java
│   ├── ATM.java
│   └── CashCassette.java
├── transaction/                     <-- نظام المعاملات والوراثة
│   ├── Transaction.java (Abstract)
│   ├── FinancialTransaction.java (Abstract)
│   ├── WithdrawalTransaction.java
│   ├── DepositTransaction.java
│   ├── TransferTransaction.java
│   └── BalanceInquiryTransaction.java
├── enums/                           <-- كل الـ Enums اللي ظاهرة في أسفل الدياجرام
│   ├── AccountStatus.java
│   ├── AccountType.java
│   ├── CardStatus.java
│   ├── TransactionStatus.java
│   ├── DepositType.java
│   ├── AlertType.java
│   ├── RequestStatus.java
│   └── AuditAction.java
├── service/                         <-- منطق العمليات والخدمات
│   ├── AuthenticationService.java
│   └── BankService.java
└── dao/                             <-- طبقة الاتصال بقاعدة البيانات (أو H2)
└── DatabaseConnection.java