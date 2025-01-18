package atm.xyz;
import java.math.BigDecimal;
import java.util.ArrayList;

public class DepositResponseDTO {
    private BigDecimal balance;
    private ArrayList<Debt> paidDebtList = new ArrayList<>();

    public DepositResponseDTO() {}

    public DepositResponseDTO(BigDecimal balance, ArrayList<Debt> paidDebtList) {
        this.balance = balance;
        this.paidDebtList = paidDebtList;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public ArrayList<Debt> getPaidDebtList() {
        return this.paidDebtList;
    }
}

