package com.smartbudget.dao;

import com.smartbudget.database.DatabaseConnection;
import com.smartbudget.models.Category;
import com.smartbudget.models.Expense;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    // ADD EXPENSE
    public boolean addExpense(int userId, int categoryId, double amount, String description, Date date) {
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

    public List getExpensesByUser(int userId) {
        List list = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE user_id = ?";

        CategoryDAO categoryDAO = new CategoryDAO();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Expense expense = new Expense();
                    expense.setExpenseId(rs.getInt("expense_id"));
                    expense.setAmount(rs.getDouble("amount"));
                    expense.setDescription(rs.getString("description"));

                    if (rs.getDate("expense_date") != null) {
                        expense.setExpenseDate(rs.getDate("expense_date").toLocalDate());
                    }

                    // 1. Grab the raw category ID number from the database row
                    int categoryId = rs.getInt("category_id");

                    // 2. Use  existing DAO to find the matching Category object
                    Category category = categoryDAO.getCategoryById(categoryId);

                    // 3. Attach it to  expense record
                    expense.setCategory(category);

                    list.add(expense);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // DELETE EXPENSE
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
}