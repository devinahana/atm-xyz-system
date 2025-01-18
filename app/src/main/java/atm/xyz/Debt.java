package atm.xyz;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

public class Debt {
    private static ArrayList<Debt> debtList = new ArrayList<>();
    private UUID debtId = UUID.randomUUID();
    private User borrower;
    private User lender;
    private BigDecimal amount;
    
    public Debt() {}

    public Debt(User borrower, User lender, BigDecimal amount) {
        this.borrower = borrower;
        this.lender = lender;
        this.amount = amount;
    }

    public static ArrayList<Debt> getDebtList() {
        return debtList;
    }

    public static ArrayList<Debt> getDebtListByUser(User user) {
        ArrayList<Debt> userDebtList = new ArrayList<>();

        for (Debt debt : debtList) {
            if (debt.getBorrower().equals(user) || debt.getLender().equals(user)) {
                userDebtList.add(debt);
            }
        }

        return userDebtList;
    }

    public User getBorrower() {
        return this.borrower;
    }

    public User getLender() {
        return this.lender;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
