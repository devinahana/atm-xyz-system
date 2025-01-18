## Author
Devina Hana<br>
Sistem Informasi - Universitas Indonesia

## Assumptions
- Name refers to a username, so it won’t contain spaces
- Debt is exclusive to each borrower-lender pair
- When user receive money, debts must be paid off first
- User can create additional debts even if they already have an existing debt
- User can transfer money even if they don’t have sufficient balance, and the deficit will automatically become a debt
- Withdrawals cannot exceed the available balance (denied if balance is insufficient)

## Getting Started
```bash
./gradlew run --quiet < files/input/input.txt > files/output/output.txt
```