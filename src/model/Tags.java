package model;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// Adapted from lecture codes
public class Tags {
  /** the map to store information. */
  private Map<String, ArrayList> tags;

  /**
   * Use a map to store all the pictures and their tags. Use the path as the key and an ArrayList of
   * tags as the value.
   *
   * @param path the path to the .ser file
   */
  public Tags(String path) {
    try {
      // map to store tags
      tags = new HashMap<>();
      File allTags = new File(path);
      // Reads serializable objects from file.
      // Populates the record list using stored data, if it exists.
      if (allTags.exists()) {
        readFromFile(path);
      } else {
        allTags.createNewFile();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Update the map when the tags change
   *
   * @param list the list of tags that has been edited
   * @param path the path to the .ser file
   */
  void editTags(ArrayList list, String path) {
    tags.put(path, list);
  }

  /**
   * return an ArrayList of the picture's tags
   *
   * @param image the image calls for its tag list
   * @return the tag list of the image
   */
  ArrayList getTagList(Picture image) {
    if (!tags.containsKey(image.getPath())) {
      String name = image.getImageName();
      return splitTag(name);
    } else {
      return tags.get(image.getPath());
    }
  }

  /**
   * read the content of the map from the .ser file
   *
   * @param path the path to the .ser file
   */
  public void readFromFile(String path) {
    try {
      InputStream file = new FileInputStream(path);
      InputStream buffer = new BufferedInputStream(file);
      ObjectInput input = new ObjectInputStream(buffer);

      // deserialize the Map
      tags = (Map) input.readObject();
    } catch (IOException e) {
      saveToFile(path);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * write the map to the file
   *
   * @param path the path to the .ser file
   */
  public void saveToFile(String path) {
    try {
      OutputStream file = new FileOutputStream(path);
      OutputStream buffer = new BufferedOutputStream(file);
      ObjectOutput output = new ObjectOutputStream(buffer);

      // serialize the Map
      output.writeObject(tags);
      output.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * return an ArrayList of all existing tags
   *
   * @return the ArrayList containing all existing tags
   */
  public ArrayList<String> showAllTags() {
    Collection e = tags.values();
    ArrayList result = new ArrayList();
    for (Object list : e) {
      for (Object item : (ArrayList) list) {
        if (!result.contains(item)) {
          result.add(item);
        }
      }
    }
    return result;
  }

  /**
   * update the key as the path of the picture changes
   *
   * @param newPath the image that changes name
   * @param oldPath the older path, aka the older key in the map
   */
  void updateKey(String newPath, String oldPath) {
    if (tags.containsKey(oldPath)) {
      ArrayList record = tags.get(oldPath);
      while (tags.containsKey(oldPath)) {
        tags.remove(oldPath);
      }
      tags.put(newPath, record);
    }
  }

  /**
   * Return a list of String given a specific name.
   *
   * @param name String of the image name.
   * @return an ArrayList of String of tags.
   */
  private static ArrayList<String> splitTag(String name) {
    String[] tagArray = name.split(" @");
    ArrayList<String> tagLst = new ArrayList<>();
    for (int i = 1; i < tagArray.length; i++) {
      tagLst.add(tagArray[i]);
    }
    return tagLst;
  }
}
