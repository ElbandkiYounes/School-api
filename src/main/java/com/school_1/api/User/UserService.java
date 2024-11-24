package com.school_1.api.User;

import com.school_1.api.Commons.Exceptions.AccessDeniedException;
import com.school_1.api.Commons.Exceptions.EmailAlreadyExistsException;
import com.school_1.api.Commons.Exceptions.NotFoundException;
import com.school_1.api.Commons.Exceptions.UnauthorizedException;
import com.school_1.api.Commons.Services.EmailService;
import com.school_1.api.User.models.LoginPayload;
import com.school_1.api.User.models.SignupPayload;
import com.school_1.api.User.models.UpdateUserPayload;
import com.school_1.api.User.models.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

@Stateless
public class UserService {
    @Inject
    private UserEJB userEJB;

    @Inject
    private EmailService emailService;

    public User signUp(SignupPayload userPayload) throws EmailAlreadyExistsException {
        if(userEJB.findUserByEmail(userPayload.getEmail()) != null) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user = userPayload.toUser();
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setVerificationToken(UUID.randomUUID().toString());
        User createdUser = userEJB.saveUser(user);
        sendVerificationEmail(createdUser);
        return createdUser;

    }

    public User login(LoginPayload loginPayload) throws UnauthorizedException, NotFoundException {
        User user = userEJB.findUserByEmail(loginPayload.getEmail());
        if (user != null && BCrypt.checkpw(loginPayload.getPassword(), user.getPassword())) {
            if (user.isValidate()) {
                return user;
            } else {
                throw new UnauthorizedException("User is not verified please check your email");
            }
        } else {
            throw new NotFoundException("Invalid email or password");
        }
    }

    public User updateUser(Long userId, UpdateUserPayload updateUserPayload, String token ) throws NotFoundException, AccessDeniedException {
        User user = userEJB.findUserById(userId);
        if (user != null) {
            if(!user.getVerificationToken().equals(token)) {
                throw new AccessDeniedException("Token is mismatched User cant update another user");
            }
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

    public void verifyEmail(String token) throws UnauthorizedException {
        User user = userEJB.findUserByVerificationToken(token);
        if (user != null) {
            user.setValidate(true);
            userEJB.updateUser(user);
        } else {
            throw new UnauthorizedException("Invalid verification token");
        }
    }

}
