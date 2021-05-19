var script = document.getElementById("map_load");
var JS__FILE__ = script.getAttribute("src");  // 获得当前js文件路径
var home = JS__FILE__.substr(0, JS__FILE__.lastIndexOf("/") + 1); // 地图API主目录

(function () {
  window.Map_loadScriptTime = (new Date).getTime();
  const mapType = 'baidu';   // 地图类型
  localStorage.setItem('mapType', mapType);
  // if(mapType === 'baidu') {  // 百度地图
  //   var B_MAP = document.createElement('script');
  //   var TextIconOverlay_min = document.createElement('script');
  //   var b_markerClusterer = document.createElement('script');
  //   B_MAP.setAttribute("src", "http://api.map.baidu.com/api?v=3.0&ak=ZczWHZo66FceT4MYuVkK4kY3");
  //   TextIconOverlay_min.setAttribute("src", "http://api.map.baidu.com/library/TextIconOverlay/1.2/src/TextIconOverlay_min.js");
  //   b_markerClusterer.setAttribute("src", home + 'b_markerClusterer.js');
  //   B_MAP.onload = function() {
  //     document.documentElement.appendChild(TextIconOverlay_min);
  //     document.documentElement.appendChild(b_markerClusterer);
  //   });
  //   document.documentElement.appendChild(B_MAP);
  // }  else {  // 谷歌地图
  //   document.write('<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCGwT3XelPA7xS3wtyOekwZ6RKCJKV_GKM"> </script>');
  //   document.write('<script async defer src="'+bmapcfg.home + 'g_markerClusterer_min.js"></script>');
  // }
  if (mapType === 'baidu') {  // 百度地图
    document.write('<script type="text/javascript" src="http://api.map.baidu.com/api?v=3.0&ak=ZczWHZo66FceT4MYuVkK4kY3"></script>');
    document.write('<script type="text/javascript" src="http://api.map.baidu.com/library/TextIconOverlay/1.2/src/TextIconOverlay_min.js"></script>');
    document.write('<script type="text/javascript" src="' + home + 'b_markerClusterer.js"></script>');
    document.write('<script type="text/javascript" src="http://api.map.baidu.com/library/DrawingManager/1.4/src/DrawingManager_min.js"></script>');
  } else {  // 谷歌地图
    document.write('<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCGwT3XelPA7xS3wtyOekwZ6RKCJKV_GKM&libraries=drawing"> </script>');
   // document.write('<script src="' + home + 'offline_g_map.js"></script></script>');
    document.write('<script src="' + home + 'g_markerClusterer_min.js"></script>');
  }

})();
