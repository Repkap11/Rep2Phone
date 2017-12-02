send2Phone = function(word) {
  var query = btoa(word.linkUrl);
  var userId = firebase.auth().currentUser.uid;
  firebase.database()
      .ref('/user_group/users/' + userId)
      .once('value')
      .then(function(userSnapshot) {
        console.log(userSnapshot.val());
        userSnapshot.child('devices').forEach(function(deviceSnapshot) {
          console.log("Got Device!");
          var deviceID = deviceSnapshot.key;
          console.log(deviceID);
          chrome.tabs.create(
              {url: "http://api.repkam09.com/api/rep2phone/" + deviceID});
        });

      });
};

chrome.contextMenus.create({
  title: "Rep2Phone",
  contexts: ["link"],  // ContextType
  onclick: send2Phone  // A callback function
});