# Chronoscope #

Chronoscope is a fast, scalable, charting toolkit designed to work well in most web browsers.
It is interactive and can be used in Gwt applications, as an embedded widget in any html page using normal javascript, a Google Gadget, or with the Google visualization API.

## Philosophy ##

  * Scalable representations. For example, almost anything works if you're implementing a plot for a dataset with 5 items.  The aim of Chronoscope is to support hundred, thousands, and hundreds of thousands of items.

  * Platform independence.  Although compiled and most often used via the Javascript API, the core code happens to be written in Java and then compiled to Javascript using Google Web Toolkit.  That enabled server-side Java2D or mobile Android targets as well as Javascript clients.

  * Performance. It is one of the most important points considered during the development. Chronoscope is able to draw thousand of points and refresh the chart at 20-30 fps.

  * Stylish.  Customizable look and feel through CSS-inspired Graph Stylesheets.

## Documentation Index ##
  * [Javascript API ](JavaScriptAPI.md)
    * [Javascript API reference](http://gwt-chronoscope.googlecode.com/svn/apidocs/jsdoc.html)
  * [GWT API](GwtAPI.md)
    * [Javadoc reference](http://gwt-chronoscope.googlecode.com/svn/apidocs/index.html)
  * [Graph Stylesheets](Gss.md)
    * [GSS API reference](http://gwt-chronoscope.googlecode.com/svn/apidocs/gssdoc.html)
  * Data series
    * [Datasets](Datasets.md)
    * [Microformats](Microformats.md)
  * [Widget overview](Widget.md)
  * [Gadget overview](Gadget.md)
  * [Google Visualization API](GViz.md)
  * [Integration with 3 party libs and tools](Integration.md)
