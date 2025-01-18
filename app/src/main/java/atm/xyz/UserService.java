package atm.xyz;
import java.math.BigDecimal;
import java.util.ArrayList;

public class UserService {

    public static UserResponseDTO loginOrCreateUser(String name) {
        User user = findUserByName(name);
        
        if (user == null) {
            User newUser = new User(name);
            return new UserResponseDTO(newUser, true);
        }

        return new UserResponseDTO(user, false);
    }

    public static User findUserByName(String name) {
        for (User user : User.getUserList()) {
            if (user.getName().equalsIgnoreCase(name)) {
                return user;
            }
        }

        return null;
    }

    public static DepositResponseDTO deposit(User user, BigDecimal depositAmount) {
        ArrayList<Debt> debtList = Debt.getDebtList();
        ArrayList<Debt> paidDebtList = new ArrayList<>();
        ArrayList<Debt> debtsToProcess = new ArrayList<>(debtList);

        for (Debt debt : debtsToProcess) {
            if (debt.getBorrower().equals(user)) {
                depositAmount = depositAmount.subtract(debt.getAmount());
                
                int compareTo = depositAmount.compareTo(BigDecimal.ZERO);
                if (compareTo > 0) {
                    deposit(debt.getLender(), debt.getAmount());
                    paidDebtList.add(new Debt(debt.getBorrower(), debt.getLender(), debt.getAmount()));
                    debtList.remove(debt);
                } else if (compareTo == 0) {
                    deposit(debt.getLender(), debt.getAmount());
                    paidDebtList.add(new Debt(debt.getBorrower(), debt.getLender(), debt.getAmount()));
                    debtList.remove(debt);
                    break;
                } else {
                    BigDecimal paidDebtAmount = debt.getAmount().subtract(depositAmount.abs());
                    Debt paidDebt = new Debt(debt.getBorrower(), debt.getLender(), paidDebtAmount);
                    deposit(debt.getLender(), paidDebtAmount);
                    paidDebtList.add(paidDebt);
                    
                    debt.setAmount(depositAmount.abs());
                    break;
                }
            }
        }
        
        if (depositAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal newBalance = user.getBalance().add(depositAmount);
            user.setBalance(newBalance);
            return new DepositResponseDTO(newBalance, paidDebtList);
        }

        return new DepositResponseDTO(user.getBalance(), paidDebtList);
    }

    public static BigDecimal withdraw(User user, BigDecimal withdrawAmount) {
        BigDecimal newBalance = user.getBalance().subtract(withdrawAmount);
        user.setBalance(newBalance);
        return newBalance;
    }

    public static TransferResponseDTO transferPrep(User sender, User target, BigDecimal transferAmount) {
        ArrayList<Debt> debtList = Debt.getDebtList();
        for (Debt debt: debtList) {
            if (debt.getBorrower().equals(target) && debt.getLender().equals(sender)) {
                BigDecimal debtMinTransfer = debt.getAmount().subtract(transferAmount);

                int compareTo = debtMinTransfer.compareTo(BigDecimal.ZERO);
                if (compareTo == 0) {
                    debtList.remove(debt);
                    return new TransferResponseDTO(sender, target);
                } else if (compareTo < 0) {
                    debtList.remove(debt);
                    return transfer(sender, target, debtMinTransfer.abs());
                } else {
                    debt.setAmount(debtMinTransfer);
                    return new TransferResponseDTO(sender, target);
                }

            }

            else if (debt.getBorrower().equals(sender) && debt.getLender().equals(target)) {
                debt.setAmount(debt.getAmount().add(transferAmount));
                return new TransferResponseDTO(sender, target);
            }
        }

        return transfer(sender, target, transferAmount);
    }

    public static TransferResponseDTO transfer(User sender, User target, BigDecimal transferAmount) {
        BigDecimal initialBalance = sender.getBalance();
        BigDecimal balanceMinTransfer = sender.getBalance().subtract(transferAmount);
        
        if (balanceMinTransfer.compareTo(BigDecimal.ZERO) < 0) {
            deposit(target, initialBalance);
            sender.setBalance(BigDecimal.ZERO);
            Debt.getDebtList().add(
                new Debt(sender, target, balanceMinTransfer.abs())
            ); 
            return new TransferResponseDTO(sender, target, initialBalance);
        } else {
            deposit(target, transferAmount);
            sender.setBalance(balanceMinTransfer);
            return new TransferResponseDTO(sender, target, transferAmount);
        }
    }
}
