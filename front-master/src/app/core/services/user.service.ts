import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private authService: AuthService,private http: HttpClient) { }

  checkPermission(){
    this.authService.check();
  }

  getUser(){
    if(this.authService.check()){
      return 'user';
    }
    return null;
  }

  getById(){
    // api/v1/account-service/accounts/getCurrentAccount
    // "/account": {
    //   "target": "http://localhost:8080",
    //   "secure": false,
    //   "changeOrigin": true,
    //   "pathRewrite": {
    //     "^/account": "/api/v1/account-service"
    //   }
    return this.http.get('/api/v1/account-service/accounts/getCurrentAccount').toPromise()
  }

}