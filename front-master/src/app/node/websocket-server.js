const WebSocket = require('ws');

const wss = new WebSocket.Server({ port: 8080 });

// 将所有连接的客户端保存在数组中
let clients = [];

wss.on('connection', function(ws) {
    clients.push(ws);

    console.log((new Date()) + ' Connection accepted.');

    // 在此处开始定时发送消息
    setInterval(() => {
        ws.send('Hello, client!');
    }, 1);

    ws.on('message', function(message) {
        console.log('Received Message: ' + message);
        clients.forEach(client => {
            if (client !== ws) {  // don't want to send a message back to the sender
                client.send(message);
            }
        });
    });

    ws.on('close', function() {
        console.log((new Date()) + ' Peer ' + ws.remoteAddress + ' disconnected.');
        // remove client from the list
        var index = clients.indexOf(ws);
        if (index !== -1) {
            clients.splice(index, 1);
        }
    });
});
