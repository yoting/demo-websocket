<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <title>Hello WebSocket</title>
    <script src="http://cdn.bootcss.com/sockjs-client/1.1.1/sockjs.min.js"></script>
    <script src="http://cdn.bootcss.com/stomp.js/2.3.3/stomp.js"></script>
    <script src="http://cdn.bootcss.com/jquery/3.1.1/jquery.min.js"></script>
    <script type="text/javascript">
        var stompClient = null;

        function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
            document.getElementById('response').innerHTML = '';
        }

        //连接到websocket
        function connect() {
            var userid = document.getElementById('name').value;
            var socket = new SockJS("http://localhost:8080/demoWebsocket/stomp");
            stompClient = Stomp.over(socket);
            stompClient.connect({name: 'aa'}, function (frame) {
                setConnected(true);
                ///topic/hello是/app/hello返回消息的默认地址
                stompClient.subscribe('/topic/hello', function (resultDto) {
                    alert(resultDto)
                    showResultMessage(JSON.parse(resultDto.body).content);
                });
                ///app/hello1是直接订阅到接口
                stompClient.subscribe('/app/hello1', function (resultDto) {
                    alert(resultDto)
                    showResultMessage(JSON.parse(resultDto.body).content);
                });
                ///topic/world1是/app/hello1返回消息重定位地址
                stompClient.subscribe('/topic/world1', function (resultDto) {
                    alert(resultDto)
                    showResultMessage(JSON.parse(resultDto.body).content);
                });
                ///queue/world2是/app/hello2返回消息重定位地址
                stompClient.subscribe('/queue/world2', function (resultDto) {
                    alert(resultDto)
                    showResultMessage(JSON.parse(resultDto.body).content);
                });
                ///user/queue/message是服务端发送消息给/queue/message目的地的订阅
                // 该种订阅方式是一对一的，不同的客户端连接会分别各自收到自己的消息
                stompClient.subscribe('/user/topic/message', function (resultDto) {
                    alert(resultDto);
                    showResultMessage(JSON.parse(resultDto.body).content);
                });
                ///topic/hello3是服务端主动发送消息地址
                stompClient.subscribe('/topic/world4', function (resultDto) {
                    alert(resultDto)
                    showResultMessage(JSON.parse(resultDto.body).content);
                });
                ///user/topic/message是服务端主动发送消息地址
                stompClient.subscribe('/user/queue/message', function (resultDto) {
                    alert(resultDto);
                    showResultMessage(JSON.parse(resultDto.body).content);
                });

            });
        }

        //断开websocket连接
        function disconnect() {
            if (stompClient != null) {
                stompClient.disconnect();
            }
            setConnected(false);
            console.log("Disconnected");
        }

        //展示接收的消息
        function showResultMessage(message) {
            var response = document.getElementById('response');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(message));
            response.appendChild(p);
        }

        //发送接收广播
        function sendBroadcast() {
            var name = document.getElementById('name').value;
            stompClient.send("/app/hello2", {
                atytopic: "greetings",
                simpSessionId: "hahahaha"
            }, JSON.stringify({'name': name, 'age': 12}));
        }

        //通过特定用户发送和接收
        function sendByUser() {
            stompClient.send("/app/hello3", {}, JSON.stringify({'name': "gusi", 'age': 22}));
        }
    </script>
</head>
<body>
<div>
    <div>
        <button id="connect" onclick="connect();">Connect</button>
        <button id="disconnect" disabled="disabled" onclick="disconnect();">Disconnect</button>
    </div>
    <div id="conversationDiv">
        <label>What is your name?</label><input type="text" id="name" value="gusi"/>
        <button id="sendBroadcast" onclick="sendBroadcast();">Send Broadcast</button>
        <button id="sendByUser" onclick="sendByUser();">Send By User</button>
        <p id="response"></p>
    </div>
</div>
</body>
</html>