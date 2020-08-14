# RTC-Trigger-Builds-Remotely
Utility for operations on RTC from Jenkins

### Summary

This is a Java based utility which is packaged an an executable jar file using maven. This uses RTC Plain Java API to automate RTC functions. 

### Functions

- You can use this utility to trigger a personal build on a build definition file from your personal workspace. It allows you to override the build parameters also. It requires valid credentials (email/password) to login to RTC. Your personal workspace may need to have a public scope for the build definition to be able to execute a personal build from your personal workspace. This is usually not executed as a standalone jar rather its used in a Jenkins job which gives you a UI to be able to provide all required data in an efficient and error-free way.

### RTC Plain Java API
This API can be downloaded free from Jazz site. You just have to create an account using personal or official mail id.
This is downloaded and checked-in to this repo under the 'lib' folder. The API version must match the server version which is 6.0.2 at the moment. In future, if the server upgrades, you need to download and replace the content of the lib folder with the new version. You will also have to populate the 'pom.xml' file with this new version. To re-populate, you may use the 'generateDepedencyPom.java' class file that will generate the dependency 'xml' portion. Once done the replacement, you will be able to recompile and generate the new utility jar with the 'pom.xml' file.

Download here (Change version as needed) - https://jazz.net/downloads/rational-team-concert/releases/6.0.2/RTC-Client-plainJavaLib-6.0.2.zip
