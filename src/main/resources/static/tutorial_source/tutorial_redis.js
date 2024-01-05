const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/tutorial-websocket'
});

let cur_subs=null;

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/redis', (greeting) => {
        console.log(greeting)
        console.log(JSON.parse(greeting.body))
        showGreeting(JSON.parse(greeting.body).message);
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

/**
 *
 * @param connected : boolean
 */
function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

//Tutorial section

function createRoom() {
    const newChatRoomName = $("#name").val();
    stompClient.publish({
        destination: "/party-quest-chat/redis/v2/create",
        body: JSON.stringify({'roomName': newChatRoomName})
    })
}

function addChats(data) {
    $("#greetings").append("<tr><td>" + data.message + "</td></tr>");
}

function createSendInput() {
    $("#greetings").append("<tr><td><form id='message_send_form' class=\"form-inline\">" +
            "<div class=\"form-group\">" +
                "<label for=\"newMessage\">새로운 메시지 입력</label>" +
                "<input type=\"text\" id=\"newMessage\" class=\"form-control\" placeholder=\"전송할 메시지를 입력하세요\">" +
            "</div>" +
                "<button id=\"sendMessage\" class=\"btn btn-default\" type=\"submit\">전송</button>" +
            "</form></td></tr>");
    $("#message_send_form").on('submit',(e)=>{
        e.preventDefault()
        sendChat()
    })
}
function enterRoom(name) {
    console.log(name + "subscribed");
    // 새 구독을 하기전에 기존의 subscription을 만료시킨다.
    if(cur_subs !==null)
        cur_subs.unsubscribe()

    //현재 이름 표시해준다.
    $("#current_room").empty()
    $("#current_room").append(name);

    $("#greetings").empty()
    createSendInput()

    const subscribeURL = '/topic/redis/v2/chatroom/'+name
    console.log(subscribeURL)

    cur_subs = stompClient.subscribe(subscribeURL, (message) => {
        const res = JSON.parse(message.body)
        console.log(res);
        addChats(res);
    });
}

function sendChat() {
    let currentRoom = $("#current_room").text();
    let newMessage = $("#newMessage").val();
    stompClient.publish({
        destination: "/party-quest-chat/redis/v2/send",
        body: JSON.stringify({
            'roomName':currentRoom,
            'message': newMessage
        })
    })
}
$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $("#create").click(() => createRoom());
});

//util function section
