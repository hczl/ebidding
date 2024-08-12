import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzDrawerModule } from 'ng-zorro-antd/drawer';
import { NzDrawerService } from 'ng-zorro-antd/drawer';
import { DrawerContentComponent } from './drawer-content/drawer-content.component';
import { BwicService } from 'src/app/core/services/bwic.service';
import { BidService } from 'src/app/core/services/bid.service';



export interface BidRankItemData {
  ranking: number;
  accountId: number;
  accountName: string;
  price: number;
  time: string;
}

export interface ParentItemData {
  bwicId: number;
  bondId: string;
  cusip: string;
  issuer: string;
  size: number;
  startPrice: number;
  maxPrice: number;
  startTime: string;
  dueTime: string;
  lastBidTime: string;
  bidCounts: number;
  expand: boolean;
  children: BidRankItemData[];
}





@Component({
  selector: 'app-ongoing-table',
  standalone: true,
  imports: [
    CommonModule,
    NzTableModule,
    NzDrawerModule
  ],
  templateUrl: './ongoing-table.component.html',
  styleUrls: ['./ongoing-table.component.less']
})


export class OngoingTableComponent {


  constructor(private drawerService: NzDrawerService, private bwicService: BwicService, private bidService: BidService) { }

  isLoading: boolean = false;
  selectedRow: ParentItemData | null = null;

  @Input() data: any[] = [];
  listOfParentData: ParentItemData[] = [];
  listOfBidRankData: BidRankItemData[] = [];

  ngOnInit(): void {
    console.log("ongoing-table.component.ts: ngOnInit(): void");

    this.getBwics();
  }

  getBwics(): void {
    this.isLoading = true;
    this.bwicService.getOngoingBwics().subscribe(data => {
      this.listOfParentData = data;
      this.isLoading = false;
    });
  }



  onRowClick(data: ParentItemData): void {
    this.selectedRow = data;
    console.log('Row clicked: ', data);

    // 获取数据
    this.bidService.getAllBidRankingsByBwicId(data.bwicId).subscribe(bidRankData => {
      const drawerRef = this.drawerService.create({
        nzTitle: 'Detailed information',
        nzPlacement: 'right',
        nzWidth: '80%',  // 宽度设为屏幕的 80%
        nzBodyStyle: {   // 自定义抽屉的样式
          padding: '20px',
          backgroundColor: '#f0f2f5', // 设置背景颜色为淡灰色
        },
        nzContent: DrawerContentComponent,
        nzContentParams: {
          data: bidRankData  // 使用从服务中获得的数据
        }
      });
    });
  }
}






