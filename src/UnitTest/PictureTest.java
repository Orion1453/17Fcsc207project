package UnitTest;

import model.Picture;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.ArrayList;

import static model.PicManager.getAllImages;
import static model.PicManager.recGetAllImg;
import static org.junit.jupiter.api.Assertions.*;

class PictureTest {

  @Test
  void testGetOriginalName() {
    Picture p = new Picture("./testPic/001.jpg");
    String name1 = "001";
    String name2 = p.getOriginalName();
    assertEquals(name1, name2);
  }

  @Test
  void testGetPath() {
    Picture p = new Picture("./testPic/003.jpg");
    String path1 = "./testPic/003.jpg";
    String path2 = p.getPath();
    assertEquals(path1, path2);
  }

  @Test
  void testGetImageName() {
    Picture p = new Picture("./testPic/2b/nier.png");
    String name1 = "nier";
    String name2 = p.getImageName();
    assertEquals(name1, name2);
  }

  @Test
  void testGetTagList() {
    Picture p = new Picture("./testPic/001.jpg");
    ArrayList l1 = new ArrayList();
    ArrayList l2 = p.getTagList();
    assertEquals(l1, l2);
  }

  @Test
  void testChangeTagsTo() {
    Picture p = new Picture("./testPic/2b/ab/002.jpg");
    ArrayList l1 = new ArrayList();
    l1.add("test");
    p.changeTagsTo(l1);
    ArrayList l2 = p.getTagList();
    assertEquals(l1, l2);
    String name1 = "002 @test";
    String name2 = p.getImageName();
    assertEquals(name1, name2);
    ArrayList l3 = new ArrayList();
    p.changeTagsTo(l3);
    String path = "./testPic/2b/ab/002.jpg";
    assertEquals(path, p.getPath());
  }
}
