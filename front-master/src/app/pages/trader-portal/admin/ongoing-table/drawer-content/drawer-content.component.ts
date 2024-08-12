// drawer-content.component.ts

import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NzTableModule, NzTableSortFn, NzTableSortOrder } from 'ng-zorro-antd/table';
import { BidRankItemData } from '../ongoing-table.component';

interface ColumnItem {
  name: string;
  sortOrder: NzTableSortOrder | null;
  sortFn: NzTableSortFn<BidRankItemData> | null;
}

@Component({
  selector: 'app-drawer-content',
  standalone: true,
  imports: [CommonModule, NzTableModule],
  templateUrl: './drawer-content.component.html',
  styleUrls: ['./drawer-content.component.less']
})

export class DrawerContentComponent {
  @Input() data: BidRankItemData[] = [];

  listOfColumns: ColumnItem[] = [
    {
      name: 'Rank',
      sortOrder: 'ascend',
      sortFn: (a: BidRankItemData, b: BidRankItemData) => a.ranking - b.ranking,
    },
    {
      name: 'Price',
      sortOrder: null,
      sortFn: (a: BidRankItemData, b: BidRankItemData) => a.price - b.price,
    },
    {
      name: 'Account ID',
      sortOrder: null,
      sortFn: (a: BidRankItemData, b: BidRankItemData) => String(a.accountId).localeCompare(String(b.accountId)),
    },
    {
      name: 'Account Name',
      sortOrder: null,
      sortFn: (a: BidRankItemData, b: BidRankItemData) => a.accountName.localeCompare(b.accountName),
    },
    {
      name: 'Transaction Time',
      sortOrder: null,
      sortFn: (a: BidRankItemData, b: BidRankItemData) => a.time.localeCompare(b.time),
    }
  ];

  trackByName(_: number, item: ColumnItem): string {
    return item.name;
  }

  ngOnInit(): void {
    console.log(this.data);
  }
}
