import { Injectable } from '@angular/core';
import { BaseApiService } from '../../../core/utils/service/base-api.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BonusDisbursementService extends BaseApiService<any> {

  constructor(httpClient: HttpClient) {
    super(
      httpClient,
      environment.payrollEndPoint + 'bonus-disbursement',
    );
  }

  bulkSave(payload: any[]): Observable<any> {
    return this.httpClient.post<any>(
      `${this._BASE_URL}/bulk-save`,
      payload
    );
  }

  criteriaSearch(payload: any): Observable<any> {
    return this.httpClient.post<any>(
      `${this._BASE_URL}/search`,
      payload
    );
  }

}