/*
	All the WEB ELEMENTS are shown here
*/
	var submit_button = document.querySelector('#submit');
	var usernameBox = document.querySelector('#username');
	var login = document.querySelector('#login');
	var loginMsg = document.querySelector('#login_message');
	var userColumn = document.querySelector('#active_users');
	var selfUser = document.querySelector('#self_user');
	var peerUser = document.querySelector('#peer_user');
	var interface = document.querySelector('#interface');
	var textarea = document.querySelector('#input_msg');
	var send = document.querySelector('#send');
	var logOut = document.querySelector('#logOut');
	var conv = document.querySelector('#conv');
	var info = document.querySelector('#info');

	var connect = document.querySelector('#connect');
	var update = document.querySelector('#update');
	var leave = document.querySelector('#leave');
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
	var data;
	var memberJson;
	var msgJson;
	var groupMemberJson;
	var seq = -1;
	var setIn;
	var chattingStatus = 0;
	var state = S_OFFLINE;
	var chatBoxNbr = 10000;
/*
	Web element event call
*/
	submit_button.addEventListener('click', function(){
		if (state == S_OFFLINE){
			sendRequest(usernameBox.value, M_LOGIN, function (responseText) {
				if (responseText[0] == M_FAIL){
					loginMsg.innerHTML = "The userName is used, try another one!";
				}else if (responseText[0] == M_SUCCESS){
					loginMsg.innerHTML = "Logging you in..."
					responseText = responseText.substring(2);
					selfName = usernameBox.value;
					//console.log(responseText);
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
					seq = -1;
					setIn = setInterval("update.click();", 500);
					state = S_LOGGEDIN;
				}
			});
		}
	});

	send.addEventListener('click', function () {
		if (state == S_CHATTING){
			sendRequest(selfName, M_EXCHANGE + textarea.value, function (responseText) {
				//console.log('send!');
			});
		}
	});

	logOut.addEventListener('click',function(){
		if (state == S_LOGGEDIN){
			sendRequest(selfName, M_LOGOUT, function (responseText) {
				if (responseText == M_SUCCESS){
					loginMsg.innerHTML = "Username:";
					login.style.display = 'block';
					interface.style.display = 'none';
					clearInterval(setIn);
					clearMemberBox();
					clearChattingBox();
					state = S_OFFLINE;
				}
			});
		}else if(state == S_CHATTING){
			leave.click();
			sendRequest(selfName, M_LOGOUT, function (responseText) {
				if (responseText == M_SUCCESS){
					loginMsg.innerHTML = "Username:";
					login.style.display = 'block';
					interface.style.display = 'none';
					clearInterval(setIn);
					clearMemberBox();
					state = S_OFFLINE;
				}
			});
		}
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
		userDiv.addEventListener('click', function () {
			if (state == S_LOGGEDIN){
				sendRequest(selfName, M_CONNECT + user, function(responseText){
					var code = responseText[0];
					if (code == M_FAIL){
						info.innerHTML = "The user is too busy, maybe chat later!";
					}else if(code == M_SUCCESS){
						info.innerHTML = "You are chatting with " + user;
						seq = responseText[1];
						//console.log(seq);
					}
				});
			}else{
				info.innerHTML = "You have to leave the current conversation first!";
			}
		});
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
		userDiv.setAttribute("id", chatBoxNbr.toString());
		chatBoxNbr ++;
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
		for (var i = 10000; i < chatBoxNbr; i++){
			var userDiv = document.getElementById(i.toString());
			conv.removeChild(userDiv);
		}
		chatBoxNbr = 10000;
	}

	update.addEventListener('click', function () {
		if (state == S_CHATTING || state == S_LOGGEDIN){
			sendRequest(selfName, M_UPDATE + seq.toString(), function (responseText) {
				if (responseText[0] == M_SUCCESS){
					if (state == S_CHATTING && responseText[1] == '0'){
							info.innerHTML = "Disconnected from the current group!";
							state = S_LOGGEDIN;
							seq = -1;
							clearChattingBox();
					}
					if (state == S_LOGGEDIN && responseText[1] == '1'){
							seq = 0;
							state = S_CHATTING;
							info.innerHTML = "Chatting";
					}
					// console.log(seq);
					clearMemberBox();
					responseText = responseText.substring(2);
					data = responseText.split('|');
					if (data[0].length != 0){
						memberJson = JSON.parse(data[0]);
						for (var peer in memberJson){
							if (peer != selfName){
								addMemberBox(peer);
							}
						}
					}
					if (data[1].length != 0){
						msgJson = JSON.parse(data[1]);
						for (var i = 0; i < msgJson.length; i++){
							if (msgJson[i][0].toString() == selfName){
								addChattingBox(selfName, msgJson[i][1].toString(), "self");
								seq++;
							}else{
								addChattingBox(msgJson[i][0].toString(), msgJson[i][1].toString(), "peer");
								seq++;
							}
						}
					}
				}else if(responseText[0] == M_FAIL){
					logOut.click();
				}
				// if (data[1].length != 0){
				// 	msgJson = JSON.parse(data[1]){
				// 	}
				// }
			});
		}
	});

	leave.addEventListener('click', function () {
		if (state == S_CHATTING){
			sendRequest(selfName, M_DISCONNECT, function(responseText){
				if (responseText == M_SUCCESS){
					info.innerHTML = "Disconnected from the current group!";
					state = S_LOGGEDIN;
					seq = -1;
					clearChattingBox();
				}
			});
		}
	});
