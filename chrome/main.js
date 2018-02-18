send2Phone = function(word) {
  var query = btoa(word.linkUrl);
  var user = firebase.auth().currentUser;
  if (user == null) {
    return;
  }
  var userId = user.uid;
  var notifyRef =
      firebase.database().ref('/user_group/users/' + userId + "/notify_phone/");
  // Create a unique ref with the query of its data
  notifyRef.push(word.linkUrl);
  console.log("Pused a new value");
};
open4Phone = function(word) {
  //var newURL = "https://repkap11.com/";
 //chrome.tabs.create({ url: newURL });
 console.log("Rep2 Phone 2");
}

chrome.contextMenus.create({
  title: "Rep2Phone",
  contexts: ["link"],  // ContextType
  onclick: send2Phone  // A callback function
});

// chrome.contextMenus.create({
//   title: "Rep2Phone 2",
//   contexts: ["link"],  // ContextType
//   onclick: open4Phone  // A callback function
// });