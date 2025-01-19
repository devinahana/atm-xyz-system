package atm.xyz;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import java.math.BigDecimal;
import java.util.ArrayList;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    private User alice, bob;

    @BeforeAll
    static void setUp() {
        User.getUserList().clear();
        Debt.getDebtList().clear();
    }

    @Test
    @Order(1)
    void testLoginOrCreateUser_NewUser() {
        // Create new user Alice
        String username = "Alice";
        UserResponseDTO response = UserService.loginOrCreateUser(username);
        alice = response.getUser();

        assertTrue(response.isNewUser());
        assertNotNull(response.getUser());
        assertEquals(response.getUser().getName(), username);
        assertEquals(response.getUser().getBalance(), BigDecimal.ZERO);
        assertEquals(User.getUserList().size(), 1);
    }

    @Test
    @Order(2)
    void testLoginOrCreateUser_OldUser() {
        // Login existing user Alice
        String username = "ALICE"; // case insensitive
        UserResponseDTO response = UserService.loginOrCreateUser(username);

        assertFalse(response.isNewUser());
        assertNotNull(response.getUser());
        assertEquals(response.getUser(), alice);
        assertEquals(response.getUser().getBalance(), BigDecimal.ZERO);
        assertEquals(User.getUserList().size(), 1);
    }

    @Test
    @Order(3)
    void testDeposit_WithoutDebt() {
        // Ensure Alice does not have balance and debt first
        assertEquals(BigDecimal.ZERO, alice.getBalance());
        assertTrue(Debt.getDebtListByUser(alice).isEmpty());
        
        // Deposit
        BigDecimal depositAmount = new BigDecimal("100");
        DepositResponseDTO response = UserService.deposit(alice, depositAmount);

        assertTrue(response.getPaidDebtList().isEmpty());
        assertEquals(depositAmount, alice.getBalance());
        assertTrue(Debt.getDebtListByUser(alice).isEmpty());
    }

    @Test
    @Order(4)
    void testWithdraw() {
        // Get Alice's initial balance 
        BigDecimal initialBalance = alice.getBalance();
        
        // Withdraw
        BigDecimal withdrawAmount = new BigDecimal("50");
        BigDecimal response = UserService.withdraw(alice, withdrawAmount);

        assertEquals(response, alice.getBalance());
        assertEquals(initialBalance.subtract(withdrawAmount), alice.getBalance());
    }

    @Test
    @Order(5)
    void testTransfer_WithoutCreatingDebt() {
        // Create new user Bob
        String username = "Bob";
        UserResponseDTO userResponseDTO = UserService.loginOrCreateUser(username);
        bob = userResponseDTO.getUser();

        // Get Alice's initial balance 
        BigDecimal initialAliceBalance = alice.getBalance();

        // Transfer some of Alice's balance to Bob
        BigDecimal transferAmount = new BigDecimal("40");
        TransferResponseDTO response = UserService.transfer(alice, bob, transferAmount);
        assertEquals(response.getTransferAmount(), transferAmount);
        assertEquals(alice.getBalance(), initialAliceBalance.subtract(transferAmount));
        assertEquals(bob.getBalance(), transferAmount);
        assertTrue(Debt.getDebtListByUser(alice).isEmpty());
    }

    @Test
    @Order(6)
    void testTransfer_CreatingDebt() {
        // Get Alice's initial balance 
        BigDecimal initialAliceBalance = alice.getBalance();

        // Get Bob's initial balance 
        BigDecimal initialBobBalance = bob.getBalance();

        // Transfer Alice's balance to Bob
        BigDecimal transferAmount = new BigDecimal("40");
        TransferResponseDTO response = UserService.transfer(alice, bob, transferAmount);
        assertNotEquals(response.getTransferAmount(), transferAmount);
        assertEquals(response.getTransferAmount(), initialAliceBalance);
        assertEquals(alice.getBalance(), BigDecimal.ZERO);
        assertEquals(bob.getBalance(), initialBobBalance.add(response.getTransferAmount()));

        ArrayList<Debt> debtList = Debt.getDebtListByUser(alice);
        assertFalse(debtList.isEmpty());
        assertEquals(debtList.getFirst().getBorrower(), alice);
        assertEquals(debtList.getFirst().getLender(), bob);
        assertEquals(debtList.getFirst().getAmount(), transferAmount.subtract(response.getTransferAmount()));
    }

    @Test
    @Order(7)
    void testDeposit_WithDebt_DepositLessThanDebt() {
        // Ensure Alice has exactly one debt
        ArrayList<Debt> initialDebtList = Debt.getDebtListByUser(alice);
        BigDecimal debtAmount = initialDebtList.getFirst().getAmount();
        assertFalse(initialDebtList.isEmpty());
        assertEquals(initialDebtList.size(), 1);
        assertNotEquals(debtAmount, BigDecimal.ZERO);
        
        // Deposit
        BigDecimal depositAmount = new BigDecimal("10");
        DepositResponseDTO response = UserService.deposit(alice, depositAmount);

        assertNotEquals(alice.getBalance(), depositAmount);
        assertEquals(alice.getBalance(), BigDecimal.ZERO);
        assertNotEquals(response.getPaidDebtList().getFirst().getAmount(), debtAmount);
        assertEquals(response.getPaidDebtList().getFirst().getAmount(), depositAmount);
        // Alice still has debt
        ArrayList<Debt> debtList = Debt.getDebtListByUser(alice);
        assertFalse(debtList.isEmpty());
        assertEquals(debtList.size(), 1);
        assertEquals(debtList.getFirst().getAmount(), debtAmount.subtract(depositAmount));
    }

    @Test
    @Order(8)
    void testDeposit_WithDebt_DepositMoreOrEqualThanDebt() {
        // Ensure Alice has exactly one debt
        ArrayList<Debt> initialDebtList = Debt.getDebtListByUser(alice);
        BigDecimal debtAmount = initialDebtList.getFirst().getAmount();
        assertFalse(initialDebtList.isEmpty());
        assertEquals(initialDebtList.size(), 1);
        assertNotEquals(debtAmount, BigDecimal.ZERO);
        
        // Deposit
        BigDecimal depositAmount = new BigDecimal("100");
        DepositResponseDTO response = UserService.deposit(alice, depositAmount);

        assertNotEquals(alice.getBalance(), depositAmount);
        assertEquals(alice.getBalance(), depositAmount.subtract(debtAmount));
        assertEquals(response.getPaidDebtList().getFirst().getAmount(), debtAmount);
        // Alice's debt has been paid
        ArrayList<Debt> debtList = Debt.getDebtListByUser(alice);
        assertTrue(debtList.isEmpty());
    }
    
}