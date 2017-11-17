<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Demo Websockets</title>
</head>
<body>
<div>
    <input type="submit" value="Start" onclick="start()"/>
</div>
<div id="messages"></div>
<script type="text/javascript">
    var webSocket = new WebSocket('ws://localhost:8080/demoWebsocket/bbb');
    webSocket.onerror = function (event) {
        onError(event)
    };
    webSocket.onopen = function (event) {
        onOpen(event)
    };
    webSocket.onclose = function (event) {
        onClose(event)
    };
    webSocket.onmessage = function (event) {
        onMessage(event)
    };

    function onMessage(event) {
        document.getElementById('messages').innerHTML
            += '<br />Receive:' + event.data;
    }

    function onOpen(event) {
        document.getElementById('messages').innerHTML
            = 'Connection established';
    }

    function onClose(event) {
        document.getElementById('messages').innerHTML
            += '<br />disconnection';
    }

    function onError(event) {
        alert(event.data);
    }

    function start() {
        webSocket.send('hello');
        return false;
    }
</script>
</body>
</html>