import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { MaintenanceInformationModel } from '../model/maintenance-information.model';
import { ApiResponse, Page } from '../model/vehicle-info.model';
import { BaseApiService } from '../../../core/utils/service/base-api.service';

@Injectable({
  providedIn: 'root'
})
export class MaintenanceInformationService extends BaseApiService<MaintenanceInformationModel> {
  constructor(private http: HttpClient) {
    super(http, environment.vehicleEndPoint + 'maintenance-information');
  }

  /** Fetch last change date for a vehicle/item pair */
  getLastChangeDate(vehicleUuid: string, itemName: string): Observable<any> {
    return this.http.get<any>(`${this._BASE_URL}/${vehicleUuid}/items/${itemName}/last-change`);
  }

  search(
    formValue: any,
    pageIndex: number,
    pageSize: number
  ): Observable<ApiResponse<Page<MaintenanceInformationModel>>> {

    const params: any = {
      pageIndex,
      pageSize
    };

    if (formValue.vehicleName) {
      params.vehicleName = formValue.vehicleName.trim();
    }
    if (formValue.vehicleType) {
      params.vehicleType = formValue.vehicleType;
    }
    if (formValue.registrationNumber) {
      params.registrationNumber = formValue.registrationNumber.trim();
    }
    if (formValue.supplierName) {
      params.supplierName = formValue.supplierName.trim();
    }

    const httpParams = new HttpParams({ fromObject: params });
    return this.http.get<ApiResponse<Page<MaintenanceInformationModel>>>(`${this._BASE_URL}/search`, { params: httpParams });
  }
}
