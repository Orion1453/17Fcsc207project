package model;

import java.nio.file.Files;
import java.util.ArrayList;
import java.io.*;

import static model.PicManager.logPath;
import static model.PicManager.sep;
import static model.PicManager.tagPath;

/** Operations on a single image. */
public class Picture {
  /** Tags in the image name. */
  private ArrayList<String> tagList;
  /** Current name. */
  private String imageName;
  /** The very original name before change. */
  private String originalName;
  /** file path. */
  private String path;

  private Tags tag = new Tags(tagPath);
  private Log log = new Log(logPath);

  /**
   * Constructor for Picture.
   *
   * @param path the absolute path of a picture to initiate the Picture.
   */
  public Picture(String path) {
    this.path = path;
    // get the current name of image name below.
    imageName = getName(this.path);
    originalName = log.getOriginalName(this);
    tagList = tag.getTagList(this);
    tag.readFromFile(tagPath);
    tag.editTags(tagList, path);
    tag.saveToFile(tagPath);
  }

  /* Getters below */
  /**
   * Get the original name.
   *
   * @return the original name of the Picture.
   */
  public String getOriginalName() {
    return originalName;
  }

  /**
   * get the absolute path .
   *
   * @return the path of this picture.
   */
  public String getPath() {
    return path;
  }

  /**
   * Get the name of Image.
   *
   * @return the imageName of the Picture.
   */
  public String getImageName() {
    return imageName;
  }

  /**
   * get the tag list.
   *
   * @return the tag list of the Picture.
   */
  public ArrayList<String> getTagList() {
    return tagList;
  }

  /* Getters above. */

  /**
   * To add a new tag to the picture.
   *
   * @param newTag the String of the new tag.
   */
  public void addTags(String newTag) {
    tagList.add(newTag);
    tag.readFromFile(tagPath);
    tag.editTags(tagList, path);
    this.rename();
  }

  //  /**
  //   * To remove a tag in the picture.
  //   *
  //   * @param oldTag the tag in the Picture to be removed.
  //   */
  //  public void removeTags(String oldTag) {
  //    int index = tagList.indexOf(oldTag);
  //    tagList.remove(index);
  //    tag.readFromFile(tagPath);
  //    tag.editTags(tagList, path);
  //    this.rename();
  //  }

  /** Rename a picture given by the current tag list. */
  private void rename() {
    StringBuilder newName = new StringBuilder(originalName);
    for (String tag : tagList) {
      newName.append(" @");
      newName.append(tag);
    }
    String oldName = imageName;
    imageName = newName.toString();
    String oldPath = path;
    String newPath = generateNewPath(path, imageName);
    this.path = newPath;
    renameFile(oldPath, newPath);
    log.readFromFile(logPath);
    log.updateKey(newPath, oldPath);
    log.update(oldName, this);
    log.saveToFile(logPath);
    tag.updateKey(newPath, oldPath);
    tag.saveToFile(tagPath);
  }

  /**
   * Return the name of picture from the String of path.
   *
   * @param path the absolute path of a file.
   * @return the name of the file.
   */
  private String getName(String path) {
    File file = new File(path);
    return file.getName().substring(0, file.getName().lastIndexOf("."));
  }

  /**
   * Return a new Path given by the old path and new name.
   *
   * @param path the absolute path of a file.
   * @param currName the name want to be changed to.
   * @return a String of new absolute path of the file.
   */
  private String generateNewPath(String path, String currName) {
    StringBuilder reversePath = new StringBuilder(path).reverse();
    StringBuilder reverseCurrName = new StringBuilder(currName).reverse();
    int start = reversePath.indexOf(".") + 1;
    int end = reversePath.indexOf(sep);
    reversePath.delete(start, end);
    reversePath.insert(start, reverseCurrName);
    return reversePath.reverse().toString();
  }

  /**
   * Set the path to the given path.
   *
   * @param path the new path to be set to the Picture.
   */
  void setPath(String path) {
    this.path = path;
  }

  /**
   * Adapted from
   * https://stackoverflow.com/questions/1000183/reliable-file-renameto-alternative-on-windows Use
   * this to enable rename and move operation on Windows.
   *
   * @param oldPath the absolute path of a file.
   * @param newPath the new absolute path of a file.
   */
  void renameFile(String oldPath, String newPath) {
    try {
      Files.move(
          new File(oldPath).toPath(),
          new File(newPath).toPath(),
          java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Used in "Go back to History", force a tags in a picture to become the target tags. Only the
   * last assignment will be recorded by log.
   *
   * @param target ArrayList of String of tags.
   */
  public void changeTagsTo(ArrayList<String> target) {
    this.tagList = target;
    tag.readFromFile(tagPath);
    tag.editTags(tagList, path);
    this.rename();
  }
}
