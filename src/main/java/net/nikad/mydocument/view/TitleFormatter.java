package net.nikad.mydocument.view;

import net.nikad.mydocument.model.Document;

public final class TitleFormatter {

    private TitleFormatter() {}

    public static String windowTitle(Document document) {
        return tabTitle(document) + " — MyDocument";
    }

    public static String tabTitle(Document document) {
        return document.getTitle() + (document.isDirty() ? " *" : "");
    }
}
