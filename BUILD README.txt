To create a distribution:

  	ensure Java 11 JDK is the version being used - the build will not work with Java 17.

Then run:

 	gradlew dist

This will read the version and build number from version.properties and
increment the build number when it finishes.

The distro will be written to build/distributions. It contains

 Run SB Editor.bat
 SimBionic-version.jar
 SimBionic-dev-version.jar (same as above with debug flags turned on)
 coreActionPredicates/
 lib/
 samples/

To increment the build number, edit api-version and engine-version in
version.properties and reset the build to 0.

If you want to build this from several different machines, I would
recommend checking in version.properties (regularly though the updated
Version.java does *not* need to be checked in). Alternatively you can
have Jenkins do all the builds, in which case you do not need to keep
the version.properties up to date.


