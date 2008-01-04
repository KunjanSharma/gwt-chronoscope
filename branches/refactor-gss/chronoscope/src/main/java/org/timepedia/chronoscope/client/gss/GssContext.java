package org.timepedia.chronoscope.client.gss;

import org.timepedia.chronoscope.client.canvas.View;

/**
 * A GssContext is responsible for mapping GssElement/pseudoElt pairs into GssProperties objects.
 * In the browser, this is done by {@link org.timepedia.chronoscope.client.browser.CssGssContext} for example.
 */
public class GssContext {

    protected View view;

    public View getView() {
        return view;
    }

    public GssContext(View view) {
        this.view = view;
    }

    public GssContext() {
    }

    public GssProperties getProperties(GssElement gssElem, String pseudoElt) {
        return new MockGssProperties();
    }

    public void setView(View view) {
        this.view = view;
    }
}
