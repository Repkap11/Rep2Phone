// TODO(DEVELOPER): Change the values below using values from the initialization
// snippet: Firebase Console > Overview > Add Firebase to your web app.
// Initialize Firebase
var config = {
  apiKey: "AIzaSyDpRDuZLBQs8TsgHQLL9rSmK84YbDeOQiA",
  authDomain: "rep2phone.firebaseapp.com",
  databaseURL: "https://rep2phone.firebaseio.com",
  projectId: "rep2phone",
  storageBucket: "rep2phone.appspot.com",
  messagingSenderId: "952267240108"
};
firebase.initializeApp(config);
const messaging = firebase.messaging();

/**
 * initApp handles setting up the Firebase context and registering
 * callbacks for the auth status.
 *
 * The core initialization is in firebase.App - this is the glue class
 * which stores configuration. We provide an app name here to allow
 * distinguishing multiple app instances.
 *
 * This method also registers a listener with
 * firebase.auth().onAuthStateChanged.
 * This listener is called when the user is signed in or out, and that
 * is where we update the UI.
 *
 * When signed in, we also authenticate to the Firebase Realtime Database.
 */
function initApp() {
  // Listen for auth state changes.
  firebase.auth().onAuthStateChanged(function(user) {
    console.log('User state change detected from the Background script of the Chrome Extension');//,user);
    startListening();
  });
}

function startListening() {
  var user = firebase.auth().currentUser;
  if (user == null) {
    return;
  }
  var userId = user.uid;
  var ref = firebase.database().ref('/user_group/users/' + userId + "/notify_pc/");
  ref.on('child_added', function(snapshot) {
  var newURL = snapshot.val();
  console.log('Got a result:',newURL);
  chrome.tabs.create({ url: newURL });
  snapshot.ref.remove();
  });
  console.log('Listening started');

}

window.onload = function() {
  initApp();
};
