package controller;

import java.util.ArrayList;

import model.Log;
import model.PicManager;
import model.Picture;
import model.Tags;
import view.GUI;
import static model.PicManager.logPath;
import static model.PicManager.tagPath;

/**
 * A controller class that connects GUI/Model, when user inputs, GUI class reflect the input to
 * controller, the controller update the information to display/working with by calling methods in
 * Model. the controller only update the display information when needs, rather than everytime gets
 * called.
 */
public class Controller {

  // current working files/directories,etc.
  private ArrayList<Picture> pictures = new ArrayList<>();
  // a temerory list of tags, when initialized by selectpicture, it stores the clone of the taglist
  // as
  // a temerory list to working with, such as add tag/remove tag, this list will not be updated to
  // the target list
  // unless updateTags method is called-
  private ArrayList<String> tags;
  // record the current working picture, can be changed using selectpicture method
  private Picture currentPicture;
  // record the current path, used to update the folder when file is moved to other places
  private String currentFolderPath;

  /** no variable are setted during initialization */
  public Controller() {}

  /**
   * call log to get the logs of currentpicture, return a list of currently displaying pictures
   *
   * @return A list of the currently displaying picture's tags
   */
  public ArrayList<String[]> getHistory() {
    Log log = new Log(logPath);
    ArrayList<String[]> history = log.showHistory(currentPicture);
    return history;
  }

  /**
   * using move the current picture to another directory and also update the list of current
   * pictures *** update the Folderview in GUI
   *
   * @param path the target directory to move to
   */
  public void movePictureTo(String path) {
    PicManager.moveFile(currentPicture, path);
    selectFolder(currentFolderPath);
  }

  /**
   * using the current folder path to update the list of images in that folder i.e. update it when
   * called *** Update the FolderView in GUI
   *
   * @param path
   */
  public void selectFolder(String path) {
    pictures = PicManager.recGetAllImg(path);
    currentFolderPath = path;
    GUI.updateFolderView();
  }

  /**
   * switch the currently working picture to another picture, and update all informtion related ***
   * update the PictureView in GUI
   *
   * @param picture the picture to work with
   */
  public void selectPicture(Picture picture) {
    currentPicture = picture;
    tags = (ArrayList) picture.getTagList().clone();
    GUI.updatePictureView();
  }

  /**
   * get the name of the picture
   *
   * @return currently working picture's name
   */
  public String getPictureName() {
    return currentPicture.getOriginalName();
  }

  /**
   * get the path of the picture
   *
   * @return cuurrent picture's path
   */
  public String getPicturePath() {
    return currentPicture.getPath();
  }

  /**
   * get the name of the picture
   *
   * @return currently working picture's name
   */
  public String getPictureFullName() {
    return currentPicture.getImageName();
  }

  /**
   * add a temproray tag
   *
   * @param tag the tag to add
   */
  public void addTempTag(String tag) {
    tags.add(tag);
  }

  /**
   * remove the temporary tag from the list
   *
   * @param tag the tag to remove
   */
  public void removeTempTag(String tag) {
    tags.remove(tag);
  }

  /**
   * update the modification on the temporary to the actuall model/file stored *** update the
   * picture view since tags are updated
   */
  public void updateTags() {
    currentPicture.changeTagsTo(tags);
    GUI.updatePictureView();
  }

  /**
   * return All tags has ever been created
   *
   * @return a list containing global Tags
   */
  public static ArrayList<String> globalTags() {
    Tags tags = new Tags(tagPath);
    ArrayList<String> list = tags.showAllTags();
    return list;
  }

  /**
   * return the tags tagged to the current working picture
   *
   * @return list of tags
   */
  public ArrayList<String> getTags() {
    return currentPicture.getTagList();
  }

  /**
   * add one tag tho the actual picture NOT the temperory tag list *** update the pictureview since
   * the tags displaying has changed
   *
   * @param tag the tag to add
   */
  public void addTag(String tag) {
    currentPicture.addTags(tag);
    selectPicture(currentPicture);
    GUI.updatePictureView();
  }

  /**
   * given a name, take out all the tags it contains and return them in a list, with key " @"
   *
   * @param name the old name
   * @return the list of tags in the given name
   */
  public static ArrayList<String> splitTag(String name) {
    String[] tagArray = name.split(" @");
    ArrayList<String> tagLst = new ArrayList<>();
    for (int i = 1; i < tagArray.length; i++) {
      tagLst.add(tagArray[i]);
    }
    return tagLst;
  }

  /**
   * change the tags of the current picture to the given list of tags *** update the pictureview
   *
   * @param list the Arraylist of tags to change to
   */
  public void updateTagsTo(ArrayList<String> list) {
    currentPicture.changeTagsTo(list);
    selectPicture(currentPicture);
    GUI.updatePictureView();
  }

  /**
   * remove the given tag from all pictures in the directory selected
   *
   * @param tag the tag to be deleted
   */
  public void removeTagFromAll(String tag) {
    for (Picture p : pictures) {
      ArrayList<String> clone = (ArrayList<String>) p.getTagList().clone();
      while (clone.contains(tag)) {
        clone.remove(tag);
      }
      if (!p.getTagList().equals(clone)) {
        p.changeTagsTo(clone);
      }
    }
    GUI.updateTags();
  }

  /**
   * replace one tag with another tag and apply it to all images in the directory selected
   *
   * @param tag the tag to be replaced
   * @param target the tag used to replace
   */
  public void renameTag(String tag, String target) {
    if (!tag.equals(target)) {
      for (Picture p : pictures) {
        ArrayList<String> clone = (ArrayList<String>) p.getTagList().clone();
        while (clone.contains(tag)) {
          clone.remove(tag);
          clone.add(target);
        }
        if (!p.getTagList().equals(clone)) {
          p.changeTagsTo(clone);
        }
      }
      GUI.updateTags();
    }
  }

  /**
   * getter for the current picture list to work with
   *
   * @return the array list of pictures
   */
  public ArrayList<Picture> getPictures() {
    return pictures;
  }
}
