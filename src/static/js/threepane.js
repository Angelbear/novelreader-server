// The simplest page-flipping interaction system: contact to the left half of
// the reader turns back one page, contact to the right half turns forward
// one page.
//
Monocle.Panels.ThreePane = function (flipper, evtCallbacks) {

  var API = { constructor: Monocle.Panels.ThreePane }
  var k = API.constants = API.constructor;
  var p = API.properties = {}


  function initialize() {
    p.panels = {
      forwards: new Monocle.Controls.Panel(),
      current: new Monocle.Controls.Panel(),
      backwards: new Monocle.Controls.Panel()
    }

    for (dir in p.panels) {
      flipper.properties.reader.addControl(p.panels[dir]);
      p.panels[dir].listenTo(evtCallbacks);
      p.panels[dir].setDirection(flipper.constants[dir.toUpperCase()]);
      var style =   { 
    		  "width": k.WIDTH,
    		  "top" : '8%',
    		  "height": '92%'
      };
      if (dir == "forwards") {
          style["right"] = 0;
      } else if (dir == "backwards") {
          style["left"] = 0;
      } 
      p.panels[dir].properties.div.dom.setStyles(style);
    }
  }


  initialize();

  return API;
}

Monocle.Panels.ThreePane.WIDTH = "33%";
