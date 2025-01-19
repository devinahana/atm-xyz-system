## Author
Devina Hana<br>

## Assumptions
- The system is built for end user so it only accepts positive whole numbers for amounts
- User must log in first before performing any actions
- Once logged in, users must log out before logging into a different account
- Name refers to a username, so it won’t contain spaces
- Debt is exclusive to each borrower-lender pair
- When user receive money, debts must be paid off first
- User can create additional debts even if they already have an existing debt
- User can transfer money even if they don’t have sufficient balance, and the deficit will automatically become a debt
- Withdrawals cannot exceed the available balance (denied if balance is insufficient)

## Getting Started
**Build the application**
```bash
./gradlew clean build
```
<br>

**Run the application with .txt input**
```bash
./gradlew run --quiet < test-case/input/in-1.txt > test-case/output/out-1.txt
```
<br>

**Run the application with your own input**
```bash
./gradlew run
```
<br>

**- Run the test**
```bash
./gradlew test
```
check the output of test in app/build/reports/tests/test/index.html or check the test-result.html for current test result