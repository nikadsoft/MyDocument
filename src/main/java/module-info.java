module net.nikad.mydocument {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;

    requires org.commonmark;
    requires org.commonmark.ext.gfm.tables;
    requires org.commonmark.ext.gfm.strikethrough;
    requires org.commonmark.ext.autolink;
    requires org.commonmark.ext.heading.anchor;

    // javafx.graphics instantiates MyDocumentApp via reflection (Application.launch)
    opens net.nikad.mydocument            to javafx.fxml, javafx.graphics;
    opens net.nikad.mydocument.controller to javafx.fxml;

    exports net.nikad.mydocument;
}
