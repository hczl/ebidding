import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {NZ_I18N} from 'ng-zorro-antd/i18n';
import {en_US} from 'ng-zorro-antd/i18n';
import {registerLocaleData} from '@angular/common';
import en from '@angular/common/locales/en';
import {FormsModule} from '@angular/forms';
import {HttpClientModule,HTTP_INTERCEPTORS} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AppRoutingModule} from './app-routing.module';
import {IconsProviderModule} from './icons-provider.module';
import {NzLayoutModule} from 'ng-zorro-antd/layout';
import {NzMenuModule} from 'ng-zorro-antd/menu';
import {NzFormModule} from 'ng-zorro-antd/form';
import {NzInputModule} from 'ng-zorro-antd/input';
import {NzButtonModule} from 'ng-zorro-antd/button';
import {NzGridModule} from 'ng-zorro-antd/grid';
import {NzCheckboxModule} from 'ng-zorro-antd/checkbox';
import {ReactiveFormsModule} from '@angular/forms';
import {NzTabsModule} from 'ng-zorro-antd/tabs';
import {NzSpinModule} from 'ng-zorro-antd/spin';
import {AuthInterceptor} from './core/interceptor/auth.interceptor';
import {NgxEchartsModule} from 'ngx-echarts';
import {NzMessageModule} from 'ng-zorro-antd/message';
import {MatMenuModule} from '@angular/material/menu';
import {MatButtonModule} from '@angular/material/button';

registerLocaleData(en);

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    IconsProviderModule,
    NzLayoutModule,
    NzMenuModule,
    NzFormModule,
    NzInputModule,
    NzButtonModule,
    NzGridModule,
    NzCheckboxModule,
    ReactiveFormsModule,
    NzTabsModule,
    NzSpinModule,
    NgxEchartsModule,
    NzMessageModule,
    MatButtonModule,MatMenuModule

  ],
  providers: [
    {provide: NZ_I18N,useValue: en_US},
    {provide: HTTP_INTERCEPTORS,useClass: AuthInterceptor,multi: true},
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
