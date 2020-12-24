package pin.linktales;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class SVGWrap extends StackPane {

  private final SVGPath path;

  public SVGWrap() {
    this(null, false);
  }

  public SVGWrap(SVGTypes type) {
    this(type, true);
  }

  public SVGWrap(SVGTypes type, boolean small) {
    super();
    path = new SVGPath();
    getChildren().add(path);
    if (type != null) {
      path.setContent(type.getData());
    }
    path.setFill(Color.rgb(0, 0, 0, 0.5));
    if (small) {
      fit(16, 16);
    }
  }

  public SVGWrap content(String content) {
    path.setContent(content);
    return this;
  }

  public SVGWrap fill(Color fill) {
    path.setFill(fill);
    return this;
  }

  public SVGWrap stroke(Color stroke) {
    path.setStroke(stroke);
    return this;
  }

  public SVGWrap fit(double width, double height) {
    return fit(width, height, true);
  }

  public SVGWrap fit(double width, double height, boolean keepRatio) {
    double originalWidth = path.prefWidth(-1);
    double originalHeight = path.prefHeight(originalWidth);
    double scaleX = width / originalWidth;
    double scaleY = height / originalHeight;
    if (keepRatio) {
      scaleX = Math.min(scaleX, scaleY);
      scaleY = scaleX;
    }
    path.setScaleX(scaleX);
    path.setScaleY(scaleY);
    setMinSize(width, height);
    setPrefSize(width, height);
    setMaxSize(width, height);
    return this;
  }

}
