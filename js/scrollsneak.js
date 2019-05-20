/**
 * Based on Scroll Sneak:
 * http://mrcoles.com/scroll-sneak/
 *
 * Note: this version of scroll adjustment assumes auto expanding/collapsing navigation
 */
$(function() {

  var prefix = "docs.nav.scroll";
  var nav = $(".site-nav");

  // if window.name matches, then scroll to the position and clean up window.name
  if (window.name.search('^'+prefix+'_(\\d+)_') == 0) {
    var name = window.name.split('_');
    nav.scrollTop(name[1]);
    window.name = name.slice(2).join('_');
  }

  var originalName;
  var active = nav.find("a.active.page").parent("li");
  var activeParents = active.parentsUntil(nav);

  // add scroll sneak to all the page links in the navigation
  // most of this is adjusting for the auto expanding/collapsing
  nav.find("a.page").each(function() {
    var link = $(this);
    // if the active page is positioned above this link but not an ancestor,
    // then scroll needs to be adjusted because of the active sections collapsing
    var collapseHeight = 0;
    if ((active.length > 0) && (active.position().top < link.position().top) && (link.parentsUntil(nav).filter(active).length == 0)) {
      // find the active section that will collapse, by searching for the first common parent
      var collapsing = active;
      activeParents.each(function() {
        var ancestor = $(this);
        if (ancestor.has(link).length > 0) return false;
        collapsing = ancestor;
      });
      collapseHeight = Math.round(collapsing.children("ul").first().outerHeight(true));
    }
    link.click(function() {
      var link = $(this);
      var adjustment = collapseHeight;
      // prevent multiple clicks storing the scroll position on window.name
      if (typeof(originalName) == 'undefined') originalName = window.name;
      // store the current scroll position into the window.name
      var position = Math.max(0, nav.scrollTop() - adjustment);
      if (position) window.name = prefix + '_' + position + '_' + originalName;
    });
  });

});
