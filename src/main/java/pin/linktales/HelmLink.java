package pin.linktales;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HelmLink extends Stage {

  private final EditorPane editor;

  private final Label lbText;
  private final TextField tfText;
  private final Label lbLink;
  private final TextField tfLink;
  private final GridPane gpEdits;
  private final Button btOk;
  private final Button btCancel;
  private final HBox hbButtons;
  private final VBox vbWindow;

  public HelmLink(EditorPane editor) {
    this.editor = editor;
    this.lbText = new Label("Text:");
    this.tfText = new TextField(editor.getBridge().getSelectionText());
    this.lbLink = new Label("Link:");
    this.tfLink = new TextField(editor.getBridge().getLinkSource());
    this.gpEdits = new GridPane();
    this.btOk = new Button("Ok");
    this.btCancel = new Button("Cancel");
    this.hbButtons = new HBox(btOk, btCancel);
    this.vbWindow = new VBox(gpEdits, hbButtons);
    initControls();
    setScene(new Scene(vbWindow));
    initStyle(StageStyle.UTILITY);
    setAlwaysOnTop(true);
    setTitle("Link");
  }

  private void initControls() {
    tfText.setPrefColumnCount(21);
    tfLink.setPrefColumnCount(21);
    gpEdits.add(lbText, 0, 0);
    gpEdits.add(tfText, 1, 0);
    gpEdits.add(lbLink, 0, 1);
    gpEdits.add(tfLink, 1, 1);
    gpEdits.setPadding(new Insets(4));
    gpEdits.setHgap(4);
    gpEdits.setVgap(4);
    hbButtons.setSpacing(4);
    hbButtons.setPadding(new Insets(4));
    vbWindow.setPadding(new Insets(4));
    btOk.setDefaultButton(true);
    btOk.setOnAction((event) -> {
      editor.execute("talesUpdateLink('" + tfText.getText() + "', '"
          + tfLink.getText() + "')");
      close();
      editor.getEdit().requestFocus();
    });
    btCancel.setCancelButton(true);
    btCancel.setOnAction((event) -> {
      close();
    });
  }

}
