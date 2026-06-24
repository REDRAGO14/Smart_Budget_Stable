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
public Budget getBudgetForPeriod(int userId, int month, int year) {

    String sql = "SELECT * FROM budgets WHERE user_id = ? AND month = ? AND year = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);
        ps.setInt(2, month);
        ps.setInt(3, year);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                Budget b = new Budget();

                b.setBudgetId(rs.getInt("budget_id"));
                b.setMonth(rs.getInt("month"));
                b.setYear(rs.getInt("year"));
                b.setAmount(rs.getDouble("amount"));

                User placeholder = new User();
                placeholder.setUserId(userId);

                b.setUser(placeholder);

                return b;
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null;
}

public double getSpentForPeriod(int userId, int month, int year) {

    String sql = "SELECT SUM(amount) AS total_spent FROM expenses " +
            "WHERE user_id = ? AND EXTRACT(MONTH FROM expense_date) = ? AND EXTRACT(YEAR FROM expense_date) = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);
        ps.setInt(2, month);
        ps.setInt(3, year);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("total_spent");
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return 0.0;
}
public boolean deleteBudget(int budgetId) {

    String sql = "DELETE FROM budgets WHERE budget_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, budgetId);

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}