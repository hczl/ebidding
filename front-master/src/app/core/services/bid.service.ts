import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BidRankItemData } from 'src/app/pages/trader-portal/admin/ongoing-table/ongoing-table.component';

@Injectable({
  providedIn: 'root'
})
export class BidService {

  constructor(private http: HttpClient) {}

  setBids(model:{bwicId:string,price:string}){
    return this.http.post('/api/v1/bid-service/bids',model)
  }


  getAllBidRankingsByBwicId(bwicId: number): Observable<BidRankItemData[]> {
    return this.http.get<BidRankItemData[]>(`/api/v1/bid-service/bwics/${bwicId}/ongoing-all-items`);
  }
}
