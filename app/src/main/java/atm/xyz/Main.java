package atm.xyz;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static boolean isLogin = false;

    public static void main(String[] args) {
        while (true) {
            
            printWelcomingMsg();
            System.out.println("Input: ");
            UserResponseDTO userDTO = new UserResponseDTO();
            User user = new User();
            String username = "";
            
            while (true) {
                // Exit the application when there is no more input
                if (!sc.hasNextLine()) {
                    System.exit(0);
                }
                String input = sc.nextLine();

                if (input.toLowerCase().startsWith("login ")) {
                    String name = input.substring(6).trim();

                    // Handle alreadly login
                    if (isLogin) {
                        System.out.println("You are already logged in as " + username);
                        if (!name.equalsIgnoreCase(username)) {
                            System.out.println("Logout first to login to another acoount");
                        }
                        System.out.println();
                        continue;
                    }

                    if (!name.isEmpty()) {
                        if (validateUserName(name)) {
                            userDTO = UserService.loginOrCreateUser(name);
                            user = userDTO.getUser();
                            username = user.getName();

                            if (userDTO.isNewUser()) {
                                System.out.println("Welcome, " + username + "!");
                                System.out.println("Your balance is $0\n");
                            } else {
                                System.out.println("Hello, " + username + "!");
                                printBalanceInformation(user);
                            }
                            isLogin = true;
                        }

                    } else {
                        System.out.println("Please input username to login!\n");
                    }

                } else if (input.toLowerCase().startsWith("deposit ")) {
                    if (checkSession()) {
                        String amountString = input.substring(8).trim();
                        if (!amountString.isEmpty()) {
                            try {
                                BigDecimal amount = new BigDecimal(amountString);

                                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                                    System.out.println("Please enter a valid positive deposit amount!\n");
                                } else if (amount.scale() > 0) {
                                    System.out.println("Please enter only an integer deposit amount!\n");
                                } else {
                                    DepositResponseDTO depositResponseDTO = UserService.deposit(user, amount);
                                    ArrayList<Debt> paidDebtList = depositResponseDTO.getPaidDebtList();
                                    if (!paidDebtList.isEmpty()) {
                                        for (Debt paidDebt : paidDebtList) {
                                            System.out.println("Transferred $" + paidDebt.getAmount() + " to "
                                                    + paidDebt.getLender().getName());
                                        }
                                    }
                                    printBalanceInformation(user);
                                }
                            } catch (NumberFormatException e) {
                                System.out.println(
                                        "Please enter a valid positive deposit amount without any currecy sign!\n");
                            }
                        } else {
                            System.out.println("Please specify the amount to deposit!\n");
                        }
                    }

                } else if (input.toLowerCase().startsWith("withdraw ")) {
                    if (checkSession()) {
                        String amountString = input.substring(9).trim();
                        if (!amountString.isEmpty()) {
                            try {
                                BigDecimal amount = new BigDecimal(amountString);

                                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                                    System.out.println("Please enter a valid positive withdraw amount!\n");
                                } else if (amount.scale() > 0) {
                                    System.out.println("Please enter only an integer withdraw amount!\n");
                                } else if (amount.compareTo(user.getBalance()) > 0) {
                                    System.out.println("Your balance is not enough for this withdrawal\n");
                                } else {
                                    BigDecimal balance = UserService.withdraw(user, amount);
                                    System.out.println("Your balance is $" + balance + "\n");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println(
                                        "Please enter a valid positive withdraw amount without any currecy sign!\n");
                            }
                        } else {
                            System.out.println("Please specify the amount to withdraw!\n");
                        }
                    }

                } else if (input.toLowerCase().startsWith("transfer ")) {
                    if (checkSession()) {
                        String[] parts = input.substring(9).trim().split("\\s+");

                        if (parts.length == 2) {
                            String amountString = parts[1];
                            User target = UserService.findUserByName(parts[0]);
                            
                            if (target == null) {
                                System.out.println("User not found. Please check the username and try again.\n");
                                continue;
                            } else if (target.equals(user)) {
                                System.out.println("You cannot transfer to your own account. Please do deposit instead\n");
                                continue;
                            }
                            
                            try {
                                BigDecimal amount = new BigDecimal(amountString);

                                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                                    System.out.println("Please enter a valid positive transfer amount!\n");
                                } else if (amount.scale() > 0) {
                                    System.out.println("Please enter only an integer transfer amount!\n");
                                } else {
                                    TransferResponseDTO transferResponseDTO = UserService.transferPrep(user, target,
                                            amount);

                                    BigDecimal transferAmount = transferResponseDTO.getTransferAmount();
                                    Debt debt = transferResponseDTO.getDebt();
                                    User targetUser = transferResponseDTO.getTarget();

                                    if (transferAmount != null && transferAmount.compareTo(BigDecimal.ZERO) > 0) {
                                        System.out.println(
                                                "Transferred $" + transferAmount + " to " + targetUser.getName());
                                    }

                                    if (debt != null) {
                                        System.out.println("Your balance is $" + user.getBalance());
                                        if (debt.getBorrower().equals(user)) {
                                            System.out.println(
                                                    "Owed $" + debt.getAmount() + " to " + targetUser.getName() + "\n");
                                        } else {
                                            System.out.println(
                                                    "Owed $" + debt.getAmount() + " from " + targetUser.getName() + "\n");

                                        }
                                    } else {
                                        System.out.println("Your balance is $" + user.getBalance() + "\n");
                                    }
                                }
                            } catch (NumberFormatException e) {
                                System.out.println(
                                        "Please enter a valid positive transfer amount without any currecy sign!\n");
                            }
                        } else {
                            System.out.println("Invalid input format. Please use: transfer [target] [amount]\n");
                        }
                    }

                } else if (input.trim().equalsIgnoreCase("logout")) {
                    if (checkSession()) {
                        System.out.println("Goodbye, " + username + "!\n");
                        isLogin = false;
                        break;
                    }
                
                } else {
                    System.out.println("Invalid command. Please read the instructions again!\n");
                }

            }

        }
    }

    public static boolean checkSession() {
        if (!isLogin) {
            System.out.println("Please log in first to proceed with this action!\n");
            return false;
        }
        return true;
    }

    public static void printWelcomingMsg() {
        System.out.println("================================Welcome to ATM XYZ================================");
        System.out.println("Please follow the instructions below to use our system!\n");
        System.out.println("[1] Login");
        System.out.println("Before managing your money, you must first log in by typing: `login [name]`");
        System.out.println("Example: login Alice");
        System.out.println("If you don't have an account, we'll automatically create one for you:)\n");
        System.out.println("[2] Deposit");
        System.out.println("To add your account balance, you can make a deposit by typing: `deposit [amount]`");
        System.out.println("Example: deposit 100\n");
        System.out.println("[3] Withdraw");
        System.out.println("To withdraw money from your account, type: `withdraw [amount]`");
        System.out.println("Example: withdraw 50\n");
        System.out.println("[4] Transfer");
        System.out.println("To transfer money to another customer, type: `transfer [target] [amount]`");
        System.out.println("Example: transfer Bob 200\n");
        System.out.println("[5] Logout");
        System.out.println("When you're finished, you can log out by typing: `logout`");
        System.out.println("==================================================================================\n");
    }

    public static boolean validateUserName(String name) {
        if (name.length() < 3 || name.length() > 20) {
            System.out.println("Username must be between 3 and 20 characters long\n");
            return false;
        }

        if (!name.matches("[a-zA-Z0-9_.-]+")) {
            System.out.println("Username can only contain letters, numbers, and the special characters _ . -\n");
            return false;
        }

        String[] restrictedNames = { "admin", "root", "user", "sys", "system" };
        for (String restrictedName : restrictedNames) {
            if (name.toLowerCase().equalsIgnoreCase(restrictedName)) {
                System.out.println("This username is restricted. Please choose another name.\n");
                return false;
            }
        }

        return true;
    }

    public static void printBalanceInformation(User user) {
        ArrayList<Debt> debtList = Debt.getDebtListByUser(user);

        if (!debtList.isEmpty()) {
            System.out.println("Your balance is $" + user.getBalance());
            for (Debt debt : debtList) {
                if (user.equals(debt.getBorrower())) {
                    System.out.println("Owed $" + debt.getAmount() + " to " + debt.getLender().getName());
                } else {
                    System.out.println("Owed $" + debt.getAmount() + " from " + debt.getBorrower().getName());
                }
            }
            System.out.println();
        } else {
            System.out.println("Your balance is $" + user.getBalance() + "\n");
        }

    }
}