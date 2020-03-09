package inventory.model.singleton;

import inventory.model.Category;
import inventory.model.Item;
import inventory.utils.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Singleton
 */
public class Inventory {

    private static Inventory SINGLE_INVENTORY = null;

    public static Inventory getInstance() {
        if (SINGLE_INVENTORY == null) {
            SINGLE_INVENTORY = new Inventory();
        }
        return SINGLE_INVENTORY;
    }

    private ObservableList<Category> categories;
    private ObservableList<Item> items;

    private Database DATABASE = Database.getInstance();

    private Inventory() {
        this.categories = FXCollections.observableArrayList();
        this.items = FXCollections.observableArrayList();

        loadCategories();
        loadItems();
    }

    public ObservableList<Category> getCategories() {
        return categories;
    }

    public ObservableList<Item> getItems() {
        return items;
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
                DATABASE.delete(item.getId(), Database.TABLE_ITEMS);
                items.remove(i--);
            }
        }

        // delete category
        categories.removeIf(category -> category.getId() == id);

        DATABASE.delete(id, Database.TABLE_CATEGORIES);
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

//        saveCategoriesJSON();
    }

    /**
     *
     * @param item
     */
    public void removeItem(Item item) {
        // decrement number of items in category
        Category category = getCategory(item.getCategoryId());
        category.decrementNumOfItems();

        DATABASE.update(Database.TABLE_CATEGORIES, category);
        // delete item
        items.removeIf(current -> current.getId() == item.getId());

        DATABASE.delete(item.getId(), Database.TABLE_ITEMS);
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
