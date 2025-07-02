package atm.xyz;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import atm.xyz.dto.TransferResponseDTO;
import atm.xyz.dto.UserResponseDTO;
import atm.xyz.model.Debt;
import atm.xyz.model.User;
import atm.xyz.service.UserService;

import java.util.List;
import java.math.BigDecimal;


public class MainTest {

    @BeforeEach
    void setUp() {
        User.getUserList().clear();
        Debt.getDebtList().clear();
    }

    @Test
    void testScenario() {
        // login Alice
        UserResponseDTO userResponseDTO = UserService.loginOrCreateUser("Alice");
        User alice = userResponseDTO.getUser();
        assertTrue(userResponseDTO.isNewUser());
        assertEquals(alice.getName().toLowerCase(), "alice");
        assertEquals(alice.getBalance(), BigDecimal.ZERO);
        assertTrue(Debt.getDebtListByUser(alice).isEmpty());
        
        // deposit 100
        UserService.deposit(alice, BigDecimal.valueOf(100));
        assertEquals(BigDecimal.valueOf(100), alice.getBalance());

        // logout
        
        // login Bob
        userResponseDTO = UserService.loginOrCreateUser("Bob");
        User bob = userResponseDTO.getUser();
        assertTrue(userResponseDTO.isNewUser());
        assertEquals(bob.getName().toLowerCase(), "bob");
        assertEquals(bob.getBalance(), BigDecimal.ZERO);
        assertTrue(Debt.getDebtListByUser(bob).isEmpty());

        // deposit 80
        UserService.deposit(bob, BigDecimal.valueOf(80));
        assertEquals(BigDecimal.valueOf(80), bob.getBalance());

        // transfer Alice 50
        TransferResponseDTO transferResponseDTO = UserService.transferPrep(bob, alice, BigDecimal.valueOf(50));
        assertEquals(BigDecimal.valueOf(30), bob.getBalance());
        assertEquals(BigDecimal.valueOf(150), alice.getBalance());
        assertNull(transferResponseDTO.getDebt());

        // transfer Alice 100
        transferResponseDTO = UserService.transferPrep(bob, alice, BigDecimal.valueOf(100));
        assertEquals(BigDecimal.ZERO, bob.getBalance());
        assertEquals(BigDecimal.valueOf(180), alice.getBalance());
        Debt debt = transferResponseDTO.getDebt();
        assertNotNull(debt);
        assertEquals(BigDecimal.valueOf(70), debt.getAmount());
        assertEquals(debt.getBorrower(), bob);
        assertEquals(debt.getLender(), alice);
        assertFalse(Debt.getDebtListByUser(bob).isEmpty());
        assertTrue(Debt.getDebtListByUser(bob).size() == 1);
        assertFalse(Debt.getDebtListByUser(alice).isEmpty());
        assertTrue(Debt.getDebtListByUser(alice).size() == 1);

        // deposit 30
        UserService.deposit(bob, BigDecimal.valueOf(30));
        assertEquals(BigDecimal.valueOf(0), bob.getBalance());
        assertEquals(BigDecimal.valueOf(210), alice.getBalance());
        List<Debt> debtList = Debt.getDebtListByUser(bob);
        assertFalse(debtList.isEmpty());
        assertTrue(debtList.size() == 1);
        assertEquals(BigDecimal.valueOf(40), debtList.get(0).getAmount());
        assertEquals(bob, debtList.get(0).getBorrower());
        assertEquals(alice, debtList.get(0).getLender());

        // logout

        // login ALICE
        userResponseDTO = UserService.loginOrCreateUser("ALICE");
        assertFalse(userResponseDTO.isNewUser());
        assertEquals(userResponseDTO.getUser(), alice);
        assertEquals(BigDecimal.valueOf(210), alice.getBalance());
        debtList = Debt.getDebtListByUser(alice);
        assertFalse(debtList.isEmpty());
        assertTrue(debtList.size() == 1);
        assertEquals(BigDecimal.valueOf(40), debtList.get(0).getAmount());
        assertEquals(bob, debtList.get(0).getBorrower());
        assertEquals(alice, debtList.get(0).getLender());

        // transfer Bob 30
        transferResponseDTO = UserService.transferPrep(alice, bob, BigDecimal.valueOf(30));
        assertEquals(BigDecimal.valueOf(210), alice.getBalance());
        assertEquals(BigDecimal.valueOf(0), bob.getBalance());
        debtList = Debt.getDebtListByUser(alice);
        assertFalse(debtList.isEmpty());
        assertTrue(debtList.size() == 1);
        assertEquals(BigDecimal.valueOf(10), debtList.get(0).getAmount());
        assertEquals(bob, debtList.get(0).getBorrower());
        assertEquals(alice, debtList.get(0).getLender());

        // logout

        // login BOB
        userResponseDTO = UserService.loginOrCreateUser("BOB");
        assertFalse(userResponseDTO.isNewUser());
        assertEquals(userResponseDTO.getUser(), bob);
        assertEquals(BigDecimal.valueOf(0), bob.getBalance());
        debtList = Debt.getDebtListByUser(bob);
        assertFalse(debtList.isEmpty());
        assertTrue(debtList.size() == 1);
        assertEquals(BigDecimal.valueOf(10), debtList.get(0).getAmount());
        assertEquals(bob, debtList.get(0).getBorrower());
        assertEquals(alice, debtList.get(0).getLender());

        // deposit 100
        UserService.deposit(bob, BigDecimal.valueOf(100));
        assertEquals(BigDecimal.valueOf(90), bob.getBalance());
        assertEquals(BigDecimal.valueOf(220), alice.getBalance());
        assertTrue(Debt.getDebtList().isEmpty());

    }
}
