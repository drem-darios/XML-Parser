# XML-Parser
Parses XML and allows you to search for tags

## Problem Statement

Design a simple XML parser that does not require DOM or SAX parser libraries. Feel free to use java regular expressions. Use the most basic set of utilities that you need to get the job done. Implement just one method that will return the text that comes between the start and end of any given tag. Use the following signature for your method:

public static Vector getXMLTagValue(String xmlFileString, String tagName);

The above method should accept an XML file or a subset of an XML file as a String and the name of the tag to extract. You may assume that the XML file will not contain empty tags or attributes. All values of the tags in the XML file that match the specified tag name should be extracted and filled into the return Vector as String values.

Therefore, to extract the list of tags in the XML file (see table 1 for sample XML file), you could write the following code:
```
String xmlFile = getXMLFile(“x.xml”);
Vector v = getXMLTagValue(xmlFile, “STUDENT”);
Vector ageV = getXMLTagValue(v.elementAt(0), “AGE”);
System.out.println(“Age is : “ + ageV.elementAt(0));
```
Sample XML file:
```
<?xml version="1.0"?>
<!DOCTYPE STUDENTS [
<!ELEMENT STUDENTS (STUDENT*)>
<!ELEMENT STUDENT (NAME, AGE, CLASS)>
<!ELEMENT NAME (#PCDATA)>
<!ELEMENT AGE (#PCDATA)>
<!ELEMENT CLASS (#PCDATA)>
]>
<STUDENTS>
<STUDENT>
<NAME>HillyBilly</NAME>
<AGE>19</AGE>
<CLASS>10</CLASS>
</STUDENT>
<STUDENT>
<NAME>Captain Kirk</NAME>
<AGE>20</AGE>
<CLASS>10</CLASS>
</STUDENT>
</STUDENTS>
```
