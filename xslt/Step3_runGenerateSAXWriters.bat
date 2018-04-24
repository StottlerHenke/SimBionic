

for %%f in (input\*.xsd) do java -jar saxon9he.jar -xsl:generateSAXWriters.xsl -s:%%f

pause