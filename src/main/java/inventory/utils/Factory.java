package inventory.utils;

import inventory.model.Category;
import inventory.model.Item;
import inventory.model.Reservation;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Factory {

    public static Reservation newReservation(ResultSet rs) {
        try {
            return new Reservation(
                    rs.getInt("id"),
                    rs.getDate("date").toLocalDate(),
                    rs.getInt("lessonId"),
                    rs.getInt("itemId"),
                    rs.getString("name"),
                    rs.getString("comment"),
                    rs.getBoolean("returned")
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Category newCategory(ResultSet rs) {
        try {
            return new Category(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("numOfItems")
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Item newItem(ResultSet rs) {
        try {
            return new Item(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getInt("categoryId")
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
