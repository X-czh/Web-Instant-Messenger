/*
	All the WEB ELEMENTS are shown here
*/
	var submit_button = document.querySelector('#submit');
	var usernameBox = document.querySelector('#username');
	var login = document.querySelector('#login');
	var loginMsg = document.querySelector('#login_message')
	var userColumn = document.querySelector('#active_users');
	var selfUser = document.querySelector('#self_user');
	var peerUser = document.querySelector('#peer_user');
	var interface = document.querySelector('#interface');
	var textarea = document.querySelector('#input_msg');
	var send = document.querySelector('#send');
	var conv = document.querySelector('#conv');
	var status = document.querySelector('#status');
/*
	All the REQUEST CODE are shown here
*/
	var M_LOGIN = '0';
	var M_UPDATE = '1';
	var M_CONNECT = '2';
	var M_EXCHANGE = '3'
	var M_DISCONNECT = '4';
	var M_LOGOUT = '5';

/*
	All the STATUS CODE are shown here
*/
	var M_SUCCESS = '1';
	var M_FAIL = '0';

/*
	All the STATE code are shown here
*/
	var S_OFFLINE = '0';
	var S_LOGGEDIN = '1';
	var S_CHATTING = '2';

/*
	All the possible utilities are here
*/
	var selfName;
	var state = S_OFFLINE;
	var data;
	var memberJson;
	var msgJson;
	var seq = -1;
/*
	Web element event call
*/
	if (state == S_OFFLINE){
		submit_button.addEventListener('click', function(){
			sendRequest(usernameBox.value, M_LOGIN, function (responseText) {
				if (responseText[0] == M_FAIL){
					loginMsg.innerHTML = "The userName is used, try another one!";
				}else if (responseText[0] == M_SUCCESS){
					loginMsg.innerHTML = "Logging you in..."
					responseText = responseText.substring(1);
					selfName = usernameBox.value;
					console.log(responseText);
					//login success, shifting page
					login.style.display = 'none';
					interface.style.display = 'block';
					selfUser.innerHTML = "Welcome " + selfName;
					//fetching the peer data
					data = responseText.split('|');
					memberJson = JSON.parse(data[0]);
					for (var peer in memberJson){
						if (peer != selfName){
							addMemberBox(peer);
						}
					}
					state = S_LOGGEDIN;
					seq = -1;
					var update = setInterval("sendRequest(selfName, M_UPDATE + seq.toString(), function (responseText) {\
						clearMemberBox();\
						responseText = responseText.substring(1);\
						data = responseText.split('|');\
						if (data[0].length != 0){\
							memberJson = JSON.parse(data[0]);\
							for (var peer in memberJson){\
								if (peer != selfName){\
									addMemberBox(peer);\
								}\
							}\
						}\
					});", 500);
				}
			});
		});
	}

	if (state == S_LOGGEDIN){

	}

	if (state == S_CHATTING){
		//update
		//connect
		//exchange
		//disconnect
		//logout
	}

	// setInterval(sendRequest())

	send.addEventListener('click', function () {
		addChattingBox(selfName, textarea.value, "self");
	});

/*
	Keyboard control
*/
	textarea.addEventListener('keyup',function(ev){
		if(ev.keyCode == 13){
			send.click();
		}
	});

	usernameBox.addEventListener('keyup',function(ev){
	    if(ev.keyCode == 13){
	        submit_button.click();
	    }
	});

/*
	Web element editors
*/
	//add a member to the user column when there is a new comer
	var addMemberBox = function(user){
		//use a userList to store all the online users
		//create a new member box
		var userDiv = document.createElement("div");
		userDiv.setAttribute("id", user);
		peerUser.appendChild(userDiv);
		//setting the style format for the div element
		userDiv.style.height = '7%';
		userDiv.style.marginTop = '5%';
		userDiv.style.textAlign = 'center';
		userDiv.style.fontSize = '18pt';
		userDiv.style.border = '1px';
		userDiv.style.borderStyle = "none none solid none";
		userDiv.style.borderColor = 'lightgrey';
		userDiv.innerHTML = "" + user;
	}
	//delete the logged out user
	var clearMemberBox = function(){
		for (var id in memberJson){
			if (id != selfName){
				var userDiv = document.getElementById(id);
				peerUser.removeChild(userDiv);
			}
		}
	}
	//add a chatting box whenever there is a new message
	var addChattingBox = function(user, message, source){
		var userDiv = document.createElement("div");
		conv.appendChild(userDiv);
		userDiv.style.marginTop = '3%';
		userDiv.style.marginRight = '2%';
		//the message from client side
		if (source == "self"){
			userDiv.style.textAlign = 'right';
			userDiv.innerHTML = message + "[" + user + "]";
		//the message from peers
		}else if(source == "peer"){
			userDiv.style.textAlign = 'left';
			userDiv.innerHTML = "[" + user + "] " + message;
		}
		userDiv.style.fontSize = '18pt';
		userDiv.style.border = '1px';
		userDiv.style.borderStyle = "none none dashed none";
		userDiv.style.borderColor = 'lightgrey';
		textarea.value = '';
		conv.scrollTop = conv.scrollHeight;
	}
	//clear the chatting column when a conversation ends.
	var clearChattingBox = function () {

	}
