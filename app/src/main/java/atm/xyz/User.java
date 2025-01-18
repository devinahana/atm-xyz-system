package atm.xyz;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class User {
    private static ArrayList<User> userList = new ArrayList<>();
    private UUID userId = UUID.randomUUID();
    private String name;
    private BigDecimal balance = BigDecimal.ZERO;

    public User() {}

    public User(String name) {
        this.name = name;
        userList.add(this);
    }

    public static ArrayList<User> getUserList() {
        return userList;
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            User user = (User) obj;
            return Objects.equals(userId, user.userId) && Objects.equals(name, user.name);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name);
    }

}

