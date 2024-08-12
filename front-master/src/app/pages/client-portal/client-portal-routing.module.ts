import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { BiddingComponent } from './bidding/bidding.component';
import { HistoryComponent } from './history/history.component';

import { HomepageComponent } from './homepage/homepage.component';
import { BwicInfoComponent } from './bwic-info/bwic-info.component';
import { ClientLayoutComponent } from './client-layout/client-layout.component';

const routes: Routes = [
  {
    path: '',
    component: ClientLayoutComponent, // use your layout component here
    children: [
      { path: '', redirectTo: 'homepage', pathMatch: 'full',data: { expectedRole: 'client' } }, // redirect to `homepage`
      { path: 'homepage', component: HomepageComponent,data: { expectedRole: 'client' }},
      { path: 'bidding', component: BiddingComponent ,data: { expectedRole: 'client' }},
      { path: 'history', component: HistoryComponent ,data: { expectedRole: 'client' }},
      { path: 'bwic-info', component: BwicInfoComponent ,data: { expectedRole: 'client' }}
    ]
  }
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClientPortalRoutingModule { }
