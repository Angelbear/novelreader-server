Monocle.Controls.FontSelector = function (reader) {

  var API = { constructor: Monocle.Controls.FontSelector };
  var k = API.constants = API.constructor;
  var p = API.properties = {
    buttons: [],
    scale: 1.0,
    minscale: 0.1,
    maxscale: 10.0
  };


  function initialize() {
    p.reader = reader;
    if (localStorage != null && localStorage['font-scale'] != null) {
        p.scale = parseFloat(localStorage['font-scale']) || 1.0;
    }
    if (p.scale == NaN || p.scale < 0.1 || p.scale >  10.0) {
        p.scale = 1.0;
    }
  }


  function createControlElements(holder) {
    var btn = holder.dom.make('div', 'controls_magnifier_button');
    btn.smallA = btn.dom.append('span', 'controls_magnifier_small_a', { text: 'A' });
    btn.largeA = btn.dom.append('span', 'controls_magnifier_big_A', { text: 'A' });
    p.buttons.push(btn);
    Monocle.Events.listenForTap(btn.smallA, decreaseFontSize);
    Monocle.Events.listenForTap(btn.largeA, increaseFontSize);
    return btn;
  }


  function increaseFontSize(evt) {
    if (p.scale < p.maxscale) {
        p.scale = p.scale * k.MAGNIFICATION;
        p.reader.formatting.setFontScale(p.scale, true);
        _sync();
    }
  }

  function decreaseFontSize(evt) {
      if (p.scale > p.minscale) {
          p.scale = p.scale / k.MAGNIFICATION;
          p.reader.formatting.setFontScale(p.scale, true);
          _sync();
      }
  }

  function _sync() {
      if (localStorage != null) {
          localStorage['font-scale'] = p.scale;
      }
  }

  API.createControlElements = createControlElements;

  initialize();

  return API;
};


Monocle.Controls.FontSelector.MAGNIFICATION = 1.2;
