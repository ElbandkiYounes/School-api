package com.school_1.api.User;

import com.school_1.api.Commons.Exceptions.NotFoundException;
import com.school_1.api.Commons.Exceptions.UnauthorizedException;
import com.school_1.api.Commons.Services.EmailService;
import com.school_1.api.User.models.SignupPayload;
import com.school_1.api.User.models.UpdateUserPayload;
import com.school_1.api.User.models.User;
import com.school_1.api.User.models.UserRole;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

@Stateless
public class UserService {
    @Inject
    private UserEJB userEJB;

    @Inject
    private EmailService emailService;

    public User SignUp(SignupPayload userPayload) {
        User user = userPayload.toUser();
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setVerificationToken(UUID.randomUUID().toString());
        User createdUser = userEJB.saveUser(user);
        sendVerificationEmail(createdUser);
        return createdUser;
    }

    public User login(String email, String password) {
        User user = userEJB.findUserByEmail(email);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            if (user.isValidate()) {
                return user;
            } else {
                throw new UnauthorizedException("User is not verified");
            }
        } else {
            throw new NotFoundException("Invalid email or password");
        }
    }

    public User updateUser(Long userId, UpdateUserPayload updateUserPayload) {
        User user = userEJB.findUserById(userId);
        if (user != null) {
            user = updateUserPayload.toUser(user);
            userEJB.updateUser(user);
            return user;
        } else {
            throw new NotFoundException("User not found");
        }
    }

    private void sendVerificationEmail(User user) {
        String subject = "Email Verification";
        String body = "Please verify your email by clicking the following link: "
                + "http://localhost:8080/School_1-1.0-SNAPSHOT/api/users/verify?token=" + user.getVerificationToken();
        emailService.sendEmail(user.getEmail(), subject, body);
    }

    public void verifyEmail(String token) {
        User user = userEJB.findUserByVerificationToken(token);
        if (user != null) {
            user.setValidate(true);
            userEJB.updateUser(user);
        } else {
            throw new UnauthorizedException("Invalid verification token");
        }
    }

}
