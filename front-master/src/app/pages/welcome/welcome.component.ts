import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzDatePickerModule } from 'ng-zorro-antd/date-picker';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzCardModule } from 'ng-zorro-antd/card';
import { FormsModule } from '@angular/forms';
import { NgForOf } from '@angular/common';
import { WebsocketComponent } from '../websocket/websocket.component';




interface Person {
  key: string;
  name: string;
  age: number;
  address: string;
}

@Component({
  selector: 'app-welcome',
  standalone: true,
  imports: [
    CommonModule,
    NzGridModule,
    NzInputModule, 
    FormsModule,
    NzDatePickerModule,  
    NzTableModule,
    NzDividerModule,
    NgForOf,
    NzCardModule,
    WebsocketComponent
  ],
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.less']
})
export class WelcomeComponent implements OnInit{
  value='1234';
  date=null;
  constructor() { }
  listOfData: Person[] = [
    {
      key: '1',
      name: 'John Brown',
      age: 32,
      address: 'New York No. 1 Lake Park'
    },
    {
      key: '2',
      name: 'Jim Green',
      age: 42,
      address: 'London No. 1 Lake Park'
    },
    {
      key: '3',
      name: 'Joe Black',
      age: 32,
      address: 'Sidney No. 1 Lake Park'
    }
  ];

  ngOnInit() {
  }

  input(event: any){
    console.log(`value: ${event.target.value}`);
  }

  
  onChange(result: Date): void {
    console.log('onChange: ', result);
  }

}
