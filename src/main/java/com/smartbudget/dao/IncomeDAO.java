 package com.smartbudget.dao;

import com.smartbudget.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

public class IncomeDAO {

    // ADD INCOME
    public static boolean addIncome(int userId, double amount, String description, Date date) {

        String sql = "INSERT INTO incomes(user_id, amount, description, income_date) VALUES(?,?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDouble(2, amount);
            ps.setString(3, description);
            ps.setDate(4, date);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}