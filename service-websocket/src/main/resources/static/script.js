'use strict';
document.querySelector('#welcomeForm').addEventListener('submit', connect, true)
document.querySelector('#dialogueForm').addEventListener('submit', sendMessage, true)
var stompClient = null;
var name = null;
function connect(event) {
	name = document.querySelector('#name').value.trim();
	if (name) {
		document.querySelector('#welcome-page').classList.add('hidden');
		document.querySelector('#dialogue-page').classList.remove('hidden');
		var socket = new SockJS('http://localhost:8080/websocket');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, connectionSuccess);
	}
	event.preventDefault();
}
function connectionSuccess() {
	stompClient.subscribe('/topic/general', onMessageReceived);
  stompClient.subscribe('/topic/public', onMessageReceived);
	stompClient.send("/app/chat.newUser", {}, JSON.stringify({
		sender : name,
		type : 'NEWUSER'
	}))
}
function sendMessage(event) {
	var messageContent = document.querySelector('#chatMessage').value.trim();
	if (messageContent && stompClient) {
		var chatMessage = {
			sender : name,
			content : document.querySelector('#chatMessage').value,
			type : 'CHAT'
		};
		stompClient.send("/app/chat.sendMessage", {}, JSON
				.stringify(chatMessage));
		document.querySelector('#chatMessage').value = '';
	}
	event.preventDefault();
}

function exitChat() {
	stompClient.disconnect()
  alert('desconectado!')
  setTimeout(()=> {
    document.location.reload()
  }, 1000)
}
function onMessageReceived(payload) {
	var message = JSON.parse(payload.body);
  console.log('message', message)
	var messageElement = document.createElement('li');
	if (message.type === 'NEWUSER') {
		messageElement.classList.add('event-data');
		message.content = `${message.sender} se juntou ao chat, servidor: ${message.portServer}` ;
	} else if (message.type === 'LEAVE') {
		messageElement.classList.add('event-data');
		message.content = `${message.sender} deixou o bate-papo, servidor: ${message.portServer}`;
	} else {
		messageElement.classList.add('message-data');
		var element = document.createElement('i');
		var text = document.createTextNode(message.sender[0]);
		element.appendChild(text);
		messageElement.appendChild(element);
		var usernameElement = document.createElement('span');
		var usernameText = document.createTextNode(`${message.sender}, servidor: ${message.portServer}`);
		usernameElement.appendChild(usernameText);
		messageElement.appendChild(usernameElement);
	}
	var textElement = document.createElement('p');
	var messageText = document.createTextNode(message.content);
	textElement.appendChild(messageText);
	messageElement.appendChild(textElement);
	document.querySelector('#messageList').appendChild(messageElement);
	document.querySelector('#messageList').scrollTop = document
			.querySelector('#messageList').scrollHeight;
}