package inventory;

import inventory.model.Item;

import java.io.*;

public class FileIO {

    public static void saveObject(String fileName, Serializable object) {
        try {
            FileOutputStream f = new FileOutputStream(new File(fileName));
            ObjectOutputStream o = new ObjectOutputStream(f);

            o.writeObject(object);

            o.close();
            f.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }

    public static <T extends Serializable> T readObject(String fileName) {
        T object = null;
        try {
            FileInputStream fi = new FileInputStream(new File(fileName));
            ObjectInputStream oi = new ObjectInputStream(fi);

            object = (T) oi.readObject();

            oi.close();
            fi.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return object;
    }
}
