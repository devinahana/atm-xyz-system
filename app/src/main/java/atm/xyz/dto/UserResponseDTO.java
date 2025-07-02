package atm.xyz.dto;

import atm.xyz.model.User;

public class UserResponseDTO {
    private User user;
    private boolean isNewUser;

    public UserResponseDTO() {}

    public UserResponseDTO(User user, boolean isNewUser) {
        this.user = user;
        this.isNewUser = isNewUser;
    }

    public User getUser() {
        return this.user;
    }

    public boolean isNewUser() {
        return this.isNewUser;
    }
}
