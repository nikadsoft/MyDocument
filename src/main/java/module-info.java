module net.nikad.mydocument {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.commonmark;
    requires org.commonmark.ext.gfm.tables;
    requires org.commonmark.ext.gfm.strikethrough;
    requires org.commonmark.ext.autolink;
    requires org.commonmark.ext.heading.anchor;

    opens net.nikad.mydocument            to javafx.fxml;
    opens net.nikad.mydocument.controller to javafx.fxml;

    exports net.nikad.mydocument;
}
