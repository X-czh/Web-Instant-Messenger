
/*
 * creates a new XMLHttpRequest object which is the backbone of AJAX,
 * or returns false if the browser doesn't support it
 */
function getXMLHttpRequest() {
	var xmlHttpReq = false;
	// to create XMLHttpRequest object in non-Microsoft browsers
	if (window.XMLHttpRequest) {
		xmlHttpReq = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		try {
			xmlHttpReq = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (exp1) {
			try {
				xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (exp2) {
				xmlHttpReq = false;
			}
		}
	}
	return xmlHttpReq;
}

/*
	LOGIN_REQUEST
*/

//Sending login request
function sendRequest(userName, msg, callback) {
	var xmlHttpRequest = getXMLHttpRequest();
	xmlHttpRequest.onreadystatechange = getReadyStateHandler(xmlHttpRequest, callback);
	xmlHttpRequest.open("POST", "chat.do/" + userName, true);
	xmlHttpRequest.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded");
	xmlHttpRequest.send(msg);
	//close the object?
}

//Handling response
function getReadyStateHandler(xmlHttpRequest, callback) {
	// an anonymous function returned
	// it listens to the XMLHttpRequest instance
	return function() {
		if (xmlHttpRequest.readyState == 4) {
			if (xmlHttpRequest.status == 200) {
				callback(xmlHttpRequest.responseText);
			} else {
				console.log(("HTTP error " + xmlHttpRequest.status + ": " + xmlHttpRequest.statusText));
			}
		}
	};
}

/*
	UPDATE_REQUEST
*/

/*
	CONNECT_REQUEST
*/

/*
	EXCHANGE_REQUEST
*/

/*
	DISCONNECT_REQUEST
*/

/*
	M_LOGGOUT
*/
