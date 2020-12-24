all : mvn pkg

mvn :
	mvn clean install

dps :
	jdeps --module-path build/libLinkTales/ --list-deps build/LinkTales.jar build/libLinkTales/* | grep -E "java\.|javax\.|jdk\."

dpsAll :
	jdeps --module-path build/libLinkTales/ --list-deps build/LinkTales.jar build/libLinkTales/*

clnJre:
	rmdir /s /q "jre"

bldJre :
	jlink --output jre --add-modules java.base,java.desktop,java.net.http,java.xml,jdk.jfr,jdk.jsobject,jdk.unsupported,jdk.xml.dom --strip-debug --strip-native-commands --compress 2 --no-header-files --no-man-pages

pkg :
	jpackage --runtime-image ./jre --input ./build --dest ./dist --main-jar LinkTales.jar --name "LinkTales" --app-version 0.1.0 --icon assets/pin/linktales/LinkTales.ico --win-dir-chooser --win-menu --win-menu-group "LinkTales"