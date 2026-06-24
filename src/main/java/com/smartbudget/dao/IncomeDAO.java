 package com.smartbudget.dao;

import com.smartbudget.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import com.smartbudget.models.Income;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
public List getIncomeByUser(int userId) {

    List list = new ArrayList<>();
    String sql = "SELECT * FROM incomes WHERE user_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Income income = new Income();

                income.setIncomeId(rs.getInt("income_id"));
                income.setAmount(rs.getDouble("amount"));
                income.setDescription(rs.getString("description"));

                if (rs.getDate("income_date") != null) {
                    income.setIncomeDate(rs.getDate("income_date").toLocalDate());
                }

                list.add(income);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}
if (rs.getDate("income_date") != null) {
    income.setIncomeDate(rs.getDate("income_date").toLocalDate());
}
public static boolean deleteIncome(int incomeId) {

    String sql = "DELETE FROM incomes WHERE income_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, incomeId);

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}