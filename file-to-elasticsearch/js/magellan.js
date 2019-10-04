$(function() {

  // add magellan targets to anchor headers, for h1 and h2
  $("a.anchor").each(function() {
    var anchor = $(this);
    var name = anchor.attr("name");
    var header = anchor.parent();
    header.attr("id", name);
    if (header.is("h1") || header.is("h2")) {
      header.attr("data-magellan-target", name);
    }
  });

  // enable magellan plugin on the active page header links in the navigation
  var nav = $(".site-nav a.active.page").parent("li");
  if (nav.length > 0) {
    // strip navigation links down to just the hash fragment
    nav.find("a.active.page, a.header").attr('href', function(_, current){
        return this.hash ? this.hash : current;
    });
    new Foundation.Magellan(nav);
  }

});
