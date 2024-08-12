import { ChangeDetectionStrategy, Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NzLayoutComponent, NzLayoutModule } from 'ng-zorro-antd/layout';
import { Message, WebsocketService } from 'src/app/core/services/websocket.service';
import { FormsModule, NgModel } from '@angular/forms';

@Component({
  selector: 'app-websocket',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule 
  ],
  templateUrl: './websocket.component.html',
  styles: [
  ],
  providers: [WebsocketService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WebsocketComponent {
  

  title = 'socketrv';
  content = '';
  received: Message[] = [];
  sent: Message[] = [];

  constructor(private WebsocketService: WebsocketService) {
    WebsocketService.messages.subscribe(msg => {
      this.received.push(msg);
      console.log("Response from websocket: " + msg);
    });
  }

  sendMsg() {
    let message = {
      source: '',
      content: ''
    };
    message.source = 'localhost';
    message.content = this.content;

    this.sent.push(message);
    this.WebsocketService.messages.next(message);
  }
}
