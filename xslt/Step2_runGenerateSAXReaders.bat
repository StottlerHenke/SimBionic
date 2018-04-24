

for %%f in (input\*.xsd) do java -jar saxon9he.jar -xsl:generateSAXReaders.xsl -s:%%f

pause