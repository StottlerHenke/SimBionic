del collections.xml
echo ^<collections^> > collections.xml

for %%f in (input\*.xsd) do java -jar saxon9he.jar -xsl:findCollectionClasses.xsl -s:%%f >> collections.xml

echo ^</collections^> >> collections.xml

java -jar saxon9he.jar -xsl:generateCollectionFunctions.xsl -s:collections.xml

pause