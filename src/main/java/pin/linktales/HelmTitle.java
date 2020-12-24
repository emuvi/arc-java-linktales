package pin.linktales;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HelmTitle extends Stage {

  private final EditorPane editor;

  private final TextField tfTitle;
  private final HBox hbTitle;
  private final Button btOk;
  private final Button btCancel;
  private final HBox hbButtons;
  private final VBox vbWindow;

  public HelmTitle(EditorPane editor) {
    this.editor = editor;
    this.tfTitle = new TextField(editor.getEngine().titleProperty().get());
    this.hbTitle = new HBox(tfTitle);
    this.btOk = new Button("Ok");
    this.btCancel = new Button("Cancel");
    this.hbButtons = new HBox(btOk, btCancel);
    this.vbWindow = new VBox(hbTitle, hbButtons);
    initControls();
    setScene(new Scene(vbWindow));
    initStyle(StageStyle.UTILITY);
    setAlwaysOnTop(true);
    setTitle("Title");
  }

  private void initControls() {
    tfTitle.setPrefColumnCount(21);
    hbTitle.setSpacing(4);
    hbTitle.setPadding(new Insets(4));
    hbButtons.setSpacing(4);
    hbButtons.setPadding(new Insets(4));
    vbWindow.setPadding(new Insets(4));
    btOk.setDefaultButton(true);
    btOk.setOnAction((event) -> {
      editor.execute("document.title = '" + tfTitle.getText() + "'");
      close();
      editor.getEdit().requestFocus();
    });
    btCancel.setCancelButton(true);
    btCancel.setOnAction((event) -> {
      close();
    });
  }


}
