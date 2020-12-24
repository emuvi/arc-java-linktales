package pin;

import javafx.application.Application;
import javafx.application.Platform;
import pin.linktales.HelmMain;

public class LinkTales {

  public static void main(String[] args) {
    Platform.setImplicitExit(false);
    Application.launch(HelmMain.class, args);
  }

}
