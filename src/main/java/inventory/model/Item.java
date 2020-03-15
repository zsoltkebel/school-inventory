package inventory.model;

import inventory.model.singleton.ReservationManager;
import javafx.beans.property.*;

import java.util.List;

public class Item extends Record<Item> {

    private StringProperty name = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private IntegerProperty categoryId = new SimpleIntegerProperty();

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

    public List<Reservation> getCurrentReservations() {
        return ReservationManager.getInstance().activeReservations(getId());
    }
}
