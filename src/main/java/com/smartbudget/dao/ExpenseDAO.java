package com.smartbudget.dao;

import com.smartbudget.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.smartbudget.models.Expense;
import com.smartbudget.models.Category;
public class ExpenseDAO {

    // ADD EXPENSE
    public boolean addExpense(int userId, int categoryId, double amount, String description, java.sql.Date date) {
        String sql = "INSERT INTO expenses(user_id, category_id, amount, description, expense_date) VALUES(?,?,?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, categoryId);
            ps.setDouble(3, amount);
            ps.setString(4, description);
            ps.setDate(5, date);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}


public List<Expense> getExpensesByUser(int userId) {
    List<Expense> list = new ArrayList<>();
    String sql = "SELECT * FROM expenses WHERE user_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Expense expense = new Expense();

                expense.setExpenseId(rs.getInt("expense_id"));
                expense.setAmount(rs.getDouble("amount"));
                expense.setDescription(rs.getString("description"));

                list.add(expense);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}


CategoryDAO categoryDAO = new CategoryDAO();

int categoryId = rs.getInt("category_id");
Category category = categoryDAO.getCategoryById(categoryId);
expense.setCategory(category);

public boolean deleteExpense(int expenseId) {
    String sql = "DELETE FROM expenses WHERE expense_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, expenseId);
        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}