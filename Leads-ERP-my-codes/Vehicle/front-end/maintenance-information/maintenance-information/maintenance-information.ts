import { CommonModule } from '@angular/common';
import { Component, Input, OnDestroy, OnInit, WritableSignal, effect, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {
  BUTTON_VISIBILITY,
  ONCLICK_RESET,
  ONCLICK_SAVE,
  ONCLICK_UPDATE,
  ONCLICK_VIEW
} from '../../../../shared/constant/button-signals.constant';
import { SelectOptionsModel } from '../../../../shared/models/select-options-model';
import { ToastHelperService } from '../../../../shared/services/toast-helper.service';
import { MaintenanceInformationService } from '../../service/maintenance-information.service';
import { ServiceItemService } from '../../service/service-item.service';
import { VehicleInfoService } from '../../service/vehicle-info.service';
import { VehicleInfo } from '../../model/vehicle-info.model';
import { MaintenanceItemDetail } from '../../model/maintenance-information.model';
import { GenericButton } from '../../../../shared/common-components/generic-component-type/generic-button/generic-button';
import { ExpansionPanelHeader } from '../../../../shared/common-components/expansion-panel-header/expansion-panel-header';
import { ExpansionSubPanelHeader } from '../../../../shared/common-components/expansion-sub-panel-header/expansion-sub-panel-header';
import { InputSelectOptionField } from '../../../../shared/common-components/input-types/input-select-option-field/input-select-option-field';
import { InputTextBox } from '../../../../shared/common-components/input-types/input-text-box/input-text-box';
import { InputNumber } from '../../../../shared/common-components/input-types/input-number/input-number';
import { InputDate } from '../../../../shared/common-components/input-types/input-date/input-date';
import { GenericDataGrid } from '../../../../shared/common-components/generic-component-type/generic-data-grid';
import { CentrinoConfigService } from '../../../../shared/services/centrino-config.service';

interface MaintenanceItemRow {
  id?: number;
  uuid?: string;
  itemName: string;
  unitPrice: number;
  quantity: number;
  subTotal: number;
  remarks?: string;
  lastChange?: string | null;
}

@Component({
  selector: 'app-maintenance-information',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    GenericButton,
    ExpansionPanelHeader,
    ExpansionSubPanelHeader,
    InputSelectOptionField,
    InputTextBox,
    InputNumber,
    InputDate,
    GenericDataGrid
  ],
  templateUrl: './maintenance-information.html',
  styleUrl: './maintenance-information.scss'
})
export class MaintenanceInformation implements OnInit, OnDestroy {

  @Input() viewObject: any | null;
  @Input() isViewDetails: boolean | false;
  @Input() highlightMap: Record<string, boolean> | null = null;
  @Input() isApprovalView: boolean = false;

  buttonName = 'Save';
  isUpdate = false;
  isDataLoading = false;
  private editData: any = null;
  private editId: number | string | null = null;
  private editUuid: string | null = null;

  mainPanel: WritableSignal<boolean> = signal(true);
  itemPanel: WritableSignal<boolean> = signal(true);

  maintenanceInfoForm!: FormGroup;
  maintenanceItemForm!: FormGroup;

  vehicleOptions: SelectOptionsModel[] = [];
  paymentTypeOptions: SelectOptionsModel[] = [];
  itemOptions: SelectOptionsModel[] = [];

  private vehicleData: VehicleInfo[] = [];
  private vehicleTypeList: any[] = [];
  private itemRecords: any[] = [];
  private itemNameMap: Record<string, string> = {};
  maintenanceItemsList: MaintenanceItemRow[] = [];
  itemGridColumns: string[] = ['itemName', 'unitPrice', 'quantity', 'subTotal', 'remarks', 'lastChange'];
  itemGridColumnNames: Record<string, string> = {
    itemName: 'Item',
    unitPrice: 'Unit Price',
    quantity: 'Quantity',
    subTotal: 'Subtotal',
    remarks: 'Remarks',
    lastChange: 'Last Change'
  };

  constructor(
    private fb: FormBuilder,
    private maintenanceInformationService: MaintenanceInformationService,
    private vehicleInfoService: VehicleInfoService,
    private serviceItemService: ServiceItemService,
    private centrinoConfigService: CentrinoConfigService,
    private router: Router,
    private toast: ToastHelperService
  ) {
    ONCLICK_SAVE.set(false);
    ONCLICK_RESET.set(false);
    ONCLICK_VIEW.set(false);
    ONCLICK_UPDATE.set(false);

    this.buildForms();
    this.setupButtonEffects();
    this.captureRouteState();
    this.setNavbarButtons();
  }

  ngOnInit(): void {

    this.loadDropdowns();

    if (this.isViewDetails) {
      this.maintenanceInfoForm.disable();

      // Set button visibility for view mode
      BUTTON_VISIBILITY.set({
        save: false,
        update: false,
        view: true,
        delete: false,
        exit: false,
        reset: false,
      });

      // Load data from viewObject if provided
      if (this.viewObject) {
        // this.viewObject.maintenanceDetails = this.viewObject?.maintenanceDetailsForApproval;
        this.loadViewData(this.viewObject);
      }
    }
  }

  private loadViewData(viewObject: any): void {
    if (!viewObject || Object.keys(viewObject).length === 0) {
      console.warn('View object is empty');
      return;
    }

    this.patchForm(viewObject);

  }

  ngOnDestroy(): void {
    BUTTON_VISIBILITY.set({
      save: false,
      update: false,
      view: false,
      delete: false,
      exit: false,
      reset: false
    });
  }

  private setNavbarButtons(): void {
    if (this.isViewDetails) {
      BUTTON_VISIBILITY.set({
        save: false,
        update: false,
        view: true,
        delete: false,
        exit: false,
        reset: false,
      });
    } else {
      BUTTON_VISIBILITY.set({
        save: !this.isUpdate,
        update: this.isUpdate,
        view: true,
        delete: false,
        exit: false,
        reset: true
      });
    }

  }

  private buildForms(): void {
    this.maintenanceInfoForm = this.fb.group({
      id: [null],
      uuid: [null],
      vehicleName: ['', Validators.required],
      couponNo: ['', [Validators.required, Validators.maxLength(50), Validators.pattern('^[0-9]*$')]],
      distanceTraveled: [null, [Validators.maxLength(6), Validators.pattern('^[0-9]*$')]],
      supplierName: ['', [Validators.required, Validators.maxLength(50)]],
      paymentType: ['', Validators.required],
      issueDate: ['', Validators.required],
      vehicleType: [''],
      registrationNumber: ['']
    });

    this.maintenanceItemForm = this.fb.group({
      itemName: ['', Validators.required],
      unitPrice: [null, [Validators.min(0)]],
      quantity: [null, [Validators.required, Validators.min(0.01)]],
      subTotal: [{ value: null, disabled: true }],
      remarks: [''],
      lastChange: [{ value: '', disabled: true }]
    });

    this.maintenanceItemForm.get('unitPrice')?.valueChanges.subscribe(() => this.updateSubTotal());
    this.maintenanceItemForm.get('quantity')?.valueChanges.subscribe(() => this.updateSubTotal());
  }

  private setupButtonEffects(): void {
    this.setNavbarButtons();

    effect(() => {
      if (ONCLICK_SAVE()) {
        this.onSubmit();
        ONCLICK_SAVE.set(false);
      }
      if (ONCLICK_UPDATE()) {
        this.onSubmit();
        ONCLICK_UPDATE.set(false);
      }
      if (ONCLICK_RESET()) {
        this.onReset();
        ONCLICK_RESET.set(false);
      }
      if (ONCLICK_VIEW()) {
        this.onBack();
        ONCLICK_VIEW.set(false);
      }
    }, { allowSignalWrites: true });
  }

  private captureRouteState(): void {
    const navigationData = this.router.getCurrentNavigation()?.extras?.state?.['data'];
    const stateData = history.state?.data;
    const incomingData = navigationData ?? stateData;

    if (incomingData) {
      const data = incomingData;
      this.isUpdate = true;
      this.buttonName = 'Update';
      this.editData = data;
      this.editId = data.id ?? null;
      this.editUuid = data.uuid ?? null;
      this.maintenanceInfoForm.patchValue({
        id: data.id ?? null,
        uuid: data.uuid ?? null
      });
      this.setNavbarButtons();
    }
  }

  private loadDropdowns(): void {
    this.isDataLoading = true;

    const vehicles$ = this.vehicleInfoService.getList().pipe(
      catchError(() => {
        this.toast.error('Unable to load vehicle list', 'Error');
        return of({ status: false, payload: [] });
      })
    );

    const items$ = this.serviceItemService.getList().pipe(
      catchError(() => {
        this.toast.error('Unable to load maintenance items', 'Error');
        return of({ status: false, payload: [] });
      })
    );

    const paymentTypes$ = this.centrinoConfigService.getVehiclePaymentTypeList().pipe(
      catchError(() => of({ payload: [] }))
    );

    const vehicleTypes$ = this.centrinoConfigService.getVehicleTypeList().pipe(
      catchError(() => of({ payload: [] }))
    );

    forkJoin({
      vehicles: vehicles$,
      items: items$,
      paymentTypes: paymentTypes$,
      vehicleTypes: vehicleTypes$
    }).subscribe({
      next: ({ vehicles, items, paymentTypes, vehicleTypes }) => {
        this.vehicleData = vehicles?.payload || [];
        this.vehicleOptions = this.vehicleData.map((v: VehicleInfo) => ({
          key: v.vehicleName,
          value: v.vehicleName
        }));

        const itemsPayload: any = items as any;
        const rawItems =
          Array.isArray(itemsPayload?.payload?.content) ? itemsPayload.payload.content :
            Array.isArray(itemsPayload?.payload?.data) ? itemsPayload.payload.data :
              Array.isArray(itemsPayload?.payload) ? itemsPayload.payload :
                Array.isArray(itemsPayload) ? itemsPayload :
                  [];
        this.itemRecords = rawItems;
        this.itemNameMap = {};
        this.itemOptions = this.itemRecords
          .map((item: any) => {
            const rawKey =
              item.id ??
              item.itemId ??
              item.item?.id ??
              item.itemCode ??
              item.code ??
              '';
            const key = rawKey !== undefined && rawKey !== null ? String(rawKey) : '';
            const display =
              item.itemName ??
              item.name ??
              item.item?.itemName ??
              item.item?.name ??
              item.itemCode ??
              item.code ??
              key ??
              '';
            if (key && display) {
              this.itemNameMap[key] = display;
            }
            return {
              key: key,
              value: String(display ?? key ?? '')
            };
          })
          .filter(opt => !!opt.value);

        this.paymentTypeOptions = (paymentTypes?.payload || [])
          .map((pt: any) => ({
            key: pt.id,
            value: pt.nameEn
          }));

        this.vehicleTypeList = vehicleTypes?.payload || [];

        if (this.isUpdate) {
          this.loadEditDataIfNeeded();
        }
      },
      error: () => this.toast.error('Failed to load dropdowns', 'Error'),
      complete: () => (this.isDataLoading = false)
    });
  }

  private patchForm(model: any): void {
    if (!model) return;

    this.editData = model;
    this.isUpdate = true;
    this.buttonName = 'Update';
    this.setNavbarButtons();

    const paymentTypeValue = model.paymentType ? Number(model.paymentType) : '';

    let vehicleTypeName = '';
    if (model.vehicleType && this.vehicleTypeList?.length) {
      const match = this.vehicleTypeList.find((v: any) =>
        String(v.id) === String(model.vehicleType)
      );
      vehicleTypeName = match?.nameEn ?? model.vehicleType;
    }

    this.maintenanceInfoForm.patchValue({
      id: model.id ?? null,
      uuid: model.uuid ?? null,
      vehicleName: model.vehicleName ?? '',
      couponNo: model.couponNo ?? '',
      distanceTraveled: model.distanceTraveled ?? null,
      supplierName: model.supplierName ?? '',
      paymentType: paymentTypeValue,
      issueDate: model.issueDate ? new Date(model.issueDate) : '',
      // vehicleType: model.vehicleType ?? '',
      vehicleType: vehicleTypeName,
      registrationNumber: model.registrationNumber ?? ''
    });

    if (Array.isArray(model.maintenanceDetails)) {
      this.maintenanceItemsList = model.maintenanceDetails.map((item: any) => ({
        id: item.id,
        uuid: item.uuid,
        itemName: this.resolveItemName(item.itemName ?? (item as any)?.itemId ?? (item as any)?.id),
        unitPrice: Number(item.unitPrice) || 0,
        quantity: Number(item.quantity) || 0,
        subTotal: Number(item.subTotal ?? item.subtotal ?? 0) || 0,
        remarks: item.remarks || '',
        lastChange: item.lastChange ?? null
      }));
    }
  }

  private loadEditDataIfNeeded(): void {
    if (this.editData) {
      this.patchForm(this.editData);

      const hasDetails = Array.isArray(this.editData?.maintenanceDetails) && this.editData.maintenanceDetails.length > 0;
      if (hasDetails) {
        return;
      }

      if (this.editUuid) {
        this.fetchAndPatchByUuid(this.editUuid);
        return;
      }

      if (this.editId !== null) {
        this.fetchAndPatchById(this.editId);
        return;
      }
    }

    if (this.editUuid) {
      this.fetchAndPatchByUuid(this.editUuid);
      return;
    }

    if (this.editId !== null) {
      this.fetchAndPatchById(this.editId);
    }
  }

  private fetchAndPatchByUuid(uuid: string): void {
    this.maintenanceInformationService.getByUuid(uuid).subscribe({
      next: (res: any) => {
        const payload = res?.payload ?? res?.data ?? res;
        this.editData = payload;
        this.patchForm(this.editData);
      },
      error: () => this.toast.error('Unable to load maintenance info details', 'Error')
    });
  }

  private fetchAndPatchById(id: number | string): void {
    this.maintenanceInformationService.getById(id as any).subscribe({
      next: (res: any) => {
        const payload = res?.payload ?? res?.data ?? res;
        this.editData = payload;
        this.patchForm(this.editData);
      },
      error: () => this.toast.error('Unable to load maintenance info details', 'Error')
    });
  }

  onDropdownChange(option: any, controlName: string): void {
    if (!option || option.selectedKey === undefined) return;
    this.maintenanceInfoForm.get(controlName)?.setValue(option.selectedKey);
    if (controlName === 'vehicleName') {
      this.populateVehicleDetails(option.selectedKey);
    }
  }

  onItemDropdownChange(option: any): void {
    if (!option || option.selectedKey === undefined) return;
    this.maintenanceItemForm.get('itemName')?.setValue(String(option.selectedKey));

    const selected = this.itemRecords.find(
      (x: any) =>
        String(x.id) === String(option.selectedKey) ||
        String(x.itemId) === String(option.selectedKey) ||
        String(x.item?.id) === String(option.selectedKey) ||
        x.itemName === option.selectedKey ||
        x.name === option.selectedKey
    );

    this.maintenanceItemForm.patchValue({
      unitPrice: selected?.unitPrice ?? null
    });

    this.updateSubTotal();

    const vehicleUuid = this.maintenanceInfoForm.get('uuid')?.value;
    if (vehicleUuid && option.selectedKey) {
      this.fetchLastChange(vehicleUuid, option.selectedKey);
    } else {
      this.maintenanceItemForm.get('lastChange')?.setValue('', { emitEvent: false });
    }
  }

  private fetchLastChange(vehicleUuid: string, itemName: string): void {
    this.maintenanceInformationService.getLastChangeDate(vehicleUuid, itemName).subscribe({
      next: (res: any) => {
        const payload = res?.payload ?? res;
        const value = typeof payload === 'string' ? payload : payload?.data ?? '';
        this.maintenanceItemForm.get('lastChange')?.setValue(value || '', { emitEvent: false });
      },
      error: () => {
        this.maintenanceItemForm.get('lastChange')?.setValue('', { emitEvent: false });
      }
    });
  }

  private populateVehicleDetails(vehicleName: string): void {
    const selectedVehicle = this.vehicleData.find(v => v.vehicleName === vehicleName);
    let vehicleTypeName = selectedVehicle?.vehicleType ?? '';

    if (selectedVehicle?.vehicleType && this.vehicleTypeList?.length) {
      const match = this.vehicleTypeList.find((v: any) => String(v.id) === String(selectedVehicle.vehicleType));
      vehicleTypeName = match?.nameEn ?? selectedVehicle.vehicleType;
    }

    this.maintenanceInfoForm.patchValue({
      vehicleType: vehicleTypeName,
      registrationNumber: selectedVehicle?.registrationNumber ?? ''
    });
  }

  private updateSubTotal(): void {
    const unitPrice = parseFloat(this.maintenanceItemForm.get('unitPrice')?.value) || 0;
    const quantity = parseFloat(this.maintenanceItemForm.get('quantity')?.value) || 0;
    const subTotal = unitPrice * quantity;
    this.maintenanceItemForm.get('subTotal')?.setValue(subTotal || null, { emitEvent: false });
  }

  addItem(): void {
    if (this.maintenanceItemForm.invalid) {
      this.markServiceItemTouched();
      return;
    }

    const raw = this.maintenanceItemForm.getRawValue();
    const displayName = this.resolveItemName(raw.itemName);
    const newItem = {
      id: undefined,
      uuid: undefined,
      itemName: displayName || raw.itemName,
      unitPrice: Number(raw.unitPrice) || 0,
      quantity: Number(raw.quantity) || 0,
      subTotal: Number(raw.subTotal) || 0,
      remarks: raw.remarks || '',
      lastChange: raw.lastChange || null
    };
    this.maintenanceItemsList = [...this.maintenanceItemsList, newItem];

    this.maintenanceItemForm.reset();
    this.toast.success('Item added', 'OK');
  }

  removeMaintenanceItem(index: number): void {
    this.maintenanceItemsList = this.maintenanceItemsList.filter((_, i) => i !== index);
  }

  onGridDelete(event: any): void {
    const row = typeof event === 'string' ? this.tryParse(event) : event;
    const index = this.resolveRowIndex(row);
    if (index > -1) {
      this.removeMaintenanceItem(index);
      this.toast.success('Item removed', 'Success');
    } else {
      this.toast.warning('Unable to delete selected item', 'Warning');
    }
  }

  private tryParse(event: string): any | null {
    try {
      return JSON.parse(event);
    } catch {
      return null;
    }
  }

  private resolveRowIndex(row: any): number {
    if (row == null) {
      return -1;
    }
    if (typeof row.rowIndex === 'number') {
      return row.rowIndex;
    }
    const matcher = (item: MaintenanceItemRow) =>
      item.itemName === row.itemName &&
      Number(item.unitPrice) === Number(row.unitPrice) &&
      Number(item.quantity) === Number(row.quantity) &&
      Number(item.subTotal) === Number(row.subTotal) &&
      (item.remarks || '') === (row.remarks || '');
    return this.maintenanceItemsList.findIndex(matcher);
  }

  onSubmit(): void {
    if (this.maintenanceInfoForm.invalid) {
      this.markMainFormTouched();
      return;
    }

    if (this.maintenanceItemsList.length === 0) {
      this.toast.warning('Add at least one maintenance item', 'Required');
      return;
    }

    const payload = this.buildPayload();
    const call = this.isUpdate ? this.maintenanceInformationService.update(payload) : this.maintenanceInformationService.create(payload);

    call.subscribe({
      next: (res) => {
        if (res.status) {
          const msg = this.isUpdate ? 'Maintenance information updated successfully.' : 'Maintenance information saved successfully.';
          this.toast.success(msg, 'Success');
          this.onReset();
        }
      },
      error: (err) => {
        const message = this.isUpdate ? 'Failed to update maintenance information' : 'Failed to save maintenance information';
        this.toast.error(err?.error?.message || message, 'Error');
      }
    });
  }

  private buildPayload(): any {
    const raw = this.maintenanceInfoForm.getRawValue();

    const selectedVehicle = this.vehicleData.find(v => v.vehicleName === raw.vehicleName);
    let vehicleTypeKey = selectedVehicle?.vehicleType ?? '';

    return {
      // id: raw.id,
      // uuid: raw.uuid,
      id: raw.id ?? this.editId,
      uuid: raw.uuid ?? this.editUuid,
      vehicleName: raw.vehicleName,
      couponNo: raw.couponNo,
      distanceTraveled: raw.distanceTraveled ?? undefined,
      supplierName: raw.supplierName,
      paymentType: String(raw.paymentType),
      issueDate: raw.issueDate,
      // vehicleType: raw.vehicleType,
      vehicleType: vehicleTypeKey,
      registrationNumber: raw.registrationNumber,
      maintenanceDetails: this.maintenanceItemsList.map(item => ({
        id: item.id,
        uuid: item.uuid,
        itemName: this.resolveItemName(item.itemName),
        unitPrice: Number(item.unitPrice) || 0,
        quantity: Number(item.quantity) || 0,
        subtotal: Number(item.subTotal) || 0,
        remarks: item.remarks || '',
        lastChange: this.normalizeLastChange(item.lastChange)
      }))
    };
  }

  private normalizeLastChange(value: string | null | undefined): string | null {
    if (!value) {
      return null;
    }
    if (/^\d{4}-\d{2}-\d{2}$/.test(value)) {
      return `${value}T00:00:00`;
    }
    const date = new Date(value);
    if (!Number.isNaN(date.getTime())) {
      return date.toISOString().slice(0, 19);
    }
    return value;
  }

  onReset(): void {
    this.maintenanceInfoForm.reset();
    this.maintenanceItemsList = [];
    this.maintenanceInfoForm.patchValue({
      issueDate: ''
    });
    this.isUpdate = false;
    this.buttonName = 'Save';
    this.editData = null;
    this.editId = null;
    this.editUuid = null;
    this.setNavbarButtons();
  }

  onBack(): void {
    this.router.navigate(['/vehicle/maintenance-information-list']);
    BUTTON_VISIBILITY.set({
      save: false,
      update: false,
      view: false,
      delete: false,
      exit: false,
      reset: false,
    });
  }

  private markMainFormTouched(): void {
    Object.keys(this.maintenanceInfoForm.controls).forEach(key => {
      this.maintenanceInfoForm.get(key)?.markAsTouched();
    });
  }

  private markServiceItemTouched(): void {
    Object.keys(this.maintenanceItemForm.controls).forEach(key => {
      this.maintenanceItemForm.get(key)?.markAsTouched();
    });
  }

  private resolveItemName(item: any): string {
    if (!item) return '';

    // If already a string/number, try to resolve against records or return as is
    if (typeof item === 'string' || typeof item === 'number') {
      const val = String(item);
      if (this.itemNameMap[val]) {
        return this.itemNameMap[val];
      }
      const match = this.itemRecords.find((it: any) =>
        [
          it.itemName,
          it.name,
          it.item?.itemName,
          it.item?.name,
          it.itemCode,
          it.code,
          String(it.id),
          String(it.itemId),
          String(it.item?.id)
        ].filter(Boolean).includes(val)
      );
      if (match) {
        return (
          match.itemName ??
          match.name ??
          match.item?.itemName ??
          match.item?.name ??
          match.itemCode ??
          match.code ??
          val
        );
      }
      return val;
    }

    // Object case: use known name fields first
    return (
      item.itemName ??
      item.name ??
      item.item?.itemName ??
      item.item?.name ??
      item.itemCode ??
      item.code ??
      String(item.id ?? item.itemId ?? item.item?.id ?? '')
    );
  }
}
