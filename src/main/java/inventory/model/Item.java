package inventory.model;

import javafx.beans.property.*;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Item extends Record<Item> {

    public StringProperty name = new SimpleStringProperty();
    public StringProperty description = new SimpleStringProperty();
    public IntegerProperty categoryId = new SimpleIntegerProperty();

    public Item(int id, String name, String description, int categoryId) {
        super(id);
        setName(name);
        setDescription(description);
        setCategoryId(categoryId);
    }

    public Item(String name, String description, int categoryId) {
        this(-1, name, description, categoryId);
    }

    public Item(Item item) {
        this(item.getId(), item.getName(), item.getDescription(), item.getCategoryId());
    }

    @Override
    public Item withId(int id) {
        return new Item(id, getName(), getDescription(), getCategoryId());
    }

    public int getId() {
        return id;
    }

    public final StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public final StringProperty descriptionProperty() {
        return description;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public final IntegerProperty categoryIdProperty() {
        return categoryId;
    }

    public int getCategoryId() {
        return categoryId.get();
    }

    public void setCategoryId(int categoryId) {
        this.categoryId.set(categoryId);
    }

    public boolean isAvailable() {
        //TODO getter to reservations that  are unreturned
//        return ReservationManager.getInstance().geRe;
        return true;
    }

    public List<Reservation> getCurrentReservations() {
        return ReservationManager.getInstance().getActiveReservations(id);
    }
}
