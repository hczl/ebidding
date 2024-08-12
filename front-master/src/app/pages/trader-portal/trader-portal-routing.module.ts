import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminComponent } from './admin/admin.component';
import { BondComponent } from './bond/bond.component';
import { HomepageComponent } from './homepage/homepage.component';
import { TraderLayoutComponent } from './trader-layout/trader-layout.component';

const routes: Routes = [
  {
    path: '',
    component: TraderLayoutComponent, // use your layout component here
    children: [
      {path: '', redirectTo: 'homepage', pathMatch: 'full',data: { expectedRole: 'trader' } }, // redirect to `homepage`
      {path: 'homepage', component: HomepageComponent,data: { expectedRole: 'trader' }},
      {path:'admin', component: AdminComponent,data: { expectedRole: 'trader' }},
      {path:'bond', component: BondComponent,data: { expectedRole: 'trader' }},
    ]
  }
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TraderPortalRoutingModule { }
