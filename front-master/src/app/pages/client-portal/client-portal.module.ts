import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClientPortalRoutingModule } from './client-portal-routing.module';
import { BiddingComponent } from './bidding/bidding.component';
import { NgxEchartsModule } from 'ngx-echarts';


@NgModule({
  declarations: [
  ],
  imports: [
    CommonModule,
    ClientPortalRoutingModule,
    NgxEchartsModule 
  ]
})
export class ClientPortalModule { }
