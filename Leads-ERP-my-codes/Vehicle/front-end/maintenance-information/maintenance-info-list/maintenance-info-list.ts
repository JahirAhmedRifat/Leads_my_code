import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { MaintenanceInformationService } from '../../service/maintenance-information.service';
import { Router } from '@angular/router';
import { BUTTON_VISIBILITY, ONCLICK_RESET, ONCLICK_SAVE, ONCLICK_UPDATE, ONCLICK_VIEW } from '../../../../shared/constant/button-signals.constant';
import { InputTextBox } from '../../../../shared/common-components/input-types/input-text-box/input-text-box';
import { InputSelectOptionField } from '../../../../shared/common-components/input-types/input-select-option-field/input-select-option-field';
import { GenericDataGrid } from '../../../../shared/common-components/generic-component-type/generic-data-grid';
import { ExpansionPanelHeader } from '../../../../shared/common-components/expansion-panel-header/expansion-panel-header';
import { ExpansionSubPanelHeader } from '../../../../shared/common-components/expansion-sub-panel-header/expansion-sub-panel-header';
import { GenericButton } from '../../../../shared/common-components/generic-component-type/generic-button/generic-button';
import { CentrinoConfigService } from '../../../../shared/services/centrino-config.service';

@Component({
  selector: 'app-maintenance-info-list',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    InputTextBox,
    InputSelectOptionField,
    GenericDataGrid,
    ExpansionPanelHeader,
    ExpansionSubPanelHeader,
    GenericButton
  ],
  templateUrl: './maintenance-info-list.html',
  styleUrl: './maintenance-info-list.scss'
})
export class MaintenanceInfoList implements OnInit {
  frmGroup!: FormGroup;
  dataSource: any[] = [];

  vehicleTypeList: any[] = [];
  paymentTypeList: any[] = [];

  vehicleTypeDropdown: any[] = [];

  pageIndex = 0;
  pageSize = 10;
  totalElements = 0;

  headerPanel = signal(true);
  searchPanel = signal(true);
  resultPanel = signal(true);

  constructor(
    private fb: FormBuilder,
    private maintenanceInformationService: MaintenanceInformationService,
    private centrinoService: CentrinoConfigService,
    private router: Router
  ) {}

  ngOnInit(): void {
    BUTTON_VISIBILITY.set({
      save: false,
      update: false,
      view: false,
      delete: false,
      exit: false,
      reset: false
    });

    ONCLICK_SAVE.set(false);
    ONCLICK_RESET.set(false);
    ONCLICK_VIEW.set(false);
    ONCLICK_UPDATE.set(false);

    this.initForm();
    this.loadCodeFiles();
  }

  initForm(): void {
    this.frmGroup = this.fb.group({
      vehicleName: [''],
      vehicleType: [''],
      registrationNumber: [''],
      supplierName: ['']
    });
  }

  loadCodeFiles(): void {
    forkJoin({
      vehicleType: this.centrinoService.getVehicleTypeList(),
      paymentType: this.centrinoService.getVehiclePaymentTypeList()
    }).subscribe({
      next: ({ vehicleType, paymentType }) => {
        this.vehicleTypeList = vehicleType?.payload || [];
        this.paymentTypeList = paymentType?.payload || [];

        this.vehicleTypeDropdown = this.vehicleTypeList.map(vt => ({
          key: vt.id.toString(),
          value: vt.nameEn
        }));

        this.loadMaintenanceList();
      },
      error: () => {
        this.vehicleTypeList = [];
        this.paymentTypeList = [];
        this.vehicleTypeDropdown = [];
        this.loadMaintenanceList();
      }
    });
  }

  loadMaintenanceList(): void {
    this.maintenanceInformationService
      .getListWithPagination(this.pageIndex, this.pageSize)
      .subscribe(res => this.handleListResponse(res));
  }

  search(): void {
    const criteria = this.buildFilters();

    if (!criteria.vehicleName && !criteria.vehicleType && !criteria.registrationNumber && !criteria.supplierName) {
      this.loadMaintenanceList();
      return;
    }

    this.pageIndex = 0;

    this.maintenanceInformationService
      .search(criteria, this.pageIndex, this.pageSize)
      .subscribe(res => this.handleListResponse(res));
  }

  reset(): void {
    this.frmGroup.reset();
    this.pageIndex = 0;
    this.loadMaintenanceList();
  }

  private buildFilters(): any {
    const raw = this.frmGroup.value;
    const vehicleTypeVal = this.resolveOptionValue(raw.vehicleType);

    return {
      vehicleName: raw.vehicleName?.trim() || '',
      vehicleType: vehicleTypeVal || '',
      registrationNumber: raw.registrationNumber?.trim() || '',
      supplierName: raw.supplierName?.trim() || ''
    };
  }

  private resolveOptionValue(value: any): string {
    if (value === null || value === undefined) {
      return '';
    }
    if (typeof value === 'string' || typeof value === 'number') {
      return String(value);
    }
    return String(value?.key ?? value?.value ?? '');
  }

  private handleListResponse(res: any): void {
    const content = res?.payload?.content ?? res?.content;

    if (!Array.isArray(content)) {
      this.dataSource = [];
      this.totalElements = res?.payload?.totalElements ?? res?.totalElements ?? 0;
      return;
    }

    const vehicleTypeMap = new Map(this.vehicleTypeList.map(x => [String(x.id), x.nameEn]));
    const paymentTypeMap = new Map(this.paymentTypeList.map(x => [String(x.id), x.nameEn]));

    this.dataSource = content.map(s => ({
      ...s,
      vehicleTypeName: vehicleTypeMap.get(String(s.vehicleType)) || '',
      paymentTypeName: paymentTypeMap.get(String(s.paymentType)) || ''
    }));

    this.totalElements = res?.payload?.totalElements ?? res?.totalElements ?? content.length;
  }

  edit(event: any): void {
    const row = this.normalizeRow(event);

    if (!row) {
      return;
    }

    const navigateWith = (payload: any) => {
      this.router.navigateByUrl('/vehicle/maintenance-information', {
        state: { data: payload }
      });
    };

    if (row?.uuid && this.maintenanceInformationService.getByUuid) {
      this.maintenanceInformationService.getByUuid(row.uuid).subscribe({
        next: (res: any) => {
          const payload = res?.payload ?? res?.data ?? res;
          navigateWith(payload || row);
        },
        error: () => navigateWith(row)
      });
      return;
    }

    if (row?.id !== undefined && this.maintenanceInformationService.getById) {
      this.maintenanceInformationService.getById(row.id).subscribe({
        next: (res: any) => {
          const payload = res?.payload ?? res?.data ?? res;
          navigateWith(payload || row);
        },
        error: () => navigateWith(row)
      });
      return;
    }

    navigateWith(row);
  }

  private normalizeRow(event: any): any {
    if (typeof event === 'string') {
      try {
        return JSON.parse(event);
      } catch {
        return null;
      }
    }
    if (event && event.eventType === 'BUTTON_CLICK') {
      return null;
    }
    return event;
  }

  onPageChange(event: { pageIndex: number; pageSize: number }): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.search();
  }

  addNew(): void {
    this.router.navigate(['/vehicle/maintenance-information']);
  }
}
