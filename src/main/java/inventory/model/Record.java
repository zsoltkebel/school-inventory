package inventory.model;

public abstract class Record<T extends Record<?>> {

    public final int id;

    public Record(int id) {
        this.id = id;
    }

    abstract public T withId(int id);

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                '}';
    }
}
