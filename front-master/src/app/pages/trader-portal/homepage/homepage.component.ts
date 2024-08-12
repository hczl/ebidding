import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import * as echarts from 'echarts';
import { BwicService } from 'src/app/core/services/bwic.service';
import { NzCarouselModule } from 'ng-zorro-antd/carousel';

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
  imports: [CommonModule, NzCarouselModule],
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.less']
})
export class HomepageComponent implements OnInit {

  AllBwics: Bwics[] = [];
  AllBonds: Bonds[] = [];
  BondIdData: string[] = [];
  cusipData: String[] = [];
  SizeData: number[] = [];
  dataTable: String[] = [];
  IdData: number[] = [];
  CountsData: number[] = [];
  StartPriceData: number[] = [];
  PresentPriceData: number[] = [];
  constructor(private bwicService: BwicService) {
    // console.log(echarts)
  }

  ngOnInit() {
    this.bwicService.getAllBonds().subscribe((item: Bonds[]) => {
      this.AllBonds = item;
    })
    this.bwicService.getAllBwics().subscribe((data: Bwics[]) => {
      this.AllBwics = data;
      // 创建副本用于排序
      const sortedBwics = [...this.AllBwics].sort((a, b) => b.bidCounts - a.bidCounts);

      // 第一张表
      this.CountsData = sortedBwics.slice(0, 5).map(bwic => bwic.bidCounts).reverse();
      this.BondIdData = sortedBwics.slice(0, 5).map(bwic => bwic.bondId).reverse();
      this.SizeData = sortedBwics.slice(0, 5).map(bwic => bwic.size).reverse();
      this.cusipData = this.BondIdData.map(bondId => {
        const bond = this.AllBonds.find(b => b.bondId === bondId);
        return bond ? bond.cusip : '';
      });
      this.dataTable = this.cusipData.map((cusip, index) => {
        return cusip + ' & ' + this.SizeData[index];
      });
      // 第二张表
      this.IdData = this.AllBwics.map(bwic => bwic.bwicId);
      this.StartPriceData = this.AllBwics.map(bwic => bwic.startPrice);
      this.PresentPriceData = this.AllBwics.map(bwic => bwic.presentPrice);
      //必须在后端获取数据后调用下列方法来生成图表。
      this.Bar();
      this.initCharts();
    });

  }
  Bar() {
    const ec = echarts as any;
    let bar = ec.init(document.getElementById('bar'));
    let barOption = {
      title: {
        text: 'Popular BWIC'
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
      toolbox: {
        feature: {
          saveAsImage: {}
        }
      },
      xAxis: {
        type: 'value',
        boundaryGap: [0, 0.01]
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
            color: '#2b473e'
          }
        },
      ]
    };
    bar.setOption(barOption);
  }
  initCharts() {
    const ec = echarts as any;
    let lineChart = ec.init(document.getElementById('lineChart'));
    let lineChartOption = {
      title: {
        text: 'Price Difference'
      },
      tooltip: {
        trigger: 'axis'
      },
      toolbox: {
        feature: {
          saveAsImage: {}
        }
      },
      legend: {
        padding: 0
      },
      xAxis: [
        {
          type: 'category',
          boundaryGap: false,
          data: this.IdData,
          name: 'BwicId'
        }
      ],
      yAxis: [
        {
          type: 'value'
        }
      ],
      series: [
        {
          name: 'StartPrice',
          type: 'line',
          smooth: true,
          lineStyle: {
            color: '#ff713a'
          },
          data: this.StartPriceData
        },
        {
          name: 'PresentPrice',
          type: 'line',
          smooth: true,
          lineStyle: {
            color: '#1ab394'
          },
          data: this.PresentPriceData
        }
      ]
    };
    lineChart.setOption(lineChartOption);
  }
}

