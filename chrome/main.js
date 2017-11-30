send2Phone = function(word) {
  var query = btoa(word.linkUrl);
  chrome.tabs.create({url: "http://api.repkam09.com/api/rep2phone/" + query});
};

chrome.contextMenus.create({
  title: "Rep2Phone",
  contexts: ["link"],  // ContextType
  onclick: send2Phone  // A callback function
});