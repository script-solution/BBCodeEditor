BBCodeEditor
============

The BBCodeEditor is a Java application and applet that allows to edit BBCode
in a WYSIWYG way. It has been written for [Boardsolution](/ScSo/Boardsolution),
so that the BBCode is the one that Boardsolution supports, but most things
should be suited for other BBCode engines as well. It consists of the control
- the heart of the project and the only really interesting part of it ;) - and
some stuff around it like buttons, comboboxes and so on. Thus, the control is
usable for other purposes as well.

Getting started
---------------

To build it, simply execute `ant` in the root directory. If you want to test
the standalone-application, you can use `ant run`. As soon as the `sign`
target has been executed, e.g. through `run`, you can open the example.html
in your favorite browser to try the applet.

Requirements
------------

It requires at least JRE 1.4.2.

