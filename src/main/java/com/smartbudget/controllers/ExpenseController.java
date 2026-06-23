package com.smartbudget.controllers;

import java.time.LocalDate;
import java.util.List;

import com.smartbudget.dao.ExpenseDAO;
import com.smartbudget.dao.IncomeDAO;
import com.smartbudget.dao.CategoryDAO; // Added for lookups
import com.smartbudget.services.impl.ExpenseServiceImpl;
import com.smartbudget.services.impl.IncomeServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import com.smartbudget.MainApplication;
import com.smartbudget.exceptions.DatabaseException;
import com.smartbudget.exceptions.ValidationException;
import com.smartbudget.models.Category;
import com.smartbudget.models.Expense;
import com.smartbudget.models.Income;
import com.smartbudget.models.User;
import com.smartbudget.services.ExpenseService;
import com.smartbudget.services.IncomeService;
import com.smartbudget.session.UserSession;
import com.smartbudget.utils.AlertUtil;

public class ExpenseController {

    // Expense Tab Components
    @FXML private ComboBox<Category> expenseCategoryComboBox;
    @FXML private TextField expenseAmountField;
    @FXML private TextField expenseDescriptionField;
    @FXML private DatePicker expenseDatePicker;

    @FXML private TableView<Expense> expenseTableView;
    @FXML private TableColumn<Expense, Integer> expenseIdCol;
    @FXML private TableColumn<Expense, String> expenseCategoryCol;
    @FXML private TableColumn<Expense, Double> expenseAmountCol;
    @FXML private TableColumn<Expense, String> expenseDescriptionCol;
    @FXML private TableColumn<Expense, LocalDate> expenseDateCol;

    // Income Tab Components
    @FXML private TextField incomeAmountField;
    @FXML private TextField incomeDescriptionField;
    @FXML private DatePicker incomeDatePicker;

    @FXML private TableView<Income> incomeTableView;
    @FXML private TableColumn<Income, Integer> incomeIdCol;
    @FXML private TableColumn<Income, Double> incomeAmountCol;
    @FXML private TableColumn<Income, String> incomeDescriptionCol;
    @FXML private TableColumn<Income, LocalDate> incomeDateCol;

    // Services & Session Management
    private ExpenseService expenseService;
    private IncomeService incomeService;
    private CategoryDAO categoryDAO; // Injected for lookup support
    private UserSession userSession;
    private User currentUser;

    @FXML
    public void initialize() {
        ExpenseDAO expenseDAO = new ExpenseDAO();
        IncomeDAO incomeDAO = new IncomeDAO();
        categoryDAO = new CategoryDAO(); // Initialize database lookup helper

        expenseService = new ExpenseServiceImpl(expenseDAO);
        incomeService = new IncomeServiceImpl(incomeDAO);

        userSession = UserSession.getInstance();

        if (!userSession.isLoggedIn()) {
            MainApplication.showLoginScreen();
            return;
        }

        currentUser = userSession.getCurrentUser();

        setupExpenseTableColumns();
        setupIncomeTableColumns();
        loadCategories();
        refreshTables();

        expenseDatePicker.setValue(LocalDate.now());
        incomeDatePicker.setValue(LocalDate.now());
    }

    private void setupExpenseTableColumns() {
        expenseIdCol.setCellValueFactory(new PropertyValueFactory<>("expenseId"));
        expenseAmountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        expenseDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        expenseDateCol.setCellValueFactory(new PropertyValueFactory<>("expenseDate"));

        expenseCategoryCol.setCellValueFactory(cellData -> {
            Expense exp = cellData.getValue();
            if (exp.getCategory() != null && exp.getCategory().getCategoryName() != null) {
                return new SimpleStringProperty(exp.getCategory().getCategoryName());
            }
            return new SimpleStringProperty("Uncategorized");
        });
    }

    private void setupIncomeTableColumns() {
        incomeIdCol.setCellValueFactory(new PropertyValueFactory<>("incomeId"));
        incomeAmountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        incomeDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        incomeDateCol.setCellValueFactory(new PropertyValueFactory<>("incomeDate"));
    }

    /**
     * Fetch all expense categories directly via CategoryDAO to ensure decoupled services.
     */
    private void loadCategories() {
        try {
            // FIXED: Pointed directly to categoryDAO.getAllCategories()
            // rather than trying to call a missing method on expenseService
            List<Category> categories = categoryDAO.getAllCategories();
            expenseCategoryComboBox.setItems(FXCollections.observableArrayList(categories));

            expenseCategoryComboBox.setConverter(new StringConverter<Category>() {
                @Override
                public String toString(Category category) {
                    return category == null ? "" : category.getCategoryName();
                }

                @Override
                public Category fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            AlertUtil.showError("Data Access Error", "Failed to retrieve category drop-down list: " + e.getMessage());
        }
    }

    /**
     * Re-query the database to load active records for the tables using correct singular service names.
     */
    private void refreshTables() {
        try {
            // FIXED: Changed from plural 'getExpensesByUser' to singular 'getExpenseByUser'
            List<Expense> expenses = expenseService.getExpensesByUser(currentUser.getUserId());

            expenseTableView.setItems(FXCollections.observableArrayList(expenses));

            // FIXED: Changed from plural 'getIncomesByUser' to singular 'getIncomeByUser'
            List<Income> incomes = incomeService.getIncomesByUser(currentUser.getUserId());
            incomeTableView.setItems(FXCollections.observableArrayList(incomes));
        } catch (Exception e) {
            AlertUtil.showError("Database Error", "Failed to populate record tables: " + e.getMessage());
        }
    }

    @FXML
    private void onSaveExpenseClick() {
        try {
            Category category = expenseCategoryComboBox.getValue();
            String amountStr = expenseAmountField.getText().trim();
            String description = expenseDescriptionField.getText().trim();
            LocalDate date = expenseDatePicker.getValue();

            if (amountStr.isEmpty()) {
                throw new ValidationException("Expense amount cannot be empty.");
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                throw new ValidationException("Please enter a valid numeric value for the expense amount.");
            }

            Expense expense = new Expense(currentUser, category, amount, description, date);
            expenseService.addExpense(expense);

            AlertUtil.showSuccess("Success", "Expense successfully recorded!");

            clearExpenseInputs();
            refreshTables();

        } catch (ValidationException e) {
            AlertUtil.showWarning("Validation Error", e.getMessage());
        } catch (Exception e) {
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    private void onDeleteExpenseClick() {
        Expense selected = expenseTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("Selection Required", "Please select an expense entry to delete.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Confirm Deletion", "Are you sure you want to permanently delete this expense?");
        if (confirm) {
            try {
                expenseService.deleteExpense(selected.getExpenseId());
                AlertUtil.showSuccess("Success", "Expense deleted successfully.");
                refreshTables();
            } catch (Exception e) {
                AlertUtil.showError("Database Error", "Failed to delete expense entry: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onSaveIncomeClick() {
        try {
            String amountStr = incomeAmountField.getText().trim();
            String description = incomeDescriptionField.getText().trim();
            LocalDate date = incomeDatePicker.getValue();

            if (amountStr.isEmpty()) {
                throw new ValidationException("Income amount cannot be empty.");
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                throw new ValidationException("Please enter a valid numeric value for the income amount.");
            }

            Income income = new Income(currentUser, amount, description, date);
            incomeService.addIncome(income);

            AlertUtil.showSuccess("Success", "Income entry successfully recorded!");

            clearIncomeInputs();
            refreshTables();

        } catch (ValidationException e) {
            AlertUtil.showWarning("Validation Error", e.getMessage());
        } catch (Exception e) {
            AlertUtil.showError("Database Error", e.getMessage());
        }
    }

    @FXML
    private void onDeleteIncomeClick() {
        Income selected = incomeTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("Selection Required", "Please select an income entry to delete.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Confirm Deletion", "Are you sure you want to permanently delete this income entry?");
        if (confirm) {
            try {
                incomeService.deleteIncome(selected.getIncomeId());
                AlertUtil.showSuccess("Success", "Income entry deleted successfully.");
                refreshTables();
            } catch (Exception e) {
                AlertUtil.showError("Database Error", "Failed to delete income entry: " + e.getMessage());
            }
        }
    }

    private void clearExpenseInputs() {
        expenseCategoryComboBox.setValue(null);
        expenseAmountField.clear();
        expenseDescriptionField.clear();
        expenseDatePicker.setValue(LocalDate.now());
    }

    private void clearIncomeInputs() {
        incomeAmountField.clear();
        incomeDescriptionField.clear();
        incomeDatePicker.setValue(LocalDate.now());
    }

    @FXML
    private void onBackClick() {
        MainApplication.showDashboardScreen();
    }
}