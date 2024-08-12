import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzBadgeModule } from 'ng-zorro-antd/badge';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { BwicService } from 'src/app/core/services/bwic.service';
import { FormsModule, ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { NzDrawerModule } from 'ng-zorro-antd/drawer';
import { NzModalModule } from 'ng-zorro-antd/modal';


interface BwicItemData {
  bwicId: string;
  issuer: string;
  cusip: string;
  startTime: string;
  dueTime: string;
  startPrice: number;
  size: number;
  result:string;
  expand: boolean;
}

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule,
    NzInputModule,
    NzIconModule,
    NzButtonModule,
    NzTableModule,
    NzBadgeModule,
    NzDropDownModule,
    NzDividerModule,
    NzGridModule,
    FormsModule,
    NzDrawerModule,
    NzModalModule],
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.less']
})

export class HistoryComponent implements OnInit {
  isVisible = false;
  constructor(private bwicService: BwicService,
    private fb: UntypedFormBuilder,) { }

  //searchValue = '';
  searchvisible = false;
  drawervisible = false

  editCache: { [key: number]: { edit: boolean; data: BwicItemData } } = {};
  listOfBwicData: BwicItemData[] = [];
  defaultData:BwicItemData[] = [];
  //listOfDisplayData = [...this.listOfBwicData];

  detailsData!: BwicItemData

  // reset(): void {
  //   this.searchValue = '';
  //   this.searchbwicId()
  // }

  // searchbwicId(): void {
  //   this.searchvisible = false;
  //   this.listOfDisplayData = this.listOfBwicData.filter((item: BwicItemData) => item.bwicId.indexOf(this.searchValue) !== -1);
  // }

  validateForm!: UntypedFormGroup;

  price: string = ''

  dataSet: Data[] = []

  async ngOnInit(): Promise<void> {
    this.getAllData();
    this.validateForm = this.fb.group({
      bwicId: [null],
      issuer: [null],
      cusip: [null],
      startTime: [null],
      dueTime: [null],
      startPrice: [null],
      size: [null],
      expand: false
    });
  }

  async getAllData() {
    const data = (await this.bwicService.getBwicByAccountId()) as BwicItemData[]
    this.listOfBwicData = [...data!]
    this.defaultData = [...data!]
    console.log(this.listOfBwicData, 'this.listOfBwicData')
  }

  async open(id: string): Promise<void> {
    // 目前只有一个数据使用push展示
    // let data:Data[] = []
    const value = await this.bwicService.getBidByBwicIdAndAccountId(id)
    // data.push(value as Data)
    // this.dataSet = [...data]
    // 如果后端返回的是一些数据，数组需要
    this.dataSet = [...value as Data[] ]
    // console.log(data)
    this.drawervisible = true;
  //   this.ngOnInit() {
  //     this.bwicService.getBidByBwicIdAndAccountId(id).subscribe((data: Bids[]) => {
  //     this.listOfBwicData = data.map(bond => {
  //       const maturityDate = new Date(bond.maturityDate as string); // 将字符串转为日期对象
  //     //           // 格式化日期字符串
  //           const formattedDate = `${maturityDate.getFullYear()}-${(maturityDate.getMonth() + 1)
  //            .toString()
  //            .padStart(2, '0')}-${maturityDate
  //        .getDate()
  //        .toString()
  //        .padStart(2, '0')} 00:00:00`;
  //        return {
  //        ...bond,
  //        maturityDate: formattedDate
  //       };
  //      });
  //     });
  //   }
  }

   resultText:string = ''

    async result(id: string) {
      // 目前只有一个数据使用push展示
      console.log(id, 'id')
      this.isVisible = true;
      this.resultText = (await this.bwicService.getMyBwicResult(id)) as string;
    }

  close(): void {
    this.drawervisible = false;
  }

  //bwicid
  bwicIdVisible: boolean = false;
  bwicIdSearchValue: string = '';

  bwicIdSearch() {
    console.log(this.bwicIdSearchValue)
    // this.searchData(this.bwicIdSearchValue)
    this.listOfBwicData = this.defaultData.filter(res => res.bwicId == this.bwicIdSearchValue)
  }

  bwicIdReset() {
    this.bwicIdSearchValue = ''
    this.getAllData()

  }
  //Issuer
  issuerVisible: boolean = false;
  issuerSearchValue: string = '';

  issuerSearch() {
    console.log(this.issuerSearchValue)
    this.listOfBwicData = this.defaultData.filter(res => res.issuer == this.issuerSearchValue)

  }

  issuerReset() {
    this.issuerSearchValue = ''
    this.getAllData()

  }
  //bwicid
  cusipVisible: boolean = false;
  cusipSearchValue: string = '';

  cusipSearch() {
    console.log(this.cusipSearchValue)
    // this.searchData(this.cusipSearchValue)
    this.listOfBwicData = this.defaultData.filter(res => res.cusip == this.cusipSearchValue)

  }

  cusipReset() {
    this.cusipSearchValue = ''
    this.getAllData()
  }

  showModal(): void {
    this.isVisible = true;
  }

  handleOk(): void {
    console.log('Button ok clicked!');
    this.isVisible = false;
  }

  handleCancel(): void {
    console.log('Button cancel clicked!');
    this.isVisible = false;
  }



}

interface Data {
  accountId: number,
  bidId: number,
  bwicId: number,
  price: number,
  ranking: number,
  time: string
}
