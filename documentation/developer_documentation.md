Pocket Pickup  
=============
###Team Poseidon

Sergey Naumets (snaumets@cs)  
Victor Farkas (vfarkas@cs)  
Jacob Gile (jjgile@cs)  
Patrick Larson (palarson@cs)  
Kane Swanson (kane@cs, kdswan@uw)  
Isaiah Mathieu (imathieu@cs)  

###Development Environment

Pocket Pickup can be developed and run on any modern operating system. We have team members working in Windows, OS X and Fedora.

The below instructions are for Unix based systems with a bash shell. Contact us for assistance setting up another development environment.

###Obtaining the Documentation

Documentation is contained in the gh-pages branch of this repository, and in this wiki

###Obtaining the Source Code and Toolset

**Note: Pocket Pickup has only been tested on physical devices running Android 4.4.2. It has not been tested using emulation software**

####IDE, SDK, and source code:

1. From the command line, navigate to a directory into which you wish to store the source code. This should NOT be the Eclipse workspace that you will use for development of this app.
2. Either download and unpack the tarbal available here https://sites.google.com/site/cse403teamposeidon/feature-complete-release, or run git clone https://github.com/isaiahmathieu/pocketpickup, creating a directory called pocketpickup
3. Open Eclipse ADT. If you do not have it installed you can download it here: http://developer.android.com/sdk/index.html by clicking the “Download the SDK” button. 
4. If prompted, update the version of Android SDK Tools.
5. In Eclipse select ‘File’->’Import...’ 
6. Open the Android directory and choose ‘Existing Android Code Into Workspace’ and hit ‘Next’.
7. Hit ‘Browse’ and select the ‘pocketpickup’ directory that you just created by cloning the repo in step 2.
8. DO NOT select the checkbox option to ‘Copy projects into workspace’. 
9. Hit ‘Finish’.
10. Right-click on the PocketPickup project click on properties, navigate to Android in the left menu. Click the “add” button next to the library window. One-by-one, add the facebook, appcompat and google-play-services libraries
11. In order to run the app on an android device, you must edit your PATH variable. Add the two lines below to your .bash_profile (PATH_TO_ADT_BUNDLE is the path to the folder called “sdk” that is inside folder you downloaded in step 3 that contains Eclipse and all the Android libraries. For, example, if I downloaded the zip file and extracted it into my Downloads folder inside my home folder, PATH_TO_ADT_BUNDLE would be “~/Downloads/adt-bundle-mac-x86_64-20140321/sdk”  

`export adtsdk=PATH_TO_ADT_BUNDLE`  
`export PATH=${PATH}:${adtsdk}/sdk/platform-tools:{adtsdk}/sdk/tools`


If you get errors like “can’t find Java.Object,” reported fixes have included restarting the ADT and/or deleting all 5 projects (not from the disk) and re-importing 

####Obtaining Facebook API credentials

1. If administrative access has not been provided to you yet, email a team member to request Facebook app administrative access for Pocket Pickup.
2. To run the app on a device through your computer, go to the Pocke Pickup Facebook app, navigate to settings (left menu), scroll to the bottom, and add your key hash, which you can obtain by running:
    - OS X or linux
keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64
    - Windows (requires installation of openssl, which must been in your system path)
keytool -exportcert -alias androiddebugkey -keystore %HOMEPATH%\.android\debug.keystore | openssl sha1 -binary | openssl base64
when prompted for a password, enter “android” 
3. 
    - In the Package Explorer view, right click on the PocketPickup project folder and choose Properties
    - In the left tab, choose the Android page
    - In the Library section, click Add
    - Add the Facebook library and press okay

####Obtaining Parse.com credentials

1. If credentials have not been provided to you yet, email a team member to request Parse.com credentials.
2. Once you have the file credentials.txt, copy it into the folder called assets that is inside the PocketPickup sub-folder (PocketPickup/PocketPickup/assets) then refresh the PocketPickup folder by selecting it and right clicking then clicking ‘Refresh’. 

####Obtain a Google Maps API Key. 

**This must be done on a per-computer basis, so these steps must be followed for each computer you intend to develop the app on.**

1. Go to: https://console.developers.google.com and log in/sign up if necessary
2. Create a new project, call it whatever you want (PocketPickup is a good choice)
3. If you are not immediately brought to this new project’s dashboard, click on the new project from the list  of other projects to open the dashboard
4. Click on “APIs & auth” section of the menu on the left
5. Scroll down to find "Google Maps Android API v2" and turn it on
6. Go to credentials
7. Under Public API Access hit the CREATE NEW KEY button
8. Select Android Key. 
9. Once the dialog appears, open Eclipse and open the preferences window
10. Expand the android tab and select 'Build'
11. Copy the SHA1 fingerprint into the input of the android key creation dialog
12. Add a semicolon to the end of the SHA1 fingerprint
13. Paste the bundle name of the app (com.uwcse403.pocketpickup) after the semi-colon from the previous step then hit Create
14. Open the manifest file located at PocketPickup/AndroidManifest.xml and copy the created key into the android:value attribute of the element that looks like this:  
            `<meta-data
    		android:name="com.google.android.maps.v2.API_KEY"
    		android:value="YOUR_GOOGLE_MAPS_API_KEY_GOES_HERE"/>`


####Style Plugins:

**Please enable style check before writing new code**

1. Open Eclipse ADT
2. Click ‘Help’->’Install New Software’
3. Click ‘Add...’
4. Type ‘Eclipse Checkstyle’ into the name field, and ‘http://eclipse-cs.sf.net/update/’ as the location URL.
5. Check the box next to ‘Checkstyle’ and click next.
6. Click next, accept the licenses (after reading them, of course) then click finish.
7. Restart Eclipse ADT
8. Click ‘Preferences’->’Checkstyle’->’New’
9. Choose external configuration file, enter “Pocket Pickup Style” for the style, and choose [your source directory]/PocketPickup/assets/checkstyle.xml
10. Set this configuration to default.
11. Right click on PocketPickup in the page explorer and click properties
12. Click Checkstyle->configure->Filters->Suppression Filters->Add
13. Browse to and select the Suppressions.xml file in the assert directory
14. Right click on PocketPickup in the page explorer and click ‘Checkstyle’->’Activate Checkstyle’

###Directory structure:

The root directory is called pocketpickup and it contains five sub-directoreis: appcompat_v7, facebook, google-play-services_lib, PocketPickup, and PocketPickupTests. The first three folders contain libraries needed by the Android application and should not be altered. Inside the PocketPickup directory, developers will see the folder organization common to most Android applications.  This includes 
- gen (Android generated files), 
- bin (the built application, class files and other binaries), 
- res (resources needed by the Android application), 
- libs (necessary libraries), and 
- src (the source code for the application).

Developers should only need to worry about src and res.  src contains the bulk of the application code.  res contains resources such as static strings that the application uses for labels and warning messages.

###Building the Application:

Running the PocketPickup project as an Android app will create a .apk file in the bin directory. This is the application. 

###Running the App:

1. Enable USB Debugging on your test device. Instructions for this vary by model so consult the appropriate manual.
2. Connect your device to your computer and select the PocketPickup folder then select ‘Run’->’Run As’-> ‘Android Application’. 
3. You will be prompted to select from a list of virtual or physical devices to run the app on. If you do not see the physical device that’s plugged into your computer, open a command prompt and run: adb kill-server; adb start-server then  try running the app again.
4. After selecting your device, hitting OK will install and run the app on your device

###Testing the Application:

We build, run, sign, and test the application through the command line using Android ant.

1. First, from the root pocketpickup directory, we generate the necessary files by running “android update lib-project -p ./<LIB_DIR>” where LIB_DIR is the root directory of the facebook, google-maps, and appcompat libraries.
2. Run “android update project -p ./PocketPickup”
3. Run “android update test-project -p ./PocketPickupTests -m ../PocketPickup”

In order to get code coverage statistics a few changes need to be made to Android’s default build.xml file.  We do not actually want to change this file, so we make a copy of it.

1. Copy the build.xml file from {android_sdk}/sdk/tools/ant into some other directory
2. Modify that build xml file to change the location of coverage.ec from /data/data/<application name>/coverage.ec to /sdcard/coverage.ec
3. Modify the build.xml file in PocketPickupTests to include the modified Android build file.  This import should be the last line

Now, to test and run the code, first log in to the application to begin a Facebook session.  Note that the screen needs to be on to run the tests.

1. From the PocketPickupTests directory, run “ant clean emma debug install test”
2. The coverage.txt file will be generated in PocketPickupTests/bin

###Setting up automated daily tests:

Our team is running daily tests on one of our own local machines.  To set up automatic
builds on your own system, use the following tutorials:

[https://digitalocean.com/community/articles/how-to-install-and-use-jenkins-on-ubuntu-12-04](https://digitalocean.com/community/articles/how-to-install-and-use-jenkins-on-ubuntu-12-04)
[https://www.digitalocean.com/community/articles/how-to-build-android-apps-with-jenkins](https://www.digitalocean.com/community/articles/how-to-build-android-apps-with-jenkins)

###Releasing a version:

Push to the repository and use github’s release feature to create a new release. **Don't do this without our approval**

###Viewing and submitting bug reports:

Pocket Pickup uses Github’s bug tracking system.  Current unresolved bugs can be viewed [here](https://github.com/palarson/pocket-pickup/issues.):

New bugs can be submitted by developers at the same page by clicking the “New Issue” button.

###Design Patterns and Principles
- Adaptor Pattern – GameHandler.java allows code in the different application Activities to use use our code for manipulating the database.
- Caching – allowable sports are queried from the database and cached locally to use for populating lists. This occurs in PocketPickupApplication.
