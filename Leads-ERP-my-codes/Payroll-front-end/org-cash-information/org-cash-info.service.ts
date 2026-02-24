import { Injectable } from '@angular/core';
import { BaseApiService } from '../../../core/utils/service/base-api.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import {Observable} from 'rxjs';
import {CommonResponseObject} from '../../../core/utils/model/common-response';

@Injectable({
  providedIn: 'root',
})
export class OrgCashInfoService extends BaseApiService<any> {

  constructor(httpClient: HttpClient) {
    super(
      httpClient,
      environment.payrollEndPoint + 'org-cash-information',
    );
  }

  criteriaSearch(payload: any): Observable<any> {
    return this.httpClient.post<any>(
      `${this._BASE_URL}/search`,
      payload
    );
  }
  
}
