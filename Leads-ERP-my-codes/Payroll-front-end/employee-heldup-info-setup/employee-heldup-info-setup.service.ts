import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { BaseApiService } from '../../../core/utils/service/base-api.service';
import { Observable } from 'rxjs';
import { CommonResponseList, CommonResponseObject } from '../../../core/utils/model/common-response';

@Injectable({
  providedIn: 'root'
})
export class EmployeeHeldupInfoSetupService extends BaseApiService<any> {

  constructor(httpClient: HttpClient) {
    super(httpClient, environment.payrollEndPoint + 'employee-heldup');
  }



  /**
   * Update single Employee Heldup info Setup
   */
  updateEmployeeHeldupInfo(payload: any): Observable<CommonResponseObject<any>> {
    return this.update(payload as any);
  }

  /**
   * Delete Employee Heldup info Setup by UUID
   */
  deleteEmployeeHeldupInfo(uuid: string): Observable<CommonResponseObject<any>> {
    return this.delete(uuid);
  }

  /**
   * Get Employee Heldup info Setup by UUID
   */
  getEmployeeHeldupInfoByUuid(uuid: string): Observable<CommonResponseObject<any>> {
    return this.getByUuid(uuid);
  }

  /**
   * Get all Employee Heldup info Setup
   */
  getAllEmployeeHeldupInfo(): Observable<CommonResponseList<any>> {
    return this.getList();
  }

  searchEmployeeHeldupInfo(filters: {
    employeeId?: string;
    employeeName?: string;
    bankName?: string;
    accountNo?: Number;
  }): Observable<CommonResponseObject<any>> {
    const cleanFilters = {
      ...(filters.employeeId?.trim() ? { phaseId: filters.employeeId.trim() } : {}),
      ...(filters.employeeName?.trim() ? { phaseId: filters.employeeName.trim() } : {}),
      ...(filters.bankName?.trim() ? { phaseId: filters.bankName.trim() } : {}),
      ...(filters.accountNo ? { taxYear: filters.accountNo } : {}),
    };
    return this.httpClient.post<CommonResponseObject<any>>(
      `${this._BASE_URL}/search`,
      cleanFilters
    );
  }

  createHeldupInfo(payload: any): Observable<any> {
    return this.httpClient.post(`${this._BASE_URL}/save`, payload);
  }

  updateHeldupInfo(payload: any): Observable<any> {
    return this.httpClient.put(`${this._BASE_URL}/update`, payload);
  }

  search(payload: any): Observable<any> {
    return this.httpClient.post<any>(this._BASE_URL + '/search', payload);
  }

  getHeldupDetailsData(uuid: any) {
    return this.httpClient.get(
      `${environment.payrollEndPoint}employee-heldup/get-data/${uuid}`
    );
  }

  getEmployeeSalaryBreakup(payload: any) {
    return this.httpClient.post(
      `${environment.payrollEndPoint}employee-salary/get-structures-by-emp-ids`,
      payload
    );
  }

}
