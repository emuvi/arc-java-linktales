package pin.linktales;

import java.io.File;
import java.net.URI;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class HelmMain extends Application {

  private final String scripts;

  private final Button btNew;
  private final Button btOpen;
  private final Button btSave;
  private final Button btSaveAs;
  private final ToolBar tbFirst;
  private final Button btContents;
  private final Button btCatalog;
  private final Button btCompress;
  private final Button btInfo;
  private final ToolBar tbSecond;
  private final TreeView<Item> tvFiles;
  private final TextField tfName;
  private final VBox vbLeft;
  private final EditorPane editor;
  private final TextField tfElement;
  private final VBox vbRight;
  private final SplitPane spBody;
  private final Scene scBody;

  private Stage stage;

  private File projectDir = null;
  private File opennedFile = null;

  public HelmMain() {
    scripts = Utils.text("Scripts.js");
    btNew = new Button();
    btOpen = new Button();
    btSave = new Button();
    btSaveAs = new Button();
    btContents = new Button();
    btCatalog = new Button();
    btCompress = new Button();
    btInfo = new Button();
    tbFirst = new ToolBar(btNew, btOpen, btSave, btSaveAs);
    tbSecond = new ToolBar(btContents, btCatalog, btCompress, btInfo);
    tvFiles = new TreeView<>();
    tfName = new TextField();
    vbLeft = new VBox(tbFirst, tbSecond, tvFiles, tfName);
    editor = new EditorPane(this);
    tfElement = new TextField();
    vbRight = new VBox(editor, tfElement);
    spBody = new SplitPane(vbLeft, vbRight);
    this.scBody = new Scene(spBody, 800, 600);
    initControls();
  }

  private void initControls() {
    btNew.setGraphic(new SVGWrap(SVGTypes.New).fit(16, 16));
    btNew.setTooltip(new Tooltip("New"));
    btNew.setOnAction((event) -> {
      doNew();
    });
    btOpen.setGraphic(new SVGWrap(SVGTypes.Open).fit(16, 16));
    btOpen.setTooltip(new Tooltip("Open"));
    btOpen.setOnAction((event) -> {
      doOpen();
    });
    btSave.setGraphic(new SVGWrap(SVGTypes.Save).fit(16, 16));
    btSave.setTooltip(new Tooltip("Save"));
    btSave.setOnAction((event) -> {
      doSave();
    });
    btSaveAs.setGraphic(new SVGWrap(SVGTypes.SaveAs).fit(16, 16));
    btSaveAs.setTooltip(new Tooltip("SaveAs"));
    btSaveAs.setOnAction((event) -> {
      doSaveAs();
    });

    btContents.setGraphic(new SVGWrap(SVGTypes.Contents).fit(16, 16));
    btContents.setTooltip(new Tooltip("Contents"));
    btContents.setOnAction((event) -> {
      doContents();
    });
    btCatalog.setGraphic(new SVGWrap(SVGTypes.Catalog).fit(16, 16));
    btCatalog.setTooltip(new Tooltip("Catalog"));
    btCatalog.setOnAction((event) -> {
      doCatalog();
    });
    btCompress.setGraphic(new SVGWrap(SVGTypes.Compress).fit(16, 16));
    btCompress.setTooltip(new Tooltip("Compress"));
    btCompress.setOnAction((event) -> {
      doCompress();
    });
    btInfo.setGraphic(new SVGWrap(SVGTypes.Info).fit(16, 16));
    btInfo.setTooltip(new Tooltip("Info"));
    btInfo.setOnAction((event) -> {
      doInfo();
    });
    tvFiles.setOnMouseClicked((event) -> {
      if (event.getClickCount() >= 2) {
        openSelected();
      }
    });
    tvFiles.setOnDragDetected((event) -> {
      TreeItem<Item> selected = tvFiles.getSelectionModel().getSelectedItem();
      if (selected != null) {
        final String path = selected.getValue().getPath();
        Dragboard db = tvFiles.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString(makeRelative(path));
        db.setContent(content);
        event.consume();
      }
    });
    VBox.setVgrow(tvFiles, Priority.ALWAYS);
    tfName.setEditable(false);
    tfName.setPadding(new Insets(1, 1, 1, 1));
    tfName.setBackground(vbLeft.getBackground());
    VBox.setMargin(tfName, new Insets(1, 1, 1, 1));
    VBox.setVgrow(editor, Priority.ALWAYS);
    tfElement.setEditable(false);
    tfElement.setPadding(new Insets(1, 1, 1, 1));
    tfElement.setBackground(vbRight.getBackground());
    VBox.setMargin(tfElement, new Insets(1, 1, 1, 1));
    VBox.setVgrow(spBody, Priority.ALWAYS);
    spBody.setDividerPosition(0, 0.22);
    scBody.setOnKeyPressed(event -> {
      if (event.isControlDown()) {
        if (event.getCode() == KeyCode.N) {
          event.consume();
          doNew();
        } else if (event.getCode() == KeyCode.O) {
          event.consume();
          doOpen();
        } else if (event.getCode() == KeyCode.S) {
          event.consume();
          doSave();
        }
      }
    });
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    this.stage = primaryStage;
    stage.setScene(scBody);
    stage.setTitle("LinkTales");
    stage.getIcons().add(Utils.image("LinkTales.png"));
    stage.setOnCloseRequest((event) -> {
      if (editor.hasChanges()) {
        if (!Utils.showConfirm(
              "The changes in editor will be lost, do you want to continue?")) {
          event.consume();
          return;
              }
      }
      Platform.exit();
      System.exit(0);
    });
    stage.show();
    try {
      List<String> params = getParameters().getRaw();
      if (!params.isEmpty()) {
        openFile(new File(Utils.decodeURL(params.get(0))));
      }
    } catch (Exception e) {
      Utils.showError(e);
    }
  }

  public String getScripts() {
    return scripts;
  }

  private boolean isInside(File file) {
    if (projectDir == null) {
      return false;
    } else {
      final String filePath = file.getAbsolutePath();
      final String projectPath = projectDir.getAbsolutePath() + File.pathSeparator;
      return filePath.startsWith(projectPath);
    }
  }

  public void doNew() {
    if (editor.hasChanges()) {
      if (!Utils.showConfirm(
            "The changes in editor will be lost, do you want to continue?")) {
        return;
            }
    }
    FileChooser chooser = new FileChooser();
    chooser.getExtensionFilters().add(webExtensions);
    if (projectDir != null) {
      chooser.setInitialDirectory(projectDir);
    }
    File selected = chooser.showSaveDialog(stage.getOwner());
    if (selected != null) {
      final String document = Utils.text("New.html");
      Utils.save(document, selected);
      editor.setSource(document);
      opennedFile = selected;
      updateOpenned();
    }
  }

  private final FileChooser.ExtensionFilter webExtensions =
    new FileChooser.ExtensionFilter("Web Pages (*.html, *.htm)", "*.html",
        "*.htm");

  public void doOpen() {
    if (editor.hasChanges()) {
      if (!Utils.showConfirm(
            "The changes in editor will be lost, do you want to continue?")) {
        return;
            }
    }
    FileChooser chooser = new FileChooser();
    chooser.getExtensionFilters().add(webExtensions);
    if (projectDir != null) {
      chooser.setInitialDirectory(projectDir);
    }
    File selected = chooser.showOpenDialog(stage.getOwner());
    if (selected != null) {
      openFile(selected);
    }
  }

  public void doSave() {
    if (opennedFile != null) {
      Utils.save(editor.getSource(), opennedFile);
      editor.resetChanges();
      Utils.showPopupMessage("Lore Saved", stage);
    } else {
      doSaveAs();
    }
  }

  public void doSaveAs() {
    FileChooser chooser = new FileChooser();
    chooser.getExtensionFilters().add(webExtensions);
    if (projectDir != null) {
      chooser.setInitialDirectory(projectDir);
    }
    File selected = chooser.showSaveDialog(stage.getOwner());
    if (selected != null) {
      Utils.save(editor.getSource(), selected);
      editor.resetChanges();
      opennedFile = selected;
      updateOpenned();
    }
  }

  public void doContents() {
    Utils.showInfo(
        "Function not yet implemented to make automagically a table of contents.");
  }

  public void doCatalog() {
    Utils.showInfo(
        "Function not yet implemented to configure the project meta data information.");
  }

  public void doCompress() {
    Utils.showInfo(
        "Function not yet implemented to compress the project as an EPub file.");
  }

  public void doInfo() {
    try {
      java.awt.Desktop.getDesktop()
        .browse(new URI("http://www.pointel.com.br/Apps/Tales"));
    } catch (Exception e) {
      Utils.showError(e);
    }
  }

  public void handleURL(String href) {
    try {
      if (href.startsWith("http://") || href.startsWith("https://")) {
        java.awt.Desktop.getDesktop().browse(new URI(href));
      } else {
        if (editor.hasChanges()) {
          if (!Utils.showConfirm(
                "The changes in editor will be lost, do you want to continue?")) {
            return;
                }
        }
        if (href.startsWith("file://")) {
          openFile(new File(new URI(href)));
        } else {
          File fileLink = opennedFile.getParentFile();
          if (href.startsWith("/")) {
            if (projectDir != null) {
              fileLink = projectDir;
            }
            href = href.substring(1);
          }
          if (href.startsWith("./")) {
            href = href.substring(2);
          }
          while (href.startsWith("../")) {
            fileLink = fileLink.getParentFile();
            href = href.substring(3);
          }
          for (String part : href.split("\\/")) {
            if (!part.isEmpty()) {
              fileLink = new File(fileLink, part);
            }
          }
          openFile(fileLink);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void openSelected() {
    final TreeItem<Item> selected = tvFiles.getSelectionModel().getSelectedItem();
    if (selected != null) {
      if (editor.hasChanges()) {
        if (!Utils.showConfirm(
              "The changes in editor will be lost, do you want to continue?")) {
          return;
              }
      }
      final File file = selected.getValue().getFile();
      if (file.isFile()) {
        openFile(file);
      }
    }
  }

  private void openFile(File file) {
    final String source = Utils.text(file);
    editor.setSource(source);
    opennedFile = file;
    updateOpenned();
  }

  private void updateOpenned() {
    if (!isInside(opennedFile)) {
      projectDir = opennedFile.getParentFile();
    }
    updateOpennedPath();
    updateProject();
  }

  private void updateOpennedPath() {
    String opennedPath = opennedFile.getAbsolutePath();
    if (projectDir != null) {
      String projectPath = projectDir.getAbsolutePath();
      if (opennedPath.startsWith(projectPath)) {
        opennedPath = opennedPath.substring(projectPath.length());
        opennedPath = opennedPath.replace("\\", "/");
      }
    }
    tfName.setText(opennedPath);
  }

  private TreeItem<Item> itemToSelect = null;

  private void loadFiles(File fromFolder, TreeItem<Item> inNode, String inPath) {
    if (fromFolder.isDirectory()) {
      for (File inside : fromFolder.listFiles()) {
        if (inside.isDirectory()) {
          final Item item = new Item(inside, inPath + inside.getName());
          final TreeItem<Item> node = new TreeItem<>(item);
          inNode.getChildren().add(node);
          loadFiles(inside, node, inPath + inside.getName() + "/");
        }
      }
      for (File inside : fromFolder.listFiles()) {
        if (inside.isFile()) {
          final Item item = new Item(inside, inPath + inside.getName());
          final TreeItem<Item> node = new TreeItem<>(item);
          inNode.getChildren().add(node);
          if (opennedFile != null && opennedFile.equals(inside)) {
            itemToSelect = node;
          }
        }
      }
    }

  }

  private void updateProject() {
    if (tvFiles.getRoot() != null) {
      tvFiles.getRoot().getChildren().clear();
    }
    final TreeItem<Item> root = new TreeItem<>(new Item(projectDir, "/"));
    tvFiles.setRoot(root);
    itemToSelect = null;
    loadFiles(projectDir, root, "/");
    root.setExpanded(true);
    if (itemToSelect != null) {
      tvFiles.getSelectionModel().select(itemToSelect);
    }
  }

  private String makeRelative(String path) {
    String openned = tfName.getText();
    String[] pathParts = path.split("\\/");
    String[] openParts = openned.split("\\/");
    int firstChange = -1;
    for (int i = 0; i < Math.min(pathParts.length, openParts.length); i++) {
      if (!pathParts[i].equals(openParts[i])) {
        firstChange = i;
        break;
      }
    }
    if (firstChange == -1) {
      return "." + path;
    }
    String result = "./";
    int returns = (openParts.length - 1) - firstChange;
    if (returns > 0) {
      result = "../";
      for (int i = 0; i < returns - 1; i++) {
        result += result;
      }
    }
    for (int i = firstChange; i < pathParts.length - 1; i++) {
      result = result + pathParts[i] + "/";
    }
    result = result + pathParts[pathParts.length - 1];
    return result;
  }

  public void changeSelectionTree(String tree) {
    tfElement.setText(tree);
  }

  private class Item {

    private final File file;
    private final String path;

    public Item(File file, String path) {
      this.file = file;
      this.path = path;
    }

    public File getFile() {
      return file;
    }

    public String getPath() {
      return path;
    }

    @Override
    public String toString() {
      return file.getName();
    }

  }

}
