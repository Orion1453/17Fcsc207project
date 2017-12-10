package view;

import controller.Controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import model.Picture;

/**
 * When this program is run, displaying a window allows user to modify images. codes for javafx
 * learned/adapted from https://www.youtube.com/watch?v=FLkOX4Eez6o and its following videos as
 * tutorials.
 *
 * <p>Differ from the classical MCV design pattern, i decide to break the "GUI.changeView()" into
 * multiple pieces. so when my GUI got something to pass to controller. it calls the controller to
 * update its information based on the user input, and the controller updates only the part related
 * to the change. in the end the GUI would refresh its output to display the changes controller
 * made. this class associate with javafx scene outputs.
 */
public class GUI extends Application {
  // since no instance is created we dont need any instance methods nor any instance variables;
  // the main stage the users view,selectphoto, and analyze photo
  private static Stage mainWindow = new Stage();
  // a stage used to show tag details
  private static Stage taginfo = new Stage();
  // storing some Scene for updating
  private static Scene folderView, pictureView;
  // the stage that is going to display the history of current displaying picture when needed
  private static Stage historyWindow = new Stage();
  // the stage that manages global tags.
  private static Stage globalTags = new Stage();
  // initialize a controller to this GUI associates with
  private static Controller controller = new Controller();

  public static void main(String[] args) {
    launch(args);
  }

  public void start(Stage PrimaryStage) {
    // set features to 4 stages
    mainWindow.setTitle("Image Analyzer");
    mainWindow.setMinHeight(680);
    mainWindow.setMinWidth(853);
    mainWindow.setMaxHeight(680);
    mainWindow.setMaxWidth(853);
    historyWindow.setMinWidth(1150);
    historyWindow.setMaxWidth(1150);
    historyWindow.setMinHeight(600);
    historyWindow.setMaxHeight(600);
    historyWindow.setTitle("Naming History");
    historyWindow.initModality(Modality.APPLICATION_MODAL);
    taginfo.setMinHeight(250);
    taginfo.setMinWidth(250);
    taginfo.setTitle("full text");
    taginfo.initModality(Modality.APPLICATION_MODAL);
    globalTags.setMinWidth(1000);
    globalTags.setMinHeight(600);
    globalTags.setMaxWidth(1000);
    globalTags.setMaxHeight(600);
    globalTags.initModality(Modality.APPLICATION_MODAL);
    // open up an empty folder view as start point, and display it
    updateFolderView();
    mainWindow.setScene(folderView);
    mainWindow.show();
  }

  /** up date the picture view based on the currently working picture in Controller */
  public static void updatePictureView() {
    FileInputStream i1file = null;
    try {
      i1file = new FileInputStream(controller.getPicturePath());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    Image image = new Image(i1file);
    BorderPane display = new BorderPane();

    // generate the image
    ImageView imageView = new ImageView(image);
    imageView.setPreserveRatio(true);
    imageView.setFitHeight(450);
    imageView.setFitWidth(580);

    // get, set all 3 parts
    Node toppart = pViewTop();
    Node botpart = pViewBot();
    Node leftpart = pViewLeft();
    display.setCenter(imageView);
    display.setTop(toppart);
    display.setBottom(botpart);
    display.setLeft(leftpart);
    display.setMinSize(850, 650);
    display.setMaxSize(850, 650);

    // set the Scene
    pictureView = new Scene(display, 850, 650);
  }

  /**
   * generate part of the picture View based on current picture
   *
   * @return the node display of top part of the pictureView
   */
  private static Node pViewTop() {
    HBox hb = new HBox();
    Button b1 = new Button("Move This\nPicture to ");
    b1.setMinHeight(80);
    b1.setMinWidth(100);
    // calls the controller to move the picture to the selected directory
    b1.setOnAction(
        e -> {
          DirectoryChooser directoryChooser = new DirectoryChooser();
          File selectedDirectory = directoryChooser.showDialog(mainWindow);
          if (selectedDirectory != null) {
            controller.movePictureTo(selectedDirectory.getAbsolutePath());
          }
        });
    Button b2 = new Button("Back");
    b2.setMinWidth(100);
    b2.setMinHeight(80);
    // display the folderview Scene
    b2.setOnAction(e -> mainWindow.setScene(folderView));
    Button b3 = new Button("View\nHistory");
    b3.setMinHeight(80);
    b3.setMinWidth(100);
    // pop up a window displaying histories of the picture
    b3.setOnAction(
        e -> {
          viewHistory();
        });
    Button b4 = new Button("Browse\nFolder");
    // pop up a window selecting directory and display the folderView of the folder
    b4.setOnAction(
        e -> {
          selectFolder();
        });
    b4.setMinHeight(80);
    b4.setMinWidth(100);
    String path = controller.getPicturePath();
    String originalName = controller.getPictureName();
    Label l1 =
        diyLabel(
            "original name: "
                + originalName
                + "\nfull name:"
                + controller.getPictureFullName()
                + "\ncurrent path: "
                + path);
    l1.setMinSize(320, 100);
    l1.setMaxSize(320, 100);
    l1.setAlignment(Pos.CENTER);
    hb.getChildren().addAll(b2, l1, b1, b3, b4);
    hb.setAlignment(Pos.CENTER_LEFT);
    hb.setSpacing(20);
    hb.setPadding(new Insets(0, 0, 0, 20));
    return hb;
  }

  /**
   * generate part of the picture View based on current picture
   *
   * @return the node display of top part of the pictureView
   */
  private static Node pViewLeft() {
    ArrayList<CheckBox> boxes = new ArrayList<>();
    ArrayList<String> tags = new ArrayList<>();
    ScrollPane sp = new ScrollPane();
    sp.setPrefViewportWidth(200);
    sp.setPrefViewportHeight(400);
    Button add = new Button("add selected tags");
    // add temporary tags to the list stored in the controller and  update it, refresh the Main
    // window useing
    // pictureView
    add.setOnAction(
        e -> {
          for (int i = 0; i < boxes.size(); i++) {
            if (boxes.get(i).isSelected()) {
              controller.addTempTag(tags.get(i));
            }
          }
          controller.updateTags();
          mainWindow.setScene(pictureView);
        });
    add.setMinSize(200, 30);
    sp.setMaxSize(200, 420);
    sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
    VBox vb = new VBox();
    vb.setSpacing(10);
    for (String name : Controller.globalTags()) {
      Label l = diyLabel(name);
      l.setMinSize(130, 50);
      l.setMaxSize(130, 50);
      l.setAlignment(Pos.CENTER);
      CheckBox cb = new CheckBox();
      cb.setSelected(false);
      HBox hb = new HBox();
      hb.setAlignment(Pos.CENTER);
      hb.setMinSize(185, 50);
      hb.getChildren().addAll(l, cb);
      vb.getChildren().add(hb);
      tags.add(name);
      boxes.add(cb);
    }
    sp.setContent(vb);

    Label label = diyLabel("existing tags");
    label.setMinSize(200, 50);
    label.setAlignment(Pos.BOTTOM_LEFT);
    VBox left = new VBox();
    left.setMaxSize(208, 400);
    left.setPadding(new Insets(0, 0, 0, 20));
    left.getChildren().addAll(label, sp, add);
    left.setSpacing(10);
    return left;
  }

  /**
   * generate part of the picture View based on current picture
   *
   * @return the node display of top part of the pictureView
   */
  private static Node pViewBot() {
    ScrollPane sp = new ScrollPane();
    sp.setPrefViewportWidth(600);
    sp.setPrefViewportHeight(30);
    sp.setMinSize(600, 30);
    sp.setMaxSize(800, 50);
    sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

    ArrayList<CheckBox> boxes = new ArrayList<>();
    ArrayList<String> tags = new ArrayList<>();

    HBox insp = new HBox();
    insp.setAlignment(Pos.BOTTOM_LEFT);
    for (String name : controller.getTags()) {
      HBox a = new HBox();
      Label lb = new Label("     " + name + " ");
      lb.setMinHeight(30);
      lb.setAlignment(Pos.CENTER_LEFT);
      // Button b = new Button("x");
      // b.setMinHeight(30);
      CheckBox cb = new CheckBox();
      a.getChildren().addAll(lb, cb);
      insp.getChildren().add(a);
      a.setAlignment(Pos.CENTER);
      tags.add(name);
      boxes.add(cb);
    }
    sp.setContent(insp);
    Label msg = new Label("tags belong to this image");
    msg.setAlignment(Pos.BOTTOM_LEFT);
    msg.setMinWidth(280);
    TextField tf = new TextField();
    tf.setPrefColumnCount(10);
    Button go = new Button("add tag");
    VBox bot = new VBox();

    // add the tag and update the Main with picture View
    go.setOnAction(
        e -> {
          if (!tf.getText().isEmpty()) {
            controller.addTag(tf.getText());
            mainWindow.setScene(pictureView);
          }
        });
    HBox hb = new HBox();
    hb.setAlignment(Pos.CENTER);
    hb.setSpacing(5);
    hb.setMinSize(600, 30);

    Button confirm = new Button("remove the selected tags");
    // remove tags from the temporary list and update them, let mainwindow refresh the pictureView
    confirm.setOnAction(
        e -> {
          for (int i = 0; i < boxes.size(); i++) {
            if (boxes.get(i).isSelected()) {
              controller.removeTempTag(tags.get(i));
            }
          }
          controller.updateTags();
          mainWindow.setScene(pictureView);
        });
    hb.getChildren().addAll(msg, tf, go, confirm);

    bot.setPadding(new Insets(0, 0, 20, 20));
    bot.getChildren().addAll(hb, sp);
    return bot;
  }

  /**
   * generate a label that pops up a new window, showing full text in that label when clicked
   *
   * @param text the text to display on the label
   * @return the special label
   */
  private static Label diyLabel(String text) {
    Label l = new Label(text);

    l.setOnMouseClicked(
        e -> {
          Label lCopy = new Label(text);
          taginfo.setScene(new Scene(lCopy));
          taginfo.show();
        });
    return l;
  }

  /**
   * pop up a directory chooser for the user to select the folder to view, calls the controller to
   * move to that folder, and switch to the updated folderView Scene
   */
  private static void selectFolder() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    File selectedDirectory = directoryChooser.showDialog(mainWindow);
    if (selectedDirectory != null) {
      controller.selectFolder(selectedDirectory.getAbsolutePath());
      mainWindow.setScene(folderView);
    }
  }

  /** pop up a history window showing all history of the given picture */
  private static void viewHistory() {

    ArrayList<String[]> list = controller.getHistory();
    historyWindow.setTitle("naming history of " + controller.getPictureName());
    VBox display = new VBox();
    display.setSpacing(10);
    display.setPadding(new Insets(20, 0, 0, 20));
    HBox row1 = new HBox();
    row1.setMinSize(800, 50);
    Label c1title = new Label("oldname");
    Label c2title = new Label("newname");
    Label c3title = new Label("time of changes made");
    Label c4title = new Label("goes back to the old name");
    c1title.setMinSize(300, 50);
    c2title.setMinSize(300, 50);
    c3title.setMinSize(300, 50);
    c4title.setMinSize(200, 50);
    row1.getChildren().addAll(c1title, c2title, c3title, c4title);
    display.getChildren().add(row1);
    for (String[] i : list) {
      HBox hb = new HBox();
      Label oldname = diyLabel(i[0]);
      oldname.setMinSize(300, 30);
      oldname.setMaxSize(300, 30);
      Label newname = diyLabel(i[1]);
      newname.setMinSize(300, 30);
      newname.setMaxSize(300, 30);
      Label time = new Label(i[2]);
      time.setMinSize(300, 30);
      time.setMaxSize(300, 30);
      Button goback = new Button("go back");
      goback.setMaxSize(200, 30);
      // turn the tag status of the picture back to the selected timestamp
      goback.setOnAction(
          e -> {
            ArrayList<String> target = Controller.splitTag(i[0]);
            controller.updateTagsTo(target);
            mainWindow.setScene(pictureView);
            historyWindow.close();
          });
      hb.getChildren().addAll(oldname, newname, time, goback);
      display.getChildren().add(hb);
    }
    ScrollPane sp = new ScrollPane();
    sp.setContent(display);
    sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
    Scene scene = new Scene(sp, 1100, 600);
    historyWindow.setScene(scene);
    historyWindow.show();
  }

  /** update the FolderView based on the list of pictures Controller is working with */
  public static void updateFolderView() {
    ArrayList<Picture> Plist = controller.getPictures();
    ScrollPane sp = new ScrollPane();
    VBox c1 = new VBox();
    VBox c2 = new VBox();
    VBox c3 = new VBox();
    c1.setSpacing(20);
    c2.setSpacing(20);
    c3.setSpacing(20);
    c1.setMinWidth(230);
    c2.setMinWidth(230);
    c3.setMinWidth(230);
    c1.setAlignment(Pos.TOP_CENTER);
    c2.setAlignment(Pos.TOP_CENTER);
    c3.setAlignment(Pos.TOP_CENTER);
    int count = 1;
    for (Picture picture : Plist) {
      FileInputStream i1file = null;
      try {
        i1file = new FileInputStream(picture.getPath());
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      Image image = new Image(i1file);
      ImageView IV = new ImageView(image); // should be an image instance
      IV.setPreserveRatio(true);
      IV.setFitHeight(200);
      IV.setFitWidth(230);
      GridPane gp = new GridPane();
      gp.getChildren().add(IV);
      gp.setAlignment(Pos.CENTER);
      gp.setMaxSize(230, 200);
      gp.setMinSize(230, 200);
      IV.setOnMouseClicked(
          e -> {
            controller.selectPicture(picture);
            mainWindow.setScene(pictureView);
          });
      if (count == 1) {
        c1.getChildren().add(gp);
        count++;
      } else if (count == 2) {
        c2.getChildren().add(gp);
        count++;
      } else {
        c3.getChildren().add(gp);
        count = 1;
      }
    }
    HBox insp = new HBox();
    insp.getChildren().addAll(c1, c2, c3);
    insp.setSpacing(20);
    insp.setAlignment(Pos.CENTER);
    sp.setContent(insp);
    sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
    GridPane gridPane = new GridPane();
    gridPane.getChildren().addAll(sp);
    gridPane.setAlignment(Pos.CENTER);
    BorderPane bp = new BorderPane(gridPane);
    sp.setPrefViewportWidth(750);
    sp.setPrefViewportHeight(450);
    bp.setMinSize(850, 650);
    bp.setMaxSize(850, 650);
    bp.setPadding(new Insets(10, 10, 10, 10));
    Button b1 = new Button("Browse\nFolder");
    b1.setMinSize(100, 80);
    // call the controller to move to another folder  and refresh the mainWindow using theupdated
    // folderview
    b1.setOnAction(
        e -> {
          selectFolder();
          mainWindow.setScene(folderView);
        });
    Label l1 = new Label("Click the image to open and analyze");
    l1.setAlignment(Pos.CENTER);
    l1.setMinWidth(560);
    HBox top = new HBox();

    Button b2 = new Button("Manage\nTags");
    b2.setMinSize(100, 80);
    // set up the Tag view window and display it allowing user to manage all tags in the selected
    // directory
    b2.setOnAction(
        e -> {
          updateTags();
          globalTags.show();
        });
    top.getChildren().addAll(l1, b1, b2);
    top.setSpacing(20);
    top.setAlignment(Pos.CENTER);
    bp.setTop(top);
    folderView = new Scene(bp, 850, 650);
  }

  /** set/refresh the window to manage all tags based on globaltags provided by controller */
  public static void updateTags() {
    ScrollPane sp = new ScrollPane();
    sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
    VBox vb = new VBox();
    for (String tag : Controller.globalTags()) {
      HBox row = new HBox();
      Label tagname = diyLabel(tag);
      tagname.setMinSize(400, 50);
      tagname.setMaxSize(400, 50);
      tagname.setAlignment(Pos.CENTER);
      Button remove = new Button("remove");
      // call the controller to remove the tag
      remove.setOnAction(
          e -> {
            controller.removeTagFromAll(tag);
          });
      TextField tf = new TextField();
      tf.setPrefColumnCount(20);
      Button rename = new Button("rename");
      // call the controller to rename the tags
      rename.setOnAction(
          e -> {
            controller.renameTag(tag, tf.getText());
            mainWindow.setScene(folderView);
          });
      row.getChildren().addAll(tagname, remove, tf, rename);
      row.setAlignment(Pos.CENTER);
      row.setSpacing(25);
      vb.getChildren().add(row);
    }
    sp.setContent(vb);
    globalTags.setScene(new Scene(sp, 1000, 600));
  }
}
