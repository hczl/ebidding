import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuardService } from 'src/app/core/guard/auth-guard-service.service';
import { LoginComponent } from './pages/login/login.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'client',
    loadChildren: () => import('./pages/client-portal/client-portal.module').then(m => m.ClientPortalModule),
    canActivate: [AuthGuardService], // 添加这一行来应用守卫
    data: { expectedRole: 'client' }
  },
  {
    path: 'trader',
    loadChildren: () => import('./pages/trader-portal/trader-portal.module').then(m => m.TraderPortalModule),
    canActivate: [AuthGuardService], // 添加这一行来应用守卫
    data: { expectedRole: 'trader' }
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' }, // 默认重定向到 login 路由
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
