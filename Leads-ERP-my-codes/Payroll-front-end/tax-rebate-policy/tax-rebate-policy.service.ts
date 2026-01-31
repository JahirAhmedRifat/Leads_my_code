import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseApiService } from '../../../core/utils/service/base-api.service';
import { environment } from '../../../../environments/environment';
import {
  CommonResponseObject,
  CommonResponseList,
} from '../../../core/utils/model/common-response';

@Injectable({
  providedIn: 'root',
})
export class TaxRebatePolicyService extends BaseApiService<any> {
  constructor(private http: HttpClient) {
    super(http, environment.payrollEndPoint + 'tax-rebate-policy');
  }

  bulkSave(payload: any[]): Observable<any> {
    return this.http.post<any>(this._BASE_URL + '/bulkSave', payload);
  }

  updateSingleRecord(payload: any): Observable<any> {
    return this.http.put<any>(this._BASE_URL + '/update', payload);
  }

  search(payload: any): Observable<any> {
    return this.http.post<any>(this._BASE_URL + '/search', payload);
  }

  searchByCriteria(
    taxYear?: number,
    serialNumber?: string
  ): Observable<CommonResponseList<any>> {
    let params = new HttpParams();
    if (taxYear) params = params.set('taxYear', taxYear.toString());
    if (serialNumber) params = params.set('serialNumber', serialNumber);

    return this.http.get<CommonResponseList<any>>(
      `${this._BASE_URL}/search`,
      { params }
    );
  }

  // getListData(page: any, size: any): Observable<any> {
  //   return this.httpClient.get<any>(this._BASE_URL + '/' + page + '/' + size);
  // }

}
