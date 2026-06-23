package com.smartbudget.dao;

import com.smartbudget.database.DatabaseConnection;
import com.smartbudget.models.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public boolean addCategory(String name) {
        String sql = "INSERT INTO categories(category_name) VALUES(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Category getCategoryById(int category_id){
        String sql = "SELECT * FROM categories WHERE category_id = ?";


        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setInt(1, category_id);

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    Category category = new Category();
                    category.setCategoryId(rs.getInt("category_id"));
                    category.setCategoryName(rs.getString("category_name"));

                    return category;
                }

            }catch (SQLException e){
                throw  new SQLException();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // FIXED: Explicitly added <Category> generic parameter mapping for type-safety
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT category_id, category_name FROM categories";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getInt("category_id"));
                category.setCategoryName(rs.getString("category_name"));
                list.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}