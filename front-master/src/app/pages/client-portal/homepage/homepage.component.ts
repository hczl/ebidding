import {CommonModule} from '@angular/common';
import {NzInputModule} from 'ng-zorro-antd/input';
import {NzIconModule} from 'ng-zorro-antd/icon';
import {NzButtonModule} from 'ng-zorro-antd/button';
import {NzFormModule} from 'ng-zorro-antd/form';
import {NzCardModule} from 'ng-zorro-antd/card';
import {NzTableModule} from 'ng-zorro-antd/table';
import {NzDividerModule} from 'ng-zorro-antd/divider';
import {AfterViewInit,Component,ElementRef,Input,OnDestroy,OnInit,ViewChild} from '@angular/core';
import * as echarts from 'echarts';
import {NzCarouselModule} from 'ng-zorro-antd/carousel';
import {BwicService} from 'src/app/core/services/bwic.service';
import {MatCardModule} from '@angular/material/card';
import {MatDividerModule} from '@angular/material/divider';
import {MatIconModule} from '@angular/material/icon';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatInputModule} from '@angular/material/input';
import {GptService} from 'src/app/core/services/gpt.service';
import {ProgressBarMode} from '@angular/material/progress-bar';
import {Subscription} from 'rxjs';
import {MatTabsModule} from '@angular/material/tabs';


export interface Bwics {
  bwicId: number,
  bondId: string,
  size: number,
  startPrice: number,
  presentPrice: number,
  startTime: string,
  dueTime: string,
  lastBidTime: string,
  bidCounts: number,
}

export interface Bonds {
  bondId: String;
  coupon: String;
  cusip: String;
  issuer: String;
  maturityDate: String;
  rating: String;
  transaction_counts: Number;
}

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [CommonModule,NzInputModule,
    NzIconModule,NzButtonModule,
    NzFormModule,NzCardModule,
    NzTableModule,NzDividerModule,
    MatCardModule,NzCarouselModule,
    MatDividerModule,MatIconModule,
    MatProgressBarModule,MatInputModule,MatTabsModule],
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.less']
})


export class HomepageComponent implements OnInit {
  //----------gpt-----------
  gptResponse: string='';
  message: string='';
  hover: boolean=false;

  defaultIconType: string='sentiment_very_satisfied';  // default icon
  queryIconType: string='auto_mode';  // query icon
  defaultProgressBarMode: ProgressBarMode='determinate';
  queryProgressBarMode: ProgressBarMode='query';

  // Set initial iconType and progressBarMode to default
  submitIconType: string=this.defaultIconType;
  progressBarMode: ProgressBarMode=this.defaultProgressBarMode;

  private subscription!: Subscription;

  /* ==============新加的 */
  selectedIndex: any=1;

  sendMessage(message: string): void {
    //清空上一次的聊天记录
    this.gptResponse='';


    // Switch to query icon and progress bar mode when sending a message
    this.submitIconType=this.queryIconType;
    this.progressBarMode=this.queryProgressBarMode;

    this.subscription=this.gptService.traderChatWithGpt(message).subscribe(
      (event: MessageEvent) => {
        // Parse the response data
        const eventData=JSON.parse(event.data);
        const content=eventData.choices[0]?.delta?.content||'';

        // Append the content to gptResponse
        this.gptResponse+=content;

        // Switch back to default icon and progress bar mode when response is received
        this.submitIconType=this.defaultIconType;
        this.progressBarMode=this.defaultProgressBarMode;
      },
      err => {
        // Handle error
        this.gptResponse='An error occurred. Please try again.';

        // Switch back to default icon and progress bar mode when an error occurs
        this.submitIconType=this.defaultIconType;
        this.progressBarMode=this.defaultProgressBarMode;
      }
    );
  }




  //------------------------


  AllBwics: Bwics[]=[];
  AllBonds: Bonds[]=[];

  //Popular Bwic
  BondIdData: string[]=[];
  cusipData: String[]=[];
  SizeData1: number[]=[];
  dataTable: String[]=[];

  //Uncoming Bwic
  CountsData: number[]=[];
  BondIdData1: string[]=[];
  cusipData1: String[]=[];
  SizeData2: number[]=[];
  StartPriceData: number[]=[];
  constructor(private bwicService: BwicService,private gptService: GptService) {}

  ngOnInit() {
    this.bwicService.getAllBonds().subscribe((item: Bonds[]) => {
      this.AllBonds=item;
    })
    this.bwicService.getAllBwics().subscribe((data: Bwics[]) => {
      this.AllBwics=data;

      //Popular Bwic
      const currentTime=new Date();
      const filteredBwics1=this.AllBwics.filter(bwic => {
        const startTime=new Date(bwic.startTime);
        const dueTime=new Date(bwic.dueTime);
        return startTime<=currentTime&&currentTime<=dueTime;
      });
      const sortedBwics=[...filteredBwics1].sort((a,b) => b.bidCounts-a.bidCounts);
      const topFiveBwics=sortedBwics.slice(0,5).reverse();
      this.CountsData=topFiveBwics.map(bwic => bwic.bidCounts);
      this.SizeData1=topFiveBwics.map(bwic => bwic.size);
      this.BondIdData=topFiveBwics.map(bwic => bwic.bondId);
      this.cusipData=this.BondIdData.map(bondId => {
        const bond=this.AllBonds.find(b => b.bondId===bondId);
        return bond? bond.cusip:'';
      });
      this.dataTable=this.cusipData.map((cusip,index) => {
        return cusip+' & '+this.SizeData1[index];
      });

      //Uncoming Bwic
      const filteredBwics2=this.AllBwics.filter(bwic => {
        const startTime=new Date(bwic.startTime);
        return startTime>currentTime;
      });
      this.BondIdData1=filteredBwics2.map(bwic => bwic.bondId);
      this.cusipData1=this.BondIdData1.map(bondId => {
        const bond=this.AllBonds.find(b => b.bondId===bondId);
        return bond? bond.cusip:'';
      });
      this.SizeData2=filteredBwics2.map(bwic => bwic.size);
      this.StartPriceData=filteredBwics2.map(bwic => bwic.startPrice);


    });

  }
  onTabChange(index: number): void {
    console.log(`Tab changed to index: ${index}`);
    // You can add your logic here based on the selected tab index
    if(index==1) {
      let _this=this;
      setTimeout(function() {
        _this.Bar();
        _this.linechart();
      },1000)
    }
  }
  Bar() {
    const ec=echarts as any;
    let bar=ec.init(document.getElementById('bar'));
    let barOption={
      title: {
        text: 'Popular Ongoing BWIC'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      legend: {},
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      // toolbox: {
      //     feature: {
      //         saveAsImage: {}
      //     }
      // },
      xAxis: {
        type: 'value',
        boundaryGap: [0,0.01]
      },
      yAxis: {
        type: 'category',
        data: this.dataTable,
        name: 'Cusip&Size'
      },
      series: [
        {
          name: 'TotalCounts',
          type: 'bar',
          data: this.CountsData,
          itemStyle: {
            color: '#c0c7d9'
          }
        },
      ]
    };
    bar.setOption(barOption);
  }
  linechart() {
    const ec=echarts as any;
    let lineChart=ec.init(document.getElementById('lineChart'));
    let option={
      title: {
        text: 'Upcoming BWIC',
      },
      tooltip: {},
      legend: {
        data: ['Size','StartPrice'],
      },
      xAxis: {
        name: 'Cusip',
        data: this.cusipData1,
      },
      yAxis: [
        {
          name: 'Size',
        },
        {
          name: 'StartPrice',
          axisLine: {
            show: false,
          },
          axisTick: {
            show: false,
          },
          splitLine: {
            show: false,
          },
        },
      ],
      series: [
        {
          name: 'Size',
          type: 'line',
          yAxisIndex: 0, // 使用第一个Y轴
          data: this.SizeData2,
        },
        {
          name: 'StartPrice',
          type: 'line',
          yAxisIndex: 1, // 使用第二个Y轴
          data: this.StartPriceData,
        },
      ],
    };
    lineChart.setOption(option);
  }
}