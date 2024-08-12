import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, filter, map ,tap} from 'rxjs';

export interface APIResult<T> {
  code: string;
  message: string;
  success: boolean;
  /** auth token */
  token: string;
  data: T;
}

export interface UserInfo {
  id?: string;
  name: string;
  memberSince?: string;
  role: string;
  token?: string;
}


@Injectable({
  providedIn: 'root'
})
export class AccountService {

  constructor(private http: HttpClient) { }

  // login(params: { username: string; password: string }): Observable<UserInfo> {
  //   return this.http.post<APIResult<UserInfo>>('/api/v1/account-service/accounts/login', params)
  //   .pipe(
  //     tap(res => console.log(res)), // 添加这一行
  //     filter(res => res.success),
  //     map(res =>res.data));
  // }

  login(params: { username: string; password: string }): Observable<UserInfo> {
    return this.http.post<UserInfo>('/api/v1/account-service/accounts/login', params);
  }

}
