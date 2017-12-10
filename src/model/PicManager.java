package model;

import java.io.File;
import java.util.ArrayList;

/** Operation with the picture directory and pictures. */
public class PicManager {
  static final String sep = File.separator;
  public static final String tagPath = "." + sep + "tags.ser";
  public static final String logPath = "." + sep + "log.ser";

  private static Tags tag = new Tags(tagPath);
  private static Log log = new Log(logPath);

  public PicManager() {}

  /**
   * Judge whether a file is an image, by its suffix.
   *
   * @param file a file to be judge.
   * @return Whether or not the file is an image.
   */
  private static boolean isImage(File file) {
    if (file.getName().lastIndexOf(".") != -1) {
      String suffix = file.getName().substring(file.getName().lastIndexOf("."));
      return (suffix.equals(".jpg")
          || suffix.equals(".jpeg")
          || suffix.equals(".png")
          || suffix.equals(".bmp")
          || suffix.equals(".gif"));
    }
    return false;
  }

  /**
   * Get all the pictures in a given directory path.
   *
   * @param directoryPath the absolute path of a directory.
   * @return an ArrayList of Picture contains the Pictures in the given directory.
   */
  public static ArrayList<Picture> getAllImages(String directoryPath) {
    ArrayList<Picture> allPictures = new ArrayList<>();
    File directory = new File(directoryPath);
    String[] allFileLst = directory.list();
    if (allFileLst != null) {
      for (String filePath : allFileLst) {
        // System.out.println("the " + i + "file: " + filePath );
        String absolutePath = directoryPath + sep + filePath;
        // System.out.println("file: " + file.getName() + "         path: " + file.getPath());
        if (isImage(new File(absolutePath))) {
          // System.out.println("it is an image!" + absolutePath);
          Picture pic = new Picture(absolutePath);
          allPictures.add(pic);
        }
      }
    }
    // System.out.println(allPictures.size() + " pictures in the directory");
    return allPictures;
  }

  /**
   * Get all the images in the root directory recursively regardless where the image is.
   *
   * @param directoryPath the absolute path of the directory.
   * @return an ArrayList of Picture contains all images in the root directory.
   */
  public static ArrayList<Picture> recGetAllImg(String directoryPath) {
    ArrayList<Picture> headLst = getAllImages(directoryPath);
    File directory = new File(directoryPath);
    String[] allFileLst = directory.list();
    if (allFileLst != null) {
      for (String filePath : allFileLst) {
        String absolutePath = directoryPath + sep + filePath;
        File file = new File(absolutePath);
        if (file.isDirectory()) {
          headLst.addAll(recGetAllImg(absolutePath));
        }
      }
    }
    return headLst;
  }

  /**
   * Move picture operation.
   *
   * @param pic the picture to be moved.
   * @param destinationDirectoryPath the absolute path of the destination directory path.
   */
  public static void moveFile(Picture pic, String destinationDirectoryPath) {
    String oldPath = pic.getPath();
    String newPath = destinationDirectoryPath + sep + new File(pic.getPath()).getName();
    pic.renameFile(pic.getPath(), newPath);
    File file = new File(newPath);
    if (file.exists()) {
      // System.out.println("File is moved successfully!");
      pic.setPath(newPath);
      tag.readFromFile(tagPath);
      log.readFromFile(logPath);
      tag.updateKey(pic.getPath(), oldPath);
      log.updateKey(pic.getPath(), oldPath);
      tag.saveToFile(tagPath);
      log.saveToFile(logPath);
    } else {
      System.out.println("File is failed to move!");
    }
  }

  /**
   * Return a list of String given a specific name.
   *
   * @param name String of the image name.
   * @return an ArrayList of String of tags.
   */
  public static ArrayList<String> splitTag(String name) {
    String[] tagArray = name.split(" @");
    ArrayList<String> tagLst = new ArrayList<>();
    for (int i = 1; i < tagArray.length; i++) {
      tagLst.add(tagArray[i]);
    }
    return tagLst;
  }
}
