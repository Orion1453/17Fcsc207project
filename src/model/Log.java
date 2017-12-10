package model;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Adapted from lecture codes

/**
 * Use a map to store all the pictures and their history of name-changing. Use the path as the key
 * and an ArrayList of Array as the value. In the Array there are 3 Strings, the old name, the new
 * name and the time.
 */
public class Log {
  /** the map that stores the information. */
  private Map<String, ArrayList> logs;

  /**
   * Use a map to store all the log information. The key is the path of the image the value is an
   * At=rrayList of Arrays, while in the Array it contains the older name, the new name and the time
   * of changing.
   *
   * @param path the path to the .ser file
   */
  public Log(String path) {
    try {
      // map to store logs
      logs = new HashMap<>();
      File allLogs = new File(path);
      // Reads serializable objects from file.
      // Populates the record list using stored data, if it exists.
      if (allLogs.exists()) {
        readFromFile(path);
      } else {
        allLogs.createNewFile();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Update the map when the name-changing happens
   *
   * @param oldName the older name of the image
   * @param image the image that needs to update its log
   */
  void update(String oldName, Picture image) {
    if (!logs.containsKey(image.getPath())) {
      ArrayList records = new ArrayList();
      String time = LocalDateTime.now().toString();
      String newName = image.getImageName();
      String[] record = {oldName, newName, time};
      records.add(record);
      String path = image.getPath();
      logs.put(path, records);
    } else {
      String time = LocalDateTime.now().toString();
      String newName = image.getImageName();
      String[] record = {oldName, newName, time};
      ArrayList records = logs.get(image.getPath());
      records.add(record);
      logs.put(image.getPath(), records);
    }
  }

  /**
   * return the ArrayList to show the whole history of name-changing
   *
   * @param image the image that needs its name-changing history
   * @return an ArrayList of Arrays that contains all of the image's log
   */
  public ArrayList<String[]> showHistory(Picture image) {
    try {
      ArrayList result = new ArrayList();
      ArrayList records = logs.get(image.getPath());
      for (int i = 0; i < records.size(); i++) {
        String[] item = (String[]) records.get(i);
        result.add(item);
      }
      return result;
    } catch (NullPointerException e) {
      return new ArrayList();
    }
  }

  /**
   * Read the content of the map from the .ser file
   *
   * @param path the path to the .ser file
   */
  void readFromFile(String path) {
    try {
      InputStream file = new FileInputStream(path);
      InputStream buffer = new BufferedInputStream(file);
      ObjectInput input = new ObjectInputStream(buffer);

      // deserialize the Map
      logs = (Map) input.readObject();
    } catch (IOException e) {
      saveToFile(path);
    } catch (ClassNotFoundException e1) {
      e1.printStackTrace();
    }
  }

  /**
   * write the map to the file
   *
   * @param path the path to the .ser file
   */
  void saveToFile(String path) {
    try {
      OutputStream file = new FileOutputStream(path);
      ObjectOutput output = new ObjectOutputStream(file);

      // serialize the Map
      output.writeObject(logs);
      output.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * return the original name of the picture -- the old name in the first Array in the ArrayList
   *
   * @param image the image searches for its original name
   * @return the images original name
   */
  String getOriginalName(Picture image) {
    if (logs.containsKey(image.getPath())) {
      if (logs.get(image.getPath()).size() != 0) {
        String[] record = (String[]) (logs.get(image.getPath()).get(0));
        return record[0];
      }
    }
    return image.getImageName();
  }

  /**
   * update the key as the path of the picture changes
   *
   * @param newPath the image that its name has changed
   * @param oldPath the image's older name, aka the older key in the map
   */
  void updateKey(String newPath, String oldPath) {
    if (logs.containsKey(oldPath)) {
      ArrayList record = logs.get(oldPath);
      while (logs.containsKey(oldPath)) {
        logs.remove(oldPath);
      }
      logs.put(newPath, record);
    }
  }
}
