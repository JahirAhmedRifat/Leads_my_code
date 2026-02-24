import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { BaseApiService } from '../../../core/utils/service/base-api.service';
import { Observable } from 'rxjs';
import { CommonResponseObject } from '../../../core/utils/model/common-response';

export interface organizationBankInfoRequest {
  oranization: number;
  orgBranch?: number;
  bankName: number;
  branchName: number;
  rouytingNumber: number;
  accountName: string;
  accountNumber: number;
  accountType: number;
  currency: number;
  paymentMethod: number;
  status: number;
  isDeleted?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class OrganizationBankInfoService extends BaseApiService<any> {

  constructor(httpClient: HttpClient) {
    super(httpClient, environment.payrollEndPoint + 'org-bank-information');
  }

  deleteOrganizationBankInfo(uuid: string): Observable<CommonResponseObject<any>> {
    return this.delete(uuid);
  }

  search(payload: any): Observable<any> {
    return this.httpClient.post<any>(this._BASE_URL + '/search', payload);
  }

}
