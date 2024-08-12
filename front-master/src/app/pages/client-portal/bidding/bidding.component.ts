import {Component,OnInit} from '@angular/core';
import {BehaviorSubject,Observable,filter} from 'rxjs';
import {CommonModule} from '@angular/common';
import {NzInputModule} from 'ng-zorro-antd/input';
import {NzIconModule} from 'ng-zorro-antd/icon';
import {NzButtonModule} from 'ng-zorro-antd/button';
import {NzFormModule} from 'ng-zorro-antd/form';
import {NzCardModule} from 'ng-zorro-antd/card';
import {NzTableModule} from 'ng-zorro-antd/table';
import {NzDividerModule} from 'ng-zorro-antd/divider';
import {FormsModule,ReactiveFormsModule,UntypedFormBuilder,UntypedFormGroup} from '@angular/forms';
import {BwicService} from 'src/app/core/services/bwic.service';
import {NzModalModule} from 'ng-zorro-antd/modal';
import {WebsocketBwicService} from 'src/app/core/services/websocket-bwic.service';
import {NzMessageModule,NzMessageService} from 'ng-zorro-antd/message';
import {NzDescriptionsModule} from 'ng-zorro-antd/descriptions';
import {BidService} from 'src/app/core/services/bid.service';
import {NzDropDownModule} from 'ng-zorro-antd/dropdown';
import {UserService} from 'src/app/core/services/user.service';

interface ItemData {
  bwicId: string;
  issuer: string;
  cusip: string;
  startTime: string;
  dueTime: string;
  startPrice: number;
  size: number;
  rating?: any;
  coupon?: any;
  score:number;
  active: boolean
}

@Component({
  selector: 'app-bidding',
  standalone: true,
  imports: [CommonModule,NzInputModule,
    NzIconModule,NzButtonModule,
    NzFormModule,NzCardModule,
    NzTableModule,NzDividerModule,
    FormsModule,NzModalModule,ReactiveFormsModule,NzMessageModule,NzDescriptionsModule,NzDropDownModule],
  templateUrl: './bidding.component.html',
  styleUrls: ['./bidding.component.less']
})



export class BiddingComponent implements OnInit {
  constructor(private bwicService: BwicService,private fb: UntypedFormBuilder,private websocketBwicService: WebsocketBwicService,private message: NzMessageService,
    private bidService: BidService,private userService: UserService) {

  }
  isVisible=false;
  searchValue: string=''
  biddingCache: {[key: number]: {bidding: boolean; data: ItemData}}={};
  listOfData: ItemData[]=[];

  defaultData: ItemData[]=[];

  biddingData!: ItemData;

  price: string=''

  msg: string=''


  async submit() {
    const model={
      bwicId: this.biddingData!.bwicId+'',
      price: this.price
    }
    await this.bidService.setBids(model).toPromise()
    this.price=''
    this.message.success('Success！')
  }

  async startBidding(bwicData: ItemData): Promise<void> {
    let userId=''
    try {
      const id=(await this.userService.getById()) as {id: string}
      userId=id!.id
    } catch(error) {
      userId='1'
    }
    this.biddingData=bwicData
    const socket=this.websocketBwicService.connect();
    // 发送消息

    // 等待 WebSocket 连接建立
    socket.onopen=() => {
      // 发送消息
      this.websocketBwicService.send(JSON.stringify({
        "msgType": "BindUserId",
        "userId": userId
      }));
    };
    // 接收到消息的处理
    socket.onmessage=(event) => {
      const message=event.data
      message.includes('{')
      console.log('Received message:',message);
      console.log('includes:',message.includes('{'));
      if(message.includes('{')) {
        this.msg=JSON.parse(message).msg
      }
    };
    this.isVisible=true;
  }


  ngOnInit(): void {
    // let localdata=sessionStorage.getItem('data');
    // if(localdata) {
    //   let savedata=JSON.parse(localdata)
    //   this.listOfData=[...savedata]
    //   this.defaultData=[...savedata]
    // } else {
    //   this.getAllData();
    // }

    this.getAllData();
  }

  async getAllData() {
    const id=(await this.userService.getById()) as {id: string}
    let userId = id!.id
    console.log("11111111111111111111111111111111111111111111111111111111111111111111111")
    const data=(await this.bwicService.getHistory(userId)) as ItemData[]
    console.log("11111111111111111111111111111111111111111111111111111111111111111111111")
    console.log(data)
    if(data) {
      this.listOfData=[...data!]
      this.defaultData=[...data!]
      sessionStorage.setItem('data',JSON.stringify(this.listOfData))
    }

  }

  showModal(): void {
    this.isVisible=true;
  }

  handleOk(): void {
    console.log('Button ok clicked!');
    this.isVisible=false;
  }

  handleCancel(): void {
    console.log('Button cancel clicked!');
    // 关闭moadl同时关闭 WebSocket 连接
    this.websocketBwicService.disconnect();
    this.msg=''
    this.isVisible=false;
  }

  //bwicid
  bwicIdVisible: boolean=false;
  bwicIdSearchValue: string='';

  bwicIdSearch() {
    console.log(this.bwicIdSearchValue)
    // this.searchData(this.bwicIdSearchValue)
    this.listOfData=this.defaultData.filter(res => res.bwicId==this.bwicIdSearchValue)
  }

  bwicIdReset() {
    this.bwicIdSearchValue=''
    this.getAllData()

  }
  //Issuer
  issuerVisible: boolean=false;
  issuerSearchValue: string='';

  issuerSearch() {
    console.log(this.issuerSearchValue)
    this.listOfData=this.defaultData.filter(res => res.issuer==this.issuerSearchValue)

  }

  issuerReset() {
    this.issuerSearchValue=''
    this.getAllData()

  }
  //bwicid
  cusipVisible: boolean=false;
  cusipSearchValue: string='';

  cusipSearch() {
    console.log(this.cusipSearchValue)
    // this.searchData(this.cusipSearchValue)
    this.listOfData=this.defaultData.filter(res => res.cusip==this.cusipSearchValue)

  }

  cusipReset() {
    this.cusipSearchValue=''
    this.getAllData()
  }



}
