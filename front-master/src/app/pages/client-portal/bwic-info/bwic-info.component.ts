import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzDrawerModule } from 'ng-zorro-antd/drawer';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzDatePickerModule } from 'ng-zorro-antd/date-picker';

@Component({
  selector: 'app-bwic-info',
  standalone: true,
  imports: [CommonModule,
    NzCardModule,
    NzDrawerModule,
    NzGridModule,
    NzInputModule,
    NzDatePickerModule],
  templateUrl: './bwic-info.component.html',
  styleUrls: ['./bwic-info.component.less']
})
export class BwicInfoComponent {


}
