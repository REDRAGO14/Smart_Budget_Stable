package com.smartbudget.dao;

import com.smartbudget.database.DatabaseConnection;
import com.smartbudget.models.Budget;
import com.smartbudget.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {

    public boolean addBudget(int userId, int month, int year, double amount) {

        String deleteSql = "DELETE FROM budgets WHERE user_id = ? AND month = ? AND year = ?";
        String insertSql = "INSERT INTO budgets(user_id, month, year, amount) VALUES(?,?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement psDel = conn.prepareStatement(deleteSql)) {

                psDel.setInt(1, userId);
                psDel.setInt(2, month);
                psDel.setInt(3, year);

                psDel.executeUpdate();
            }

            try (PreparedStatement psIns = conn.prepareStatement(insertSql)) {

                psIns.setInt(1, userId);
                psIns.setInt(2, month);
                psIns.setInt(3, year);
                psIns.setDouble(4, amount);

                return psIns.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List getBudgetsByUser(int userId) {

        List list = new ArrayList<>();

        String sql = "SELECT * FROM budgets WHERE user_id = ? ORDER BY year DESC, month DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Budget b = new Budget();

                    b.setBudgetId(rs.getInt("budget_id"));
                    b.setMonth(rs.getInt("month"));
                    b.setYear(rs.getInt("year"));
                    b.setAmount(rs.getDouble("amount"));

                    User placeholder = new User();
                    placeholder.setUserId(userId);

                    b.setUser(placeholder);

                    list.add(b);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}