package com.smartbudget.controllers;

import com.smartbudget.MainApplication;
import com.smartbudget.dao.UserDAO;
import com.smartbudget.exceptions.DatabaseException;
import com.smartbudget.exceptions.ValidationException;
import com.smartbudget.models.User;
import com.smartbudget.services.AuthService;
import com.smartbudget.services.impl.AuthServiceImpl;
import com.smartbudget.utils.AlertUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for registration screen.
 */
public class RegisterController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button registerButton;
    
    private AuthService authService;
    
    /**
     * Initialize controller.
     */
    @FXML
    public void initialize() {
        UserDAO userDAO = new UserDAO();
        authService = new AuthServiceImpl(userDAO);
        errorLabel.setText("");
    }
    
    /**
     * Handle register button click.
     */
    @FXML
    private void onRegisterClick() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Clear previous errors
        errorLabel.setText("");
        
        try {
            // Attempt registration
            User user =new User(username, email, password, confirmPassword);
                    authService.register(user);
            AlertUtil.showSuccess("Registration Successful", 
                "Account created! Your username is: " + user.getUsername());
            // Navigate back to
            MainApplication.showLoginScreen();
        } catch (ValidationException e) {
            errorLabel.setText(e.getMessage());
        }
    }
    
    /**
     * Handle back to login link click.
     */
    @FXML
    private void onBackClick() {
        MainApplication.showLoginScreen();
    }
}
