send2Phone = function(word) {
  var url;
  if (word.linkUrl){
    //console.log("linkUrl");
    url = word.linkUrl;
  } else if (word.pageUrl){
    //console.log("pageUrl");
    url = word.pageUrl;
  }
  console.log("Url:",url);
  
  var user = firebase.auth().currentUser;
  if (user == null) {
    return;
  }
  var userId = user.uid;
  var notifyRef =
      firebase.database().ref('/user_group/users/' + userId + "/notify_phone/");
  notifyRef.push(url);
};

chrome.contextMenus.create({
  title: "Rep2Phone",
  contexts: ["link","page"],  // ContextType
  onclick: send2Phone  // A callback function
});