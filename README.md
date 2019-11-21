
# HomeHacks-Android
Georgia Tech Junior Design Project
Contributors:

 - Krishna Patel
 - Shilpa Patel
 - Bri Nations
 - Evie Taylor
 - Jason Tu

Native Android implementation
HomeHacks is an Android app designed to help auditors audit homes.
Homeowners can indicate what times they are available, and auditors can schedule audits accordingly. Auditors can then fill out an audit through the app, and homeowners can see the completed audits.

auditor module is the Auditor app.
homeowner module is the Homeowner app.

## Release Notes
Added integration tests

## Install Guide
1. Install Android Studio
2. Clone project
3. Set up Firebase (Authentication, Storage, Cloud Firestore)
	a. Create a folder in Firebase Storage called "surveys" and upload survey (sample survey in the form of v1.json located in the root directory of this repository) to the newly-created "surveys" folder.
4. Link Firebase project
5. Run