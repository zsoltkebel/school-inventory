package inventory.model;

import javafx.beans.property.*;

/**
 * Category class
 * following tha JavaFX Bean Pattern/Convention
 */
public class Category extends Record<Category> {

    private StringProperty name = new SimpleStringProperty();
    private IntegerProperty numOfItems = new SimpleIntegerProperty(0);

    public Category(int id, String name, int numOfItems) {
        super(id);
        setName(name);
        setNumOfItems(numOfItems);
    }

    public Category(String name, int numOfItems) {
        this(-1, name, numOfItems);
    }

    public Category(Category category) {
        this(category.getId(), category.getName(), category.getNumOfItems());
    }

    @Override
    public Category withId(int id) {
        return new Category(id, this.name.get(), this.numOfItems.get());
    }

    public final StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return nameProperty().get();
    }

    public void setName(String name) {
        nameProperty().set(name);
    }

    public final IntegerProperty numOfItemsProperty() {
        return numOfItems;
    }

    public int getNumOfItems() {
        return numOfItems.get();
    }

    public void setNumOfItems(int numOfItems) {
        this.numOfItems.set(numOfItems);
    }

    public void incrementNumOfItems() {
        numOfItems.set(getNumOfItems() + 1);
    }

    public void decrementNumOfItems() {
        numOfItems.set(Math.max(getNumOfItems() - 1, 0));
    }
}
