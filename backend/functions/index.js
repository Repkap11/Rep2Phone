// The Cloud Functions for Firebase SDK to create Cloud Functions and setup
// triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


var trigger_notify = require('./trigger_notify');
exports.trigger_notify =
    functions.database.ref('/user_group/users/{userId}/notify')
        .onWrite(event => {return trigger_notify.trigger_notify(event)});