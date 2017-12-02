send2Phone = function(word) {
  var query = btoa(word.linkUrl);
  var userId = firebase.auth().currentUser.uid;
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