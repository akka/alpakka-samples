$(function() {

  // close the overlay navigation when header links are clicked
  $(".overlay-nav .nav-toc a.header, .overlay-nav .nav-toc a.active.page").attr("data-toggle", "underlay overlay");

  // TOCs support three styles:
  //   - box: wrap in a shadowed box and apply TOC styling
  //   - blocks: section TOCs in boxes in a block grid with equal heights
  //   - list: regular list of links
  var tocs = $(".page-content .toc");
  tocs.each(function() {
    var toc = $(this);
    // if there's no style already set then add .box for TOCs of depth 1 otherwise .blocks
    if (!(toc.hasClass("blocks") || toc.hasClass("box") || toc.hasClass("list"))) {
      toc.addClass((toc.children("ul").children("li").has("ul").length == 0) ? "box" : "blocks");
    }
    if (toc.hasClass("blocks")) {
      var list = toc.children("ul");
      list.addClass("row medium-up-2 large-up-3 toc-grid");
      list.children("li").addClass("column column-block toc-block").attr("data-equalizer-watch", "").wrapInner("<div class='toc-box'></div>");
      new Foundation.Equalizer(list, { equalizeByRow: true, equalizeOn: "medium" });
    } else if (toc.hasClass("box")) {
      toc.wrapInner("<div class='toc-box'></div>");
    }
  });
});
