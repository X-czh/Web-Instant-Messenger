// The targeted url is: http://www.tuling123.com/openapi/api
// The API key of little-potato-bot: 33f89e00865c4d83ae6d3e03962b3c51
// Required request method: HTTP POST, JSON;
// content: key, info, userid;
// encode: utf-8
// Response: {"code" : 100000, "text":"xxx"}

/*
 * creates a new XMLHttpRequest object which is the backbone of AJAX,
 * or returns false if the browser doesn't support it
 */

 /*
  * creates a new XMLHttpRequest object which is the backbone of AJAX,
  * or returns false if the browser doesn't support it
  */

var userid = "daddy"
var key = "33f89e00865c4d83ae6d3e03962b3c51"
var send_button = document.querySelector("#send");
var response_output = document.querySelector("#response");
var input_msg = document.querySelector("#input_msg");
var data = {
    "key" : "http://www.tuling123.com/openapi/api",
    "info" : null,
    "userid" : userid
}

send_button.addEventListener("click", function(){
    if(input_msg.value.length > 0){
        data["info"] = input_msg.value;
        sendRequest(JSON.stringify(), function(responseText){
            responseText = JSON.parse(responseText);
            responseText = responseText["text"];
            response_output.innerHTML = responseText;
        });
    }else{
        alert("输入框不能为空！");
    }
});

function getXMLHttpRequest(){
     var xmlHttpReq = false;
     // to create XMLHttpRequest object in non-Microsoft browers
     if (window.XMLHttpRequest){
         xmlHttpReq = new XMLHttpRequest();
     }else if (window.ActiveXObject){
         try {
             xmlHttpReq = new ActiveXObject("Msxml2.XMLHTTP");
         } catch (exp1) {
             try {
                 xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
             } catch (exp2){
                 xmlHttpReq = false;
             }
         }
     }
     return xmlHttpReq;
 }

function sendRequest(data, callback){
    var xmlHttpRequest = getXMLHttpRequest()
    xmlHttpRequest.onreadystatechange = getReadyStateHandler(xmlHttpRequest, callback);
    xmlHttpRequest.open("POST", "http://www.tuling123.com/openapi/api", true);
    xmlHttpRequest.setRequestHeader("Content-Type", "application/json;charset=utf-8")
    xmlHttpRequest.send(data);
}

function getReadyStateHandler(xmlHttpRequest, callback){
    // an anonymous function returned
    // It listens to the XMLHttpRequest instance
    return function(){
        if (xmlHttpRequest.readyState == 4){
            if (xmlHttpRequest.status == 200){
                callback(xmlHttpRequest.responseText);
            } else {
                console.log("HTTP error " + xmlHttpRequest.status + ": " + xmlHttpRequest.statusText);
            }
        }
    }
}
