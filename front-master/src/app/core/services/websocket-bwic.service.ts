import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class WebsocketBwicService {
  private socket!: WebSocket;

  constructor() { }

  connect(): WebSocket {
    // 构建 WebSocket URL
    const url = `ws://8.137.20.207:30001/msg/`;
    // const url = `ws://localhost:8002/msg/`;

    // 创建 WebSocket 连接
    this.socket = new WebSocket(url);

    // 处理连接成功的事件
    this.socket.onopen = () => {
      console.log('WebSocket connected');
    };

    // 处理接收到消息的事件
    this.socket.onmessage = (event) => {
      const message = event.data;
      console.log('Received message:', message);
    };

    // 处理连接关闭的事件
    this.socket.onclose = () => {
      console.log('WebSocket connection closed');
    };

    // 返回 WebSocket 实例
    return this.socket;
  }

  send(message: any): void {
    // 确保 WebSocket 连接已建立
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      // 发送消息
      this.socket.send(message);
      console.log(message,'send')
    } else {
      console.error('WebSocket connection is not open');
    }
  }

  disconnect(): void {
    // 关闭 WebSocket 连接
    if (this.socket) {
      this.socket.close();
    }
  }
}
