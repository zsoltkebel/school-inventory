package inventory.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JSONUtil {

    public interface JSONObjectReadListener {
        void onObjectRead(JSONObject object);
    }

    public static void readJSONArray(String fileName, JSONObjectReadListener jsonObjectReadListener) {

        try {
            File file = new File(fileName);
            if (file.createNewFile());

            FileReader reader = new FileReader(fileName);

            //JSON parser object to parse read file
            JSONParser jsonParser = new JSONParser();
            //Read JSON file
            JSONArray jsonArray = (JSONArray) jsonParser.parse(reader);

            jsonArray.forEach(jsonObject -> jsonObjectReadListener.onObjectRead((JSONObject) jsonObject));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {

        }
    }
}
