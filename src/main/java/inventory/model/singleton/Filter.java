package inventory.model.singleton;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * Singleton
 */
public class Filter {

    private static Filter SINGLE_FILTER = new Filter();

    public static Filter getInstance() {
        return SINGLE_FILTER;
    }

    private SimpleIntegerProperty categoryIdProperty;

    private Filter() {
        categoryIdProperty = new SimpleIntegerProperty(-1);
    }

    public int getCategoryId() {
        return categoryIdProperty.get();
    }

    public void setCategoryId(int id) {
        categoryIdProperty.set(id);
    }

    public boolean isEmpty() {
        return categoryIdProperty.get() < 0;
    }

    public void clear() {
        categoryIdProperty.set(-1);
    }

    public interface FilterChangeListener {
        void onFilterChanged();
    }

    public void addListener(FilterChangeListener filterChangeListener) {
        categoryIdProperty.addListener((observableValue, oldValue, newValue) -> {

            filterChangeListener.onFilterChanged();
        });
    }
}
