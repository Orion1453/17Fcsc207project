package UnitTest;

import model.Picture;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.ArrayList;

import static model.PicManager.*;
import static org.junit.jupiter.api.Assertions.*;

class PicManagerTest {

  @Test
  void testGetAllImages() {
    ArrayList<Picture> lst = getAllImages("./testPic");
    int num = getAllImages("./testPic").size();
    assertEquals(2, num);
    String name1 = new File("./testPic/003.jpg").getName();
    String name2 = new File("./testPic/001.jpg").getName();
    assertEquals(name1, lst.get(0).getImageName() + ".jpg");
    assertEquals(name2, lst.get(1).getImageName() + ".jpg");
  }

  @Test
  void testRecGetAllImg() {
    ArrayList<Picture> lst = recGetAllImg("./testPic");
    int num = recGetAllImg("./testPic").size();
    assertEquals(7, num);
    String name1 = new File("./testPic/2b/nier.png").getName();
    String name2 = new File("./testPic/2b/ab/002.jpg").getName();
    assertEquals(name1, lst.get(2).getImageName() + ".png");
    assertEquals(name2, lst.get(3).getImageName() + ".jpg");
  }

  @Test
  void testMoveFile() {
    String oldPath = new File("./testPic/003.jpg").getAbsolutePath();
    String dirPath = new File("./testPic/2b").getAbsolutePath();
    Picture pic = new Picture(oldPath);
    moveFile(pic, dirPath);
    assertTrue(new File(dirPath + "/003.jpg").exists());
    moveFile(pic, new File(dirPath).getParentFile().getAbsolutePath());
  }

  @Test
  void testSplitTag() {
    String name1 = "csc258 @sb @cnm @nmb";
    String name2 = " @sb";
    String name3 = "csc258";
    ArrayList<String> lst1 = new ArrayList<>();
    lst1.add("sb");
    lst1.add("cnm");
    lst1.add("nmb");
    ArrayList<String> lst2 = new ArrayList<>();
    lst2.add("sb");
    ArrayList<String> lst3 = new ArrayList<>();
    assertEquals(lst1, splitTag(name1));
    assertEquals(lst2, splitTag(name2));
    assertEquals(lst3, splitTag(name3));
  }
}
