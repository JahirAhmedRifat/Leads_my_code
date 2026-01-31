import {
  Component,
  OnInit,
  CUSTOM_ELEMENTS_SCHEMA,
  signal,
  effect,
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { InputTextBox } from '../../../../shared/common-components/input-types/input-text-box/input-text-box';
import { ExpansionPanelHeader } from '../../../../shared/common-components/expansion-panel-header/expansion-panel-header';
import {
  BUTTON_VISIBILITY,
  ONCLICK_SAVE,
  ONCLICK_UPDATE,
  ONCLICK_RESET,
  ONCLICK_VIEW,
} from '../../../../shared/constant/button-signals.constant';
import { TaxRebatePolicyService } from '../../services/tax-rebate-policy.service';
import { ToastHelperService } from '../../../../shared/services/toast-helper.service';
import { GenericModal } from '../../../../shared/common-components/generic-component-type/generic-modal/generic-modal';
import { GenericDataGrid } from '../../../../shared/common-components/generic-component-type/generic-data-grid';
import { GenericButton } from '../../../../shared/common-components/generic-component-type/generic-button/generic-button';
import { InputSelectOptionField } from '../../../../shared/common-components/input-types/input-select-option-field/input-select-option-field';
import { TaxRebatePolicyList } from '../tax-rebate-policy-list/tax-rebate-policy-list';
import { InputAmount } from '../../../../shared/common-components/input-types/input-amount/input-amount';

interface RebateDetail {
  policySerialNo: number;
  taxYear: any;
  amountTypeKey: any;
  amountTypeValue: string;
  totalAmount: any;
  uuid?: string; // Optional
}

@Component({
  selector: 'app-tax-rebate-policy',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatIconModule,
    InputTextBox,
    ExpansionPanelHeader,
    InputSelectOptionField,
    GenericButton,
    GenericDataGrid,
    GenericModal,
    InputAmount
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './tax-rebate-policy.html',
  styleUrl: './tax-rebate-policy.scss',
})
export class TaxRebatePolicy implements OnInit {
  frmGroup!: FormGroup;
  isUpdate = false;
  isEdit = false;
  uuid: string | null = null;
  policySerialNo: number | null = null;
  isModalShow: boolean = false;
  rebateListModal: any = null;
  rebateDetailsData = signal<any[]>([]);
  data: any[] = [];
  currentEditIndex: number | null = null;
  taxRebatePolicyPanel = signal(true);
  taxRebateSetupDetailsPanel = signal(true);

  maxAmount = signal<number>(1000000);
  decimalPlaces = signal<number>(2);

  amountTypeOptions: any[] = [
    { key: '1', value: 'Taxable Income' },
    { key: '2', value: 'Investment Amount' },
    { key: '3', value: 'Max Rebate from Investment' }
  ];

  rebateDetailsColumns = [
    'policySerialNo',
    'taxYear',
    'amountTypeValue', // Show the display value
    'totalAmount'
  ];

  rebateDetailsColumnNames = {
    policySerialNo: 'Policy Serial No',
    taxYear: 'Tax Year',
    amountTypeValue: 'Amount Type',
    totalAmount: 'Value'
  };

  constructor(
    private fb: FormBuilder,
    private taxRebatePolicyService: TaxRebatePolicyService,
    private toastHelper: ToastHelperService
  ) {
    this.frmGroup = this.createForm();
    this.setupButtonEffects();
  }

  ngOnInit() {

    const taxRebatePolicyData = history.state?.taxRebatePolicyData;
    if (taxRebatePolicyData) {
      this.isEdit = true;
      this.loadTaxRebatePolicyData(taxRebatePolicyData);
      BUTTON_VISIBILITY.set({
        save: false,
        update: true,
        view: true,
        delete: false,
        exit: false,
        reset: true,
      });
    } else {
      // default tax year to current year
      this.frmGroup.patchValue({ taxYear: this.getCurrentYear() });
      BUTTON_VISIBILITY.set({
        save: true,
        update: false,
        view: true,
        delete: false,
        exit: false,
        reset: true,
      });
    }

    this.frmGroup.get('amountType')?.valueChanges.subscribe((value) => {
      this.handleAmountTypeChange(value);
    });

  }

  handleAmountTypeChange(amountTypeKey: string) {

    const totalAmountControl = this.frmGroup.get('totalAmount');
    if (!totalAmountControl) return;

    totalAmountControl.clearValidators();

    if (amountTypeKey === '1' || amountTypeKey === '2') {
      // Percentage type (0–100)
      this.maxAmount.set(100);
      this.decimalPlaces.set(0);

      totalAmountControl?.setValidators([
        Validators.required,
        Validators.min(0),
        Validators.max(100)
      ]);

    } else if (amountTypeKey === '3') {
      // Absolute amount (0–1,000,000)
      this.maxAmount.set(1_000_000);
      this.decimalPlaces.set(2);

      totalAmountControl?.setValidators([
        Validators.required,
        Validators.min(0),
        Validators.max(1_000_000)
      ]);
    }

    totalAmountControl?.updateValueAndValidity();
  }


  private createForm(): FormGroup {
    const form = this.fb.group({
      taxYear: [
        '',
        [
          Validators.required,
          Validators.pattern(/^[1-9]\d{3}$/)
        ]
      ],
      amountType: ['', Validators.required],
      totalAmount: [
        '',
        [
          Validators.required,
          // Validators.max(1000000)
        ]
      ],
      policySerialNo: [''],
    });

    return form;
  }

  private setupButtonEffects(): void {

    // Handle button clicks
    effect(() => {
      if (ONCLICK_SAVE()) {
        this.submit();
        ONCLICK_SAVE.set(false);
      }
      if (ONCLICK_UPDATE()) {
        this.update();
        ONCLICK_UPDATE.set(false);
      }
      if (ONCLICK_RESET()) {
        this.reset();
        ONCLICK_RESET.set(false);
      }
      if (ONCLICK_VIEW()) {
        this.rebateListModal = TaxRebatePolicyList;
        this.isModalShow = true;
        ONCLICK_VIEW.set(false);
      }
    });

  }

  loadTaxRebatePolicyData(data: any): void {

    // Handle single item
    if (!Array.isArray(data)) {

      if (data.uuid) {
        this.uuid = data.uuid;
        this.policySerialNo = data.policySerialNo;

        this.frmGroup.patchValue({
          taxYear: data.taxYear?.toString() || '',
          amountType: data.amountType?.toString() || '',
          totalAmount: data.totalAmount || ''
        });
        this.rebateDetailsData.set([]);
      }
      //------------ Optional //without UUID (new item to add to grid)---------------
      else {
        // Single item without UUID (new item to add to grid)       
        const displayValue = this.getGenericName(data.amountType);
        this.rebateDetailsData.set([{
          policySerialNo: data.policySerialNo || 1,
          taxYear: data.taxYear?.toString() || '',
          amountTypeKey: data.amountType?.toString() || '',
          amountTypeValue: displayValue,
          totalAmount: data.totalAmount || ''
        }]);
      }
    }
    //------------ Optional /// Multiple items - show grid ---------------
    else if (Array.isArray(data) && data.length > 0) {
      // Multiple items - show grid
      const transformedData = data.map((item, index) => {
        const displayValue = this.getGenericName(item.amountType);
        return {
          policySerialNo: item.policySerialNo || (index + 1),
          taxYear: item.taxYear?.toString() || '',
          amountTypeKey: item.amountType?.toString() || '',
          amountTypeValue: displayValue,
          totalAmount: item.totalAmount || '',
          uuid: item.uuid || null
        };
      });

      this.rebateDetailsData.set(transformedData);

      // If there's at least one item, populate the form with the first one for editing
      if (transformedData.length > 0) {
        const firstItem = transformedData[0];
        this.frmGroup.patchValue({
          taxYear: firstItem.taxYear,
          amountType: firstItem.amountTypeKey,
          totalAmount: firstItem.totalAmount
        });
      }
    }

  }

  getModalResult(event: any) {
    if (event && event.taxRebatePolicyData) {
      this.isEdit = true;
      this.loadTaxRebatePolicyData(event.taxRebatePolicyData);
      BUTTON_VISIBILITY.set({
        save: false,
        update: true,
        view: true,
        delete: false,
        exit: false,
        reset: true,
      });
    }
    this.isModalShow = false;
  }

  addRebate() {
    if (this.frmGroup.invalid) {
      // this.frmGroup.markAllAsTouched();
      this.toastHelper.warning('Please fill up all required fields');
      return;
    }

    const formValue = this.frmGroup.getRawValue();
    this.data = [...this.rebateDetailsData()];

    // Get the display value
    const displayValue = this.getGenericName(formValue.amountType);

    // ---------- Prevent duplicate amountType ----------
    const exists = this.data.some(
      (item, idx) =>
        (item.amountTypeKey === formValue.amountType ||
          item.amountTypeValue === displayValue) &&
        idx !== this.currentEditIndex
    );

    if (exists) {
      this.toastHelper.warning('This Amount Type already exists in the list');
      return;
    }

    const payload: RebateDetail = {
      policySerialNo: 0,
      taxYear: formValue.taxYear,
      amountTypeKey: formValue.amountType, // Store the key
      amountTypeValue: displayValue,
      totalAmount: formValue.totalAmount
    };

    // -------- Add / Update ----------
    if (this.currentEditIndex !== null) {
      // Update existing - preserve UUID if editing existing item
      const existingItem = this.data[this.currentEditIndex];
      payload.policySerialNo = existingItem.policySerialNo;
      if (existingItem.uuid) {
        payload.uuid = existingItem.uuid;
      }
      this.data[this.currentEditIndex] = payload;
    } else {
      // Add new
      payload.policySerialNo = this.data.length + 1;
      this.data.push(payload);
    }

    // -------- Refresh grid ----------
    this.rebateDetailsData.set([...this.data]);
    this.reset();
    this.currentEditIndex = null;
    this.isUpdate = false;
  }

  getGenericName(key: any): string {
    if (!key) return 'N/A';
    const stringKey = key.toString();
    const object = this.amountTypeOptions.find(v => v.key === stringKey);
    return object?.value || 'N/A';
  }

  updateRebate() {
    this.addRebate();
  }

  onEditRebateDetail(itemJson: any): void {
    const element = JSON.parse(itemJson);

    const data = [...this.rebateDetailsData()];
    const index = data.findIndex(
      item => item.policySerialNo === element.policySerialNo
    );

    if (index === -1) return;

    this.currentEditIndex = index;
    this.isUpdate = true;

    this.frmGroup.patchValue({
      taxYear: element.taxYear,
      amountType: element.amountTypeKey,
      totalAmount: element.totalAmount
    });
  }

  onDeleteRebateDetail(itemJson: any): void {
    const element = JSON.parse(itemJson);

    let data = [...this.rebateDetailsData()];
    data = data.filter(
      item => item.policySerialNo !== element.policySerialNo
    );

    // Re-assign serial numbers
    data = data.map((item, index) => ({
      ...item,
      policySerialNo: index + 1
    }));

    this.rebateDetailsData.set(data);
  }

  getCurrentYear(): string {
    return new Date().getFullYear().toString();
  }

  reset() {
    this.frmGroup.reset();
    this.isEdit = false;
    this.currentEditIndex = null;
    this.isUpdate = false;
    if (!this.isEdit) {
      this.frmGroup.patchValue({ taxYear: this.getCurrentYear() });
    }
    BUTTON_VISIBILITY.set({
      save: true,
      update: false,
      view: true,
      delete: false,
      exit: false,
      reset: true,
    });
  }

  submit() {

    if (this.rebateDetailsData().length === 0) {
      this.toastHelper.warning('Please add at least one rebate detail before saving');
      return;
    }

    // Prepare the request payload
    const payload = this.rebateDetailsData().map(item => ({
      taxYear: Number(item.taxYear),
      policySerialNo: item.policySerialNo,
      amountType: Number(item.amountTypeKey), // Convert key to number
      totalAmount: Number(item.totalAmount)
    }));

    // Call the service
    this.taxRebatePolicyService.bulkSave(payload)
      .subscribe({
        next: (res) => {

          if (res?.status) {
            this.toastHelper.success(res.message || 'Tax rebate policies saved successfully!');

            this.rebateDetailsData.set([]);
            this.data = [];
            this.reset();
          } else {
            this.toastHelper.error(res?.message || 'Failed to save');
          }
        },
        error: (err) => {
          const serverMessage = err?.error?.detail || err?.error?.message || 'Server error';
          this.toastHelper.error(serverMessage);
        }
      });
  }

  update() {
    if (this.frmGroup.invalid) {
      this.frmGroup.markAllAsTouched();
      // this.toastHelper.warning('Please fill all required fields');
      return;
    }

    const formValue = this.frmGroup.getRawValue();

    // Prepare payload for single record update
    const payload = {
      uuid: this.uuid,
      taxYear: Number(formValue.taxYear),
      policySerialNo: this.policySerialNo || 1,
      amountType: Number(formValue.amountType),
      totalAmount: Number(formValue.totalAmount),
      isDeleted: false
    };

    // Call the update API for single record
    this.taxRebatePolicyService.updateSingleRecord(payload)
      .subscribe({
        next: (res) => {
          if (res?.status) {
            this.toastHelper.success(res.message || 'Tax rebate policy updated successfully!');
            this.reset();
          } else {
            this.toastHelper.error(res?.message || 'Failed to update');
          }
        },
        error: (err) => {
          const serverMessage = err?.error?.detail || err?.error?.message || 'Server error';
          this.toastHelper.error(serverMessage);
        }
      });
  }

}
