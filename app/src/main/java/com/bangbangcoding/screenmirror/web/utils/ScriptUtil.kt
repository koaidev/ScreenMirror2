package com.bangbangcoding.screenmirror.web.utils

class ScriptUtil {

    companion object {
        const val FACEBOOK_SCRIPT = "javascript:function clickOnVideo(link, classValueName){" +
                "browser.getVideoData(link);" +
                "var element = document.getElementById(\"mInlineVideoPlayer\");" +
                "element.muted = true;" +
                "var parent = element.parentNode; " +
                "parent.removeChild(element);" +
                "parent.setAttribute('class', classValueName);}" +
                "function getVideoLink(){" +
                "try{var items = document.getElementsByTagName(\"div\");" +
                "for(i = 0; i < items.length; i++){" +
                "if(items[i].getAttribute(\"data-sigil\") == \"inlineVideo\"){" +
                "var classValueName = items[i].getAttribute(\"class\");" +
                "var jsonString = items[i].getAttribute(\"data-store\");" +
                "var obj = JSON && JSON.parse(jsonString) || $.parseJSON(jsonString);" +
                "var videoLink = obj.src;" +
                "var videoName = obj.videoID;" +
                "items[i].setAttribute('onclick', \"clickOnVideo('\"+videoLink+\"','\"+classValueName+\"')\");}}" +
                "var links = document.getElementsByTagName(\"a\");" +
                "for(i = 0; i < links.length; i++){" +
                "if(links[ i ].hasAttribute(\"data-store\")){" +
                "var jsonString = links[i].getAttribute(\"data-store\");" +
                "var obj = JSON && JSON.parse(jsonString) || $.parseJSON(jsonString);" +
                "var videoName = obj.videoID;" +
                "var videoLink = links[i].getAttribute(\"href\");" +
                "var res = videoLink.split(\"src=\");" +
                "var myLink = res[1];" +
                "links[i].parentNode.setAttribute('onclick', \"browser.getVideoData('\"+myLink+\"')\");" +
                "while (links[i].firstChild){" +
                "links[i].parentNode.insertBefore(links[i].firstChild," +
                "links[i]);}" +
                "links[i].parentNode.removeChild(links[i]);}}}catch(e){}}" +
                "getVideoLink();"

        const val FACEBOOK_SCRIPT_VIDEO = """javascript:
            function getVideoLink(){
                const items = document.getElementsByTagName('div');
                var result1 = false;
                for(i = 0; i < items.length; i++){
                    if( items[i].hasAttribute('data-video-id')) {
                        var id = items[i].getAttribute('data-video-id');
                        return id;
                    }
                }
                return "_ooo_";
            }
            
            getVideoLink();
        """

        const val FB_V2 = """javascript:
            function getVideoLink(){
                const items = document.getElementsByTagName('div');
                var result1 = "";
                for(i = 0; i < items.length; i++){
                    if( items[i].hasAttribute('data-video-id')) {
                        var id = items[i].getAttribute('data-video-id');
                        result1 += id + "---";
                        
                        //items[i].getChildren()
                        //inline-video-icon
                        var size = items[i].children.length;
                        
                        for(j = 0; j < size; j++){
                        	//if( items[i].hasAttribute('data-video-id')) {
                        	result1 += ">" + items[i].children[j].className + ";";
                            if (result1.includes('inline-video-icon play hidden')) {
                            return id;
                            //}
                            }
                        }
                        //return;
                    }
                }
                return "_000_";
                
                        //document.getElementById("demo").innerHTML = result1;
                //document.getElementById("demo").innerHTML = "_ooo_";
            }
            
            getVideoLink();
        """

        const val FB_V3 = """javascript:
            function getVideoLink(){
                const items = document.getElementsByTagName('div');
                var result1 = "";
                for(i = 0; i < items.length; i++){
                    if( items[i].hasAttribute('data-store')) {
                        var id = items[i].getAttribute('data-store');
                        if (id.includes('videoID')) {
                            //return id;
                            const obj = JSON.parse(id);
//                        const videos = document.getElementsByTagName('video')
                        
                        const videos = items[i].getElementsByTagName('video')
                        if (videos.length > 0) {
                        	return obj.videoURL; 
                        }
                            //return obj.videoURL;
                        }
                        
                        
//                        result1 += id + " ---";
//                        const obj = JSON.parse(id);
                        
                        //items[i].getChildren()
                        //inline-video-icon
                        
//                        return obj.videoURL;
                        
//                        const videos = document.getElementsByTagName('video')
//                        if (videos.length > 0) {
//                        	return obj.videoURL; 
//                        }
                    }
                }
                return "_696_";
            }
            getVideoLink();
        """

        const val ARAB = """
            
        """

        const val BABE_P = """
                function onProcessXVideo() {
                  //let content = document.getElementById('content');
                  let links = document.getElementsByTagName('link');
                 
                  for (var i = 0; i < links.length; i++) {
                    let script = links[i];
                    if (script.hasAttribute('rel')) {
                      var id = script.getAttribute('rel');
                      var p0 = script.getAttribute('href');
                
                      if (id.indexOf('video_src') > -1) {
                        return p0;
                      }
                    }
                  }
                  return '_000_';
                }
                onProcessXVideo();
        """


        const val BABYLON = """
            function onProcessXVideo() {
                  //let content = document.getElementById('content');
                  let links = document.getElementsByTagName('video');
                 
                  for (var i = 0; i < links.length; i++) {
                    let script = links[i];
                    if (script.hasAttribute('src')) {
                   
                      var id = script.getAttribute('src');
                      if (id.includes('.mp4')) {
                        return id;
                      }
                    }
                  }
                    return "_000_";
                }
                onProcessXVideo();
        """

//        <script id='initials-script'>
//        window.initials=
        const val HAMSTER = """
            function onProcessXVideo() {
                  //let content = document.getElementById('content');
                  let links = document.getElementsByTagName('script');
                 
                  for (var i = 0; i < links.length; i++) {
                    let script = links[i];
                    if (script.hasAttribute('id')) {
                    if (script.getAttribute('id') == 'initials-script') {
                    
                   
                      var id = script.getAttribute('src');
                      if (id.includes('.mp4')) {
                        return id;
                      }
                    }
                    }
                  }
                    return "_000_";
                }
                onProcessXVideo();
        """

        const val IJJJ = """
            function onProcessXVideo() {
                  let links = document.getElementsByTagName('source');
                 
                  for (var i = 0; i < links.length; i++) {
                    let script = links[i];
                    if (script.hasAttribute('src') && script.hasAttribute('type')) {
                   
                      var id = script.getAttribute('src');
                      var type = script.getAttribute('type');
                     
                      if (type.includes('mp4')) {
                        return id;
                      }
                    }
                  }
                  return "_000_";
                }
                onProcessXVideo()
        """

        const val XXX1 = "javascript: (function onProcessXVideo(){\n" +
                "    let content = document.getElementById('content');\n" +
                "    let scripts = content.getElementsByTagName('script');\n" +
                "    for(var i = 0; i< scripts.length; i++) {\n" +
                "        let script = scripts[i];\n" +
                "        if (script.text.indexOf('setVideoUrlLow') > -1 || script.text.indexOf('setVideoUrlHigh') > -1) {\n" +
                "            AndroidDownloader.onProcessXVideo(script.text);\n" +
                "            break;\n" +
                "        }\n" +
                "    }\n" +
                "})()"

        const val PORX = "javascript: (function loadVideoPornHub(){\n" +
                "    let parent = document.getElementsByClassName('container clearfix');\n" +
                "    for(var i = 0 ; i < parent.length;i++){\n" +
                "        let child = parent[i];\n" +
                "        let scripts = child.getElementsByTagName('script');\n" +
                "        for(var j = 0 ; j < scripts.length;j++){\n" +
                "            let script = scripts[0];\n" +
                "            if (script.text.indexOf('flashvars') > -1 || script.text.indexOf('flashvars') > -1) {\n" +
                "                let key = script.text.substring(script.text.indexOf('var') + 3,script.text.indexOf('='));\n" +
                "                var myJsonString = JSON.stringify(eval(key));\n" +
                "                AndroidDownloader.loadVideoPornHub(myJsonString);\n" +
                "                break;\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "})()"
    }
}
