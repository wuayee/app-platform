
let methodMap = getFnData();
let eventNameMap = {
  change: methodMap.fullscreenchange,
  error: methodMap.fullscreenerror
};
let screenfull = {
  exit: function () {
    return new Promise(function (resolve, reject) {
      if (!this.isFullscreen) {
        resolve();
        return;
      }
      let onFullScreenExit = function () {
        this.off('change', onFullScreenExit);
        resolve();
      }.bind(this);
      this.on('change', onFullScreenExit);
      let returnPromise = document[methodMap.exitFullscreen]();
      if (returnPromise instanceof Promise) {
        returnPromise.then(onFullScreenExit).catch(reject);
      }
    }.bind(this));
  },
  onchange: function (callback) {
    this.on('change', callback);
  },
  request: function (element, options) {
    return new Promise(function (resolve, reject) {
      let onFullScreenEntered = function () {
        this.off('change', onFullScreenEntered);
        resolve();
      }.bind(this);
      this.on('change', onFullScreenEntered);
      element = element || document.documentElement;
      let returnPromise = element[methodMap.requestFullscreen](options);
      if (returnPromise instanceof Promise) {
        returnPromise.then(onFullScreenEntered).catch(reject);
      }
    }.bind(this));
  },
  on: function (event, callback) {
    let eventName = eventNameMap[event];
    if (eventName) {
      document.addEventListener(eventName, callback, false);
    }
  }
};
Object.defineProperties(screenfull, {
  isFullscreen: {
    get: function () {
      return Boolean(document[methodMap.fullscreenElement]);
    }
  },
  element: {
    enumerable: true,
    get: function () {
      return document[methodMap.fullscreenElement];
    }
  },
  isEnabled: {
    enumerable: true,
    get: function () {
      return Boolean(document[methodMap.fullscreenEnabled]);
    }
  }
});
function getFnData() {
  let methodsVal;
  let methodsMap = [
    [
      'requestFullscreen',
      'exitFullscreen',
      'fullscreenElement',
      'fullscreenEnabled',
      'fullscreenchange',
      'fullscreenerror'
    ],
    [
      'webkitRequestFullscreen',
      'webkitExitFullscreen',
      'webkitFullscreenElement',
      'webkitFullscreenEnabled',
      'webkitfullscreenchange',
      'webkitfullscreenerror'

    ],
    [
      'webkitRequestFullScreen',
      'webkitCancelFullScreen',
      'webkitCurrentFullScreenElement',
      'webkitCancelFullScreen',
      'webkitfullscreenchange',
      'webkitfullscreenerror'
    ],
    [
      'mozRequestFullScreen',
      'mozCancelFullScreen',
      'mozFullScreenElement',
      'mozFullScreenEnabled',
      'mozfullscreenchange',
      'mozfullscreenerror'
    ],
    [
      'msRequestFullscreen',
      'msExitFullscreen',
      'msFullscreenElement',
      'msFullscreenEnabled',
      'MSFullscreenChange',
      'MSFullscreenError'
    ]
  ];
  let ret = {};
  for (let i = 0; i < methodsMap.length; i++) {
    methodsVal = methodsMap[i];
    if (methodsVal && methodsVal[1] in document) {
      for (let j = 0; j < methodsVal.length; j++) {
        ret[methodsMap[0][j]] = methodsVal[j];
      }
      return ret;
    }
  }
  return false;
}
export default screenfull;
