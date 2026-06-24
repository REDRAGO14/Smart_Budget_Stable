package com.smartbudget.dao;

import com.smartbudget.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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