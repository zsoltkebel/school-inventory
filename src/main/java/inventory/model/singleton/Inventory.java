package inventory.model.singleton;

import inventory.model.Category;
import inventory.model.Item;
import inventory.model.Reservation;
import inventory.utils.Database;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Class implementing the singleton design pattern
 *
 * Wraps the characteristics and functionality of an inventory that contains
 * a list of {@link Category} objects and a list of {@link Item} objects
 * Manages the operations (insert/edit/remove) and keeps the records of the database
 * synchronized with the objects in the memory
 */
public class Inventory {

    private static Inventory SINGLE_INVENTORY = null;

    public static Inventory getInstance() {
        if (SINGLE_INVENTORY == null) {
            SINGLE_INVENTORY = new Inventory();
        }
        return SINGLE_INVENTORY;
    }

    private Inventory() {
        this.categories = FXCollections.observableArrayList();
        this.items = FXCollections.observableArrayList();

        loadCategories();
        loadItems();
    }

    // instance variables
    private ObservableList<Category> categories;
    private ObservableList<Item> items;

    private Database DATABASE = Database.getInstance();

    // variable for filtering
    private IntegerProperty filterCategoryId = new SimpleIntegerProperty(-1);

    public ObservableList<Category> getCategories() {
        return categories;
    }

    public ObservableList<Item> getItems() {
        return items;
    }

    public IntegerProperty filterCategoryIdProperty() {
        return filterCategoryId;
    }

    public int getFilterCategoryId() {
        return filterCategoryId.get();
    }

    public void setFilterCategoryId(int id) {
        filterCategoryId.set(id);
    }

    public boolean isEmptyFilter() {
        return filterCategoryId.get() < 0;
    }

    public void clearFilter() {
        setFilterCategoryId(-1);
    }

    public boolean isExistingCategoryName(String name) {
        return categories.stream().anyMatch(category -> category.getName().equals(name));
    }

    private void loadCategories() {
        categories.clear();
        categories.addAll(DATABASE.queryAll(Category.class, Database.TABLE_CATEGORIES, false, null));
    }

    /**
     *
     * @param name
     */
    public void insertCategory(String name) {
        Category category = new Category(name, 0);

        categories.add(DATABASE.insert(Database.TABLE_CATEGORIES, category));
    }

    /**
     *
     * @param id
     */
    public void removeCategory(int id) {
        // delete all related items
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);

            if (item.getCategoryId() == id) {
                removeItem(item);
                i--;
            }
        }

        // delete category
        categories.removeIf(category -> category.getId() == id);

        DATABASE.remove(id, Database.TABLE_CATEGORIES);
    }

    private void loadItems() {
        items.clear();
        items.addAll(DATABASE.queryAll(Item.class, Database.TABLE_ITEMS, false, null));
    }

    /**
     *
     * @param name
     * @param description
     * @param categoryId
     */
    public void insertItem(String name, String description, int categoryId) {
        Item item = new Item(name, description, categoryId);

        Category category = getCategory(categoryId);
        category.incrementNumOfItems();

        DATABASE.update(Database.TABLE_CATEGORIES, category);

        items.add(DATABASE.insert(Database.TABLE_ITEMS, item));
    }

    /**
     *
     * @param item
     */
    public void removeItem(Item item) {
        // delete all related reservations
        for (int i = 0; i < ReservationManager.getInstance().reservationsObservable().size(); i++) {
            Reservation reservation = ReservationManager.getInstance().reservationsObservable().get(i);

            if (reservation.getItemId() == item.getId()) {
                ReservationManager.getInstance().remove(reservation);
                i--;
            }
        }

        // decrement number of items in category
        Category category = getCategory(item.getCategoryId());
        category.decrementNumOfItems();

        DATABASE.update(Database.TABLE_CATEGORIES, category);
        // delete item
        items.removeIf(current -> current.getId() == item.getId());

        DATABASE.remove(item.getId(), Database.TABLE_ITEMS);
    }

    public void updateItem(Item oldItem, Item newItem) {
        int index = items.indexOf(oldItem);

        if (oldItem.getCategoryId() != newItem.getCategoryId()) {
            getCategory(oldItem.getCategoryId()).decrementNumOfItems();
            getCategory(newItem.getCategoryId()).incrementNumOfItems();

            DATABASE.update(Database.TABLE_CATEGORIES, getCategory(oldItem.getCategoryId()));
            DATABASE.update(Database.TABLE_CATEGORIES, getCategory(newItem.getCategoryId()));
        }

        items.set(index, DATABASE.update(Database.TABLE_ITEMS, newItem));
    }

    public Category getCategory(int id) {
        if (id < 0) return null;
        return categories.stream()
                .filter(category -> category.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Category getFilterCategory() {
        return getCategory(getFilterCategoryId());
    }

    public Item getItem(int id) {
        if (id < 0) return null;
        return items.stream()
                .filter(item -> item.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Item[] getItems(int categoryId) {
        if (categoryId < 0) return null;
        return items.stream()
                .filter(item -> item.getCategoryId() == categoryId)
                .toArray(Item[]::new);
    }

    public int getNumOfItems(Category category) {
        return (int) items.stream()
                .filter(item -> item.getCategoryId() == category.getId())
                .count();
    }

    public boolean isItemAvailable(int id) {
        //TODO itemAvailable
        return true;
    }

}
