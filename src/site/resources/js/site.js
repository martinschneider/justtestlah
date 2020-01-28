/* some formatting hacks */
$(function() {
  $("div.well.sidebar-nav").remove();
  $("body>hr").remove();
  // fix ToC links
  $("a").each(function(){ 
    var oldName=$(this).attr("name");
    if (oldName!=undefined)
    {
    var newName=oldName.replace(new RegExp("_", "g"), "-")
                       .replace(new RegExp("2[0-9,a-f,A-F]|\\.|:", "g"),"")
                       .toLowerCase();
      $(this).attr("name", newName);
    }
  });
  document.title="JustTestLah! (JTL) test framework";
});