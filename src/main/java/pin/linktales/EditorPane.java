package pin.linktales;

import org.w3c.dom.Document;

import javafx.concurrent.Worker.State;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class EditorPane extends StackPane {

  private final HelmMain master;

  private final HTMLEditor edit;
  private final WebView view;
  private final WebEngine engine;
  private final JavaBridge bridge;

  private Document document;

  public EditorPane(HelmMain master) {
    super();
    this.master = master;
    this.edit = new HTMLEditor();
    getChildren().add(edit);
    this.view = (WebView) edit.lookup("WebView");
    this.engine = view.getEngine();
    engine.getLoadWorker().stateProperty()
      .addListener((observable, oldState, newState) -> {
        if (newState == State.SUCCEEDED) {
          document = engine.getDocument();
          initHandlers();
          initEdit();
        }
      });
    this.bridge = new JavaBridge();
  }

  private boolean first = true;

  private void initEdit() {
    if (!first) {
      return;
    }
    first = false;
    javafx.scene.Node node = edit.lookup(".top-toolbar");
    if (node instanceof ToolBar) {
      ToolBar bar = (ToolBar) node;
      Button btLink = new Button();
      btLink.setGraphic(new SVGWrap(SVGTypes.Link));
      btLink.setOnDragOver((event) -> {
        if (event.getGestureSource() != btLink
            && event.getDragboard().hasString()) {
          event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
        event.consume();
      });
      btLink.setOnDragDropped((event) -> {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
          String path = db.getString();
          execute("talesStartLinkChange()");
          bridge.setLinkSource(path);
          new HelmLink(this).show();
          success = true;
        }
        event.setDropCompleted(success);
        event.consume();
      });
      btLink.setOnAction((event) -> {
        execute("talesStartLinkChange()");
        new HelmLink(this).show();
      });
      bar.getItems().add(0, btLink);
      bar.getItems().add(1, new Separator());
      Button btTitle = new Button();
      btTitle.setGraphic(new SVGWrap(SVGTypes.Title));
      btTitle.setOnAction(e -> {
        new HelmTitle(this).show();
      });
      bar.getItems().add(new Separator());
      bar.getItems().add(btTitle);
    }
  }

  private void initHandlers() {
    JSObject window = (JSObject) execute("window");
    window.setMember("talesBridge", bridge);
    execute(master.getScripts());
    execute("talesBindLinks()");
    bridge.setChanges(0);
  }

  public HelmMain getMaster() {
    return this.master;
  }

  public HTMLEditor getEdit() {
    return this.edit;
  }

  public WebView getView() {
    return this.view;
  }

  public WebEngine getEngine() {
    return this.engine;
  }

  public JavaBridge getBridge() {
    return this.bridge;
  }

  public Document getDocument() {
    return this.document;
  }

  public Object execute(String script) {
    return engine.executeScript(script);
  }

  public void setSource(String source) {
    edit.setHtmlText(source);
  }

  public String getSource() {
    return edit.getHtmlText().replace(" contenteditable=\"true\"", "");
  }

  public boolean hasChanges() {
    return bridge.changes > 0;
  }

  public void resetChanges() {
    bridge.changes = 0;
    execute("talesResetChanges()");
  }

  public class JavaBridge {

    private int changes = 0;
    private String selectionText = "";
    private String linkSource = "";

    public int getChanges() {
      return changes;
    }

    public void setChanges(int changes) {
      this.changes = changes;
    }

    public void selectionChange() {
    }

    public void changeSelectionTree(String tree) {
      master.changeSelectionTree(tree);
    }

    public String getSelectionText() {
      return selectionText;
    }

    public void setSelectionText(String selectionText) {
      this.selectionText = selectionText;
    }

    public String getLinkSource() {
      return linkSource;
    }

    public void setLinkSource(String linkSource) {
      this.linkSource = linkSource;
    }

    public void handleURL(String href) {
      master.handleURL(href);
    }

    public void log(String text) {
      System.out.println(text);
    }

  }

}
