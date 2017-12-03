send2Phone = function(word) {
  var query = btoa(word.linkUrl);
  var user = firebase.auth().currentUser;
  if (user == null) {
    return;
  }
  var userId = user.uid;
  var notifyRef =
      firebase.database().ref('/user_group/users/' + userId + "/notify/");
  // Create a unique ref with the query of its data
  notifyRef.push(word.linkUrl);
  console.log("Pused a new value");
};

chrome.contextMenus.create({
  title: "Rep2Phone",
  contexts: ["link"],  // ContextType
  onclick: send2Phone  // A callback function
});