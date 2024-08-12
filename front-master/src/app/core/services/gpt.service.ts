import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError, Observer } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { EventSourcePolyfill } from 'event-source-polyfill';

@Injectable({
  providedIn: 'root'
})
export class GptService {
  constructor(private http: HttpClient) { }

  traderChatWithGpt(message: string): Observable<MessageEvent> {

    const apiUrl = 'api/v1/bwic-service/bwics/chat';
    const authToken = localStorage.getItem('Token') || '';
    const headers = { 'Authorization': authToken };
    let params = new HttpParams();
    const messages = {
      "model": "moonshot-v1-8k",
      "messages": [
        {
          "role": "system",
          "content": "你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。"
        },
        {
          "role": "user",
          "content": `${message}`
        }
      ],
      "temperature": 0.3
    };
    params = params.append('message', JSON.stringify(messages));
    // params = params.append('message', message);

    return new Observable((observer: Observer<MessageEvent>) => {

      const eventSource = new EventSourcePolyfill(apiUrl + '?' + params.toString(), { headers: headers });
      console.log('message', eventSource);
      eventSource.onmessage = ((event: MessageEvent) => {
        console.log('Received data:', event.data);
        observer.next(event);

        // 解析数据
        const data = JSON.parse(event.data);
        if (data.choices[0].finish_reason === 'stop') {
          observer.complete();
        }
      }) as any;

      eventSource.onerror = ((error: Event) => observer.error(error)) as  any;



      return () => {
        console.log('closing event source');
        eventSource.close();
      };
    }).pipe(catchError(err => {
      console.error('API call failed:', err);
      return throwError(err);
    }));
  }

  clientChatWithGpt(message: string): Observable<MessageEvent> {
    const apiUrl = 'api/v1/bwic-service/bids/chat';
    const authToken = localStorage.getItem('Token') || '';
    const headers = new HttpHeaders({
      'Authorization': authToken,
      'Content-Type': 'application/json' // 指定内容类型为 JSON
    });

    let params = new HttpParams();
    const str = [{
      "role": "system",
      "content": "你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。"
    },
    {
      "role": "user",
      "content": message // 使用传入的消息内容
    }]

    params = params.append('message',     JSON.stringify(str)      );

    return this.http.post<MessageEvent>(apiUrl, null, { headers: headers, params: params })
      .pipe(
        catchError((error: any) => {
          console.error('API调用失败:', error);
          return throwError(error);
        })
      );
  }

}
