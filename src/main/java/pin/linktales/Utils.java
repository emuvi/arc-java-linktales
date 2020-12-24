package pin.linktales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Utils {

  // String Utils

  public static String firstUpper(String string) {
    StringBuilder result = new StringBuilder();
    if (string.length() > 0) {
      result.append(string.substring(0, 1).toUpperCase());
    }
    if (string.length() > 1) {
      result.append(string.substring(1).toLowerCase());
    }
    return result.toString();
  }

  public static String decodeURL(String value) {
    try {
      return URLDecoder.decode(value, StandardCharsets.UTF_8);
    } catch (Exception e) {
      return value;
    }
  }

  // File Utils

  public static String text(String name) {
    try (BufferedReader br = new BufferedReader(
          new InputStreamReader(Utils.class.getResourceAsStream(name)))) {
      return br.lines().collect(Collectors.joining(System.lineSeparator()));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String text(File file) {
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      return br.lines().collect(Collectors.joining(System.lineSeparator()));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Image image(String name) {
    try {
      return new Image(Utils.class.getResourceAsStream(name));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void save(String text, File file) {
    try (FileWriter fw = new FileWriter(file)) {
      fw.write(text);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // Interface Utils

  public static VBox getCol(Node... fromNodes) {
    final VBox result = new VBox(fromNodes);
    result.setAlignment(Pos.TOP_LEFT);
    result.setSpacing(8);
    result.setPadding(new Insets(8, 8, 8, 8));
    return result;
  }

  public static HBox getRow(Node... fromNodes) {
    final HBox result = new HBox(fromNodes);
    result.setAlignment(Pos.BASELINE_LEFT);
    result.setSpacing(8);
    return result;
  }

  public static void setMinAsMaxPref(Control... ofControls) {
    double max = 0;
    for (Control control : ofControls) {
      max = Math.max(max, control.getWidth());
    }
    for (Control control : ofControls) {
      control.setMinWidth(max);
    }
  }

  private static final Background popupBackground = new Background(
      new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, new Insets(0)));
  private static final Border popupBorder = new Border(new BorderStroke(Color.DARKGRAY,
        BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN));

  public static Popup createPopup(final String message) {
    final Popup popup = new Popup();
    popup.setAutoFix(true);
    popup.setAutoHide(true);
    popup.setHideOnEscape(true);
    Label label = new Label(message);
    label.setOnMouseReleased((event) -> {
      popup.hide();
    });
    label.setBackground(popupBackground);
    label.setBorder(popupBorder);
    label.setPadding(new Insets(7));
    popup.getContent().add(label);
    return popup;
  }

  public static void showPopupMessage(final String message, final Stage stage) {
    final Popup popup = createPopup(message);
    popup.setOnShown((event) -> {
      popup.setX(stage.getX() + stage.getWidth() - (popup.getWidth() + 30));
      popup.setY(stage.getY() + stage.getHeight() - (popup.getHeight() + 30));
    });
    popup.show(stage);
    final PauseTransition pause = new PauseTransition(Duration.seconds(3));
    pause.setOnFinished(e -> popup.hide());
    pause.play();
  }

  public static boolean showInfo(final String message) {
    return showAlert(message, AlertType.INFORMATION);
  }

  public static boolean showError(final Throwable error) {
    return showError(error.getMessage());
  }

  public static boolean showError(final String message) {
    return showAlert(message, AlertType.ERROR);
  }

  public static boolean showConfirm(final String message) {
    return showAlert(message, AlertType.CONFIRMATION);
  }

  public static boolean showAlert(final String message, final AlertType type) {
    Alert alert = new Alert(type);
    alert.setTitle("Alert - " + firstUpper(type.toString()));
    alert.setHeaderText(message);
    final AlertResult result = new AlertResult();
    alert.showAndWait().ifPresent(rs -> {
      if (rs == ButtonType.OK) {
        result.isOk = true;
      }
    });
    return result.isOk;
  }

  private static class AlertResult {

    boolean isOk = false;

  }

}
