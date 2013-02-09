Drill-Xerces
============

Fun implementation of [Apaches free Xerces XML Parser](http://xerces.apache.org/xerces-j/)

Drill-Xerces tends to get angry if users don't follow his validation hints...


----

Be Careful!
=====
**If you make Drill-Xerces too angry, he'll take over your document...**

**Damage to your document is predicted if you don't fix validation errors in time!**

**Use at your own risk!**

----


Usage
=====

Commandline:

`java -jar DrillXerces.jar yourDocument.xml`

or integrate in [oXygen](http://www.oxygenxml.com) as a "Custom Validation Engine":

* *Options* => *Preferences* => *Custom Validation Engines* => *New*
* *Executable path:* `java -jar "/path/to/DrillXerces.jar"`
* *Associated editors:* XML-Editor
* *Command line arguments:*
 * *XSD:* `${cf}`
