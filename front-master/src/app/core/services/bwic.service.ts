import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {ParentItemData } from 'src/app/pages/trader-portal/admin/ongoing-table/ongoing-table.component';
import { BwicUpcomingRecord } from 'src/app/pages/trader-portal/admin/upcoming-table/upcoming-table.component';
import { BwicEndedRecordResponseDTO } from 'src/app/pages/trader-portal/admin/ended-table/ended-table.component';
import { Observable,map } from 'rxjs';


export interface BwicUpcomingFullRecord {
  bwicId: string;
  bondId: string;
  size: number;
  startPrice: number;
  startTime: string;
  dueTime: string;
}

export interface Bwics{
  bwicId: number,
  bondId: string,
  size: number,
  startPrice: number,
  presentPrice: number,
  startTime: string,
  dueTime: string,
  lastBidTime: string,
  bidCounts: number
}

export interface Bonds{
  bondId: String;
  coupon: String;
  cusip: String;
  issuer: String;
  maturityDate: String;
  rating: String;
  transaction_counts: Number;
}

export interface newBwic{
  bond_id:string;
  startPrice:string;
  startTime:string;
  dueTime:string;
  size:string;
}

@Injectable({
  providedIn: 'root'
})
export class BwicService {

  constructor(private http: HttpClient) {}


  getHistory(userId: string){
    console.log("2222222222222222222222");
    return this.http
      .get(`/api/v1/bwic-service/bwics/history/${userId}`).toPromise();
  }
  getBwicByAccountId(){
    return this.http
    .get(`/api/v1/bwic-service/bwics/getBwicByAccountId`).toPromise();
  }

  getBidByBwicIdAndAccountId(id:string){
    return this.http
    .get(`/api/v1/bid-service/getBidByBwicIdAndAccountId/`+id).toPromise();
  }

  getMyBwicResult(bwicId: string){

      const apiUrl = `api/v1/bwic-service/bwics/${bwicId}/getMyBwicResult`;

      return this.http.get(apiUrl, {  responseType: 'text' }).toPromise()
    }

  getAllBonds(): Observable<Bonds[]>{
    const apiUrl = `api/v1/bwic-service/bwics/Allbonds`;
    return this.http.get<Bonds[]>(apiUrl);
  }

  getAllBwics(): Observable<Bwics[]>{
    const apiUrl = `api/v1/bwic-service/bwics/Allbwics`;
    return this.http.get<Bwics[]>(apiUrl);
  }

  createBwic(params : {
    bondId:string;
    startPrice:string;
    startTime:string;
    dueTime:string;
    size:string }):Observable<newBwic>{
      const apiUrl = `api/v1/bwic-service/bwics`;
      return this.http.post<newBwic>(apiUrl, params);
  }


    //---------------向后端请求ongoing的数据----------------
    getOngoingBwics(): Observable<ParentItemData[]> {
      const apiUrl = '/api/v1/bwic-service/bwics/ongoing';
      console.log("bwic.service.ts: getOngoingBwics(): apiUrl = " + apiUrl);
      return this.http.get<ParentItemData[]>(apiUrl).pipe(
        map((data: ParentItemData[]) =>
          data.map(item => ({
            ...item,   // 拷贝 item 的所有字段
            expand: false,  // 默认设置为false
            children: item.children.map(child => ({
              ...child,    // 拷贝 child 的所有字段
            }))
          }))
        )
      );
    }

    //---------------向后端请求ongoing的数据----------------



      //---------------向后端请求Upcoming的数据----------------
getUpcomingBwics(): Observable<BwicUpcomingRecord[]> {
  const apiUrl = '/api/v1/bwic-service/bwics/upcoming';
  console.log("bwic.service.ts: getUpcomingBwics(): apiUrl = " + apiUrl);
  return this.http.get<BwicUpcomingRecord[]>(apiUrl).pipe(
    map((data: BwicUpcomingRecord[]) =>
      data.map(item => ({
        ...item,   // 拷贝 item 的所有字段
      }))
    )
  );
}
  //---------------向后端请求upComing的数据----------------


//向后端更新upcoming的一个数据
updateBwicUpcomingFullRecord(bwicId: string, record: BwicUpcomingFullRecord): Observable<any> {
  const apiUrl = `api/v1/bwic-service/bwics/${bwicId}/full-record`;
  return this.http.put(apiUrl, record);
}
//向后端更新upcoming的一个数据


//向后端删除upcoming的一个数据
deleteBwicUpcomingRecord(bwicId: string): Observable<any> {
  const apiUrl = `api/v1/bwic-service/bwics/${bwicId}`;
  return this.http.delete(apiUrl);
}
//向后端删除upcoming的一个数据




// ---------------向后端请求ended的数据---------------

getEndedBwics(): Observable<BwicEndedRecordResponseDTO[]> {
  const apiUrl = `api/v1/bwic-service/bwics/ended`;
  console.log("bwic.service.ts: getEndedBwics(): apiUrl = " + apiUrl);
  return this.http.get<BwicEndedRecordResponseDTO[]>(apiUrl);
}

// ---------------向后端请求ended的数据---------------

  getBidding(value:string){
    return this.http
      .get(`/api/v1/bwic-service/bwics/${value}/bycusip`);
  }



}
