import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NzTableModule, NzTableSortFn, NzTableSortOrder } from 'ng-zorro-antd/table';
import { BwicService } from 'src/app/core/services/bwic.service';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { saveAs } from 'file-saver';

interface ColumnItem {
  name: string;
  sortOrder: NzTableSortOrder | null;
  sortFn: NzTableSortFn<BwicEndedRecordResponseDTO> | null;
}

export interface BwicEndedRecordResponseDTO {
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
}


@Component({
  selector: 'app-ended-table',
  standalone: true,
  imports: [CommonModule,
    NzTableModule,
    NzButtonModule
  ],
  templateUrl: './ended-table.component.html',
  styleUrls: ['./ended-table.component.less'],
  providers: [BwicService] 
})

export class EndedTableComponent {
  @Input() data: BwicEndedRecordResponseDTO[] = [];

  listOfColumns: ColumnItem[] = [
    {
      name: 'BWIC ID',
      sortOrder: null,
      sortFn: (a: BwicEndedRecordResponseDTO, b: BwicEndedRecordResponseDTO) => a.bwicId - b.bwicId,
    },
    {
      name: 'Bond ID',
      sortOrder: null,
      sortFn: (a: BwicEndedRecordResponseDTO, b: BwicEndedRecordResponseDTO) => a.bondId.localeCompare(b.bondId),
    },
    {
      name: 'CUSIP',
      sortOrder: null,
      sortFn: (a: BwicEndedRecordResponseDTO, b: BwicEndedRecordResponseDTO) => a.cusip.localeCompare(b.cusip),
    },
    {
      name: 'Issuer',
      sortOrder: null,
      sortFn: (a: BwicEndedRecordResponseDTO, b: BwicEndedRecordResponseDTO) => a.issuer.localeCompare(b.issuer),
    },
    {
      name: 'Size',
      sortOrder: null,
      sortFn: (a: BwicEndedRecordResponseDTO, b: BwicEndedRecordResponseDTO) => a.size - b.size,
    },
    {
      name: 'Start Price',
      sortOrder: null,
      sortFn: (a: BwicEndedRecordResponseDTO, b: BwicEndedRecordResponseDTO) => a.startPrice - b.startPrice,
    },
    {
      name: 'Max Price',
      sortOrder: null,
      sortFn: (a: BwicEndedRecordResponseDTO, b: BwicEndedRecordResponseDTO) => a.maxPrice - b.maxPrice,
    },
    {
      name: 'Start Time',
      sortOrder: null,
      sortFn: (a: BwicEndedRecordResponseDTO, b: BwicEndedRecordResponseDTO) => a.startTime.toString().localeCompare(b.startTime.toString()),
    },
    {
      name: 'Due Time',
      sortOrder: null,
      sortFn: (a: BwicEndedRecordResponseDTO, b: BwicEndedRecordResponseDTO) => a.dueTime.toString().localeCompare(b.dueTime.toString()),
    },
    {
      name: 'Last Bid Time',
      sortOrder: null,
      sortFn: (a: BwicEndedRecordResponseDTO, b: BwicEndedRecordResponseDTO) => a.lastBidTime.toString().localeCompare(b.lastBidTime.toString()),
    },
    {
      name: 'Bid Counts',
      sortOrder: null,
      sortFn: (a: BwicEndedRecordResponseDTO, b: BwicEndedRecordResponseDTO) => a.bidCounts - b.bidCounts,
    }
];

  trackByName(index: number, _: ColumnItem): number {
    return index;
}

constructor(private bwicService: BwicService) { }


ngOnInit(): void {
  this.bwicService.getEndedBwics().subscribe(
    (data: BwicEndedRecordResponseDTO[]) => {
      this.data = data;
    }
  );
}

downloadFile(): void {
  this.downloadTableData();
}


public downloadTableData(): void {
  const data = this.data.map(row => ({
    ...row,
    startTime: new Date(row.startTime).toLocaleString(),
    dueTime: new Date(row.dueTime).toLocaleString(),
    lastBidTime: new Date(row.lastBidTime).toLocaleString(),
  }));

  const csv = this.convertToCSV(data);
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
  saveAs(blob, 'tableData.csv');
}


private convertToCSV(objArray: any[]): string {
  const array = typeof objArray != 'object' ? JSON.parse(objArray) : objArray;
  let str = `${Object.keys(array[0]).map(value => `"${value}"`).join(",")}` + '\r\n';

  return array.reduce((str:string, next:string) => {
    str += `${Object.values(next).map(value => `"${value}"`).join(",")}` + '\r\n';
    return str;
  }, str);
}


}
