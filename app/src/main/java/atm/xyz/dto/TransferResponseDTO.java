package atm.xyz.dto;
import java.math.BigDecimal;

import atm.xyz.model.Debt;
import atm.xyz.model.User;

public class TransferResponseDTO {
    private User sender;
    private User target;
    private BigDecimal transferAmount;

    public TransferResponseDTO() {}

    public TransferResponseDTO(User sender, User target, BigDecimal transferAmount) {
        this.sender = sender;
        this.target = target;
        this.transferAmount = transferAmount;
    }

    public TransferResponseDTO(User sender, User target) {
        this.sender = sender;
        this.target = target;
    }

    public User getSender() {
        return this.sender;
    }

    public User getTarget() {
        return this.target;
    }

    public BigDecimal getTransferAmount() {
        return this.transferAmount;
    }

    public Debt getDebt() {
        for (Debt debt : Debt.getDebtList()) {
            if ((debt.getBorrower().equals(this.sender) && debt.getLender().equals(this.target))
            || (debt.getBorrower().equals(this.target) && debt.getLender().equals(this.sender))) {
                return debt;
            }
        }

        return null;
    }
}

