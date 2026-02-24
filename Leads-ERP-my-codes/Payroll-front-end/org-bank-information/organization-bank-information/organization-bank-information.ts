import { Component, effect, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastHelperService } from '../../../../shared/services/toast-helper.service';
import { BUTTON_VISIBILITY, ONCLICK_RESET, ONCLICK_SAVE, ONCLICK_UPDATE, ONCLICK_VIEW } from '../../../../shared/constant/button-signals.constant';
import { ExpansionPanelHeader } from '../../../../shared/common-components/expansion-panel-header/expansion-panel-header';
import { GenericModal } from '../../../../shared/common-components/generic-component-type/generic-modal/generic-modal';
import { InputSelectOptionField } from '../../../../shared/common-components/input-types/input-select-option-field/input-select-option-field';
import { InputTextBox } from '../../../../shared/common-components/input-types/input-text-box/input-text-box';
import { CentrinoConfigService } from '../../../../shared/services/centrino-config.service';
import { OrganizationBankInformationList } from '../organization-bank-information-list/organization-bank-information-list';
import { OrganizationBankInfoService } from '../../service/organization-bank-info.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-organization-bank-information',
  imports: [
    ReactiveFormsModule,
    ExpansionPanelHeader,
    GenericModal,
    InputSelectOptionField,
    InputTextBox,
  ],
  templateUrl: './organization-bank-information.html',
  styleUrl: './organization-bank-information.scss'
})
export class OrganizationBankInformation implements OnInit {
  frmGroup: FormGroup;
  isEdit = false;
  isUpdate = false;
  uuid: string | null = null;
  submittedData: any = null;
  organizationBankInformation = signal(true);
  isModalShow: boolean = false;
  organizationBankInfoListModal: any = null;
  organizationBranchOptions: any[] = [];
  organizationOptions: any[] = [];
  branchList: any[] = [];
  currencyOptions: any[] = [];

  branchNameOptions = [
    { key: 1, value: 'Motijheel Branch' },
    { key: 2, value: 'Gulshan Branch' },
    { key: 3, value: 'Dhanmondi Branch' },
    { key: 4, value: 'Uttara Branch' },
    { key: 5, value: 'Mirpur Branch' }
  ];

  bankNameOptions = [
    { key: 1, value: 'Sonali Bank Limited' },
    { key: 2, value: 'Janata Bank Limited' },
    { key: 3, value: 'Agrani Bank Limited' },
    { key: 4, value: 'Rupali Bank Limited' },
    { key: 5, value: 'Islami Bank Bangladesh Limited' }
  ];

  accountTypeOptions = [
    { key: 1, value: 'Savings Account' },
    { key: 2, value: 'Current Account' },
    { key: 3, value: 'salary Account' },
    { key: 4, value: 'Corporate Account' },
    { key: 5, value: 'Join Account' },
    { key: 6, value: 'Fixed Deposit Account' }
  ];

  // paymentMethodOptions = [
  //   { key: 1, value: 'Cash Deposit' },
  //   { key: 2, value: 'Cheque' },
  //   { key: 3, value: 'Online Transfer' },
  //   { key: 4, value: 'BEFTN' },
  //   { key: 5, value: 'RTGS' },
  //   { key: 6, value: 'Mobile Banking' },
  //   { key: 7, value: 'Card Payment' }
  // ];

  statusOptions = [
    { key: 'ACTIVE', value: 'Active' },
    { key: 'INACTIVE', value: 'Inactive' }
  ];

  constructor(
    private fb: FormBuilder,
    private toast: ToastHelperService,
    private centinoService: CentrinoConfigService,
    private organizationBankInfoService: OrganizationBankInfoService,
  ) {
    effect(() => {
      if (ONCLICK_SAVE()) {
        this.submit();
        ONCLICK_SAVE.set(false);
      }
    });

    effect(() => {
      if (ONCLICK_UPDATE()) {
        this.update();
        ONCLICK_UPDATE.set(false);
      }
    });

    effect(() => {
      if (ONCLICK_RESET()) {
        this.reset();
        ONCLICK_RESET.set(false);
      }
    });

    effect(() => {
      if (ONCLICK_VIEW()) {
        this.organizationBankInfoListModal = OrganizationBankInformationList;
        this.isModalShow = true;
        this.reset();
        ONCLICK_VIEW.set(false);
      }
    });
    this.buttonSetup();
  }

  ngOnInit() {
    this.populateForm();
    this.loadDrowpdownOptions();
  }

  private loadDrowpdownOptions(): void {
    forkJoin({
      companies: this.centinoService.getCompanyList(),
      currencies: this.centinoService.getCurrencyList()
    }).subscribe({
      next: ({ companies, currencies }) => {
        // Organization dropdown
        this.organizationOptions = (companies || []).map((i: any) => ({
          key: i.COMPANY_ID,
          value: i.COMPANY_NAME
        }));

        // Currency dropdown
        this.currencyOptions = (currencies || []).map((i: any) => ({
          key: i.CURRENCY_ID,
          value: i.CURRENCY_NM
        }));

        // set state data
        const data = history.state?.data;
        if (data) {
          this.loadDataFromList(data);
        }

      },
      error: (err) => {
        console.error('Dropdown load failed', err);
        // this.toast.error('Failed to load dropdown data');
      }
    });
  }

  private editData: any = null;

  private loadDataFromList(data: any) {
    if (!data) return;

    this.isEdit = true;
    this.uuid = data.uuid;

    // store full data for later use
    this.editData = data;

    this.frmGroup.patchValue({
      companyId: data.companyId,
      // branchId: data.branchId,
      routingNumber: data.routingNumber,
      bankName: data.bankName,
      bankBranch: data.bankBranch,
      accountType: data.accountType,
      accountName: data.accountName,
      accountNumber: data.accountNumber,
      currency: data.currency,
      glAccountNo: data.glAccountNo,
      status: data.status,
    });

    // trigger branch load
    this.onOrganizationChange(data.companyId);

    // Update button visibility
    BUTTON_VISIBILITY.set({
      save: false,
      update: true,
      view: true,
      delete: false,
      exit: false,
      reset: true,
    });

  }

  onOrganizationChange(organizationId: any): void {

    if (!organizationId) {
      this.organizationBranchOptions = [];
      this.branchList = [];
      this.frmGroup.get('branch')?.reset();
      this.frmGroup.get('routingNumber')?.reset();
      return;
    }

    // Call API to get branches for selected organization
    const payload = {
      pCompanyId: organizationId || ''
    };

    this.centinoService.getOfficeListByCompanyId(payload).subscribe({
      next: (res: any) => {
        if (res && Array.isArray(res)) {

          // store full response
          this.branchList = res;

          // map only what dropdown needs
          this.organizationBranchOptions = res.map((branch: any) => ({
            key: branch.OFFICE_ID || branch.id,
            value: branch.OFFICE_NAME || branch.name,
          }));

          // patch branchId AFTER options exist
          if (this.isEdit && this.editData?.branchId) {
            const branchExists = res.some(
              b => (b.OFFICE_ID || b.id) === this.editData.branchId
            );

            if (branchExists) {
              this.frmGroup.patchValue({
                branchId: this.editData.branchId
              });
            }
          }

        } else {
          this.organizationBranchOptions = [];
          this.branchList = [];
        }
      },
      error: (err: any) => {
        this.organizationBranchOptions = [];
        this.branchList = [];
        const errorMsg = err?.error?.detail || err?.error?.message || 'Failed to load branches';
        //this.toast.error(errorMsg);
      }
    });
  }

  onBranchChange(branchId: any): void {
    if (!branchId) {
      this.frmGroup.get('routingNumber')?.reset();
      return;
    }

    const selectedBranch = this.branchList.find(
      b => b.OFFICE_ID === branchId
    );

    if (selectedBranch?.OFFICE_ROUTING_NO) {
      this.frmGroup.patchValue({
        routingNumber: selectedBranch.OFFICE_ROUTING_NO
      });
    }
  }

  getModalResult(result: any) {
    if (result?.data) {
      this.isModalShow = false;
      this.loadDataFromList(result.data);
    }
  }

  buttonSetup() {
    BUTTON_VISIBILITY.set({
      save: !this.isEdit,
      update: this.isEdit,
      view: true,
      delete: false,
      exit: false,
      reset: true,
    });
  }

  populateForm() {
    this.frmGroup = this.fb.group({
      companyId: ['', Validators.required],
      branchId: ['', Validators.required],
      routingNumber: [
        '',
        [
          Validators.required,
          Validators.minLength(4),
          Validators.maxLength(12)
        ]
      ],
      bankName: ['', Validators.required],
      bankBranch: ['', Validators.required],
      accountType: ['', Validators.required],
      accountName: ['', Validators.required],
      accountNumber: [
        '',
        [
          Validators.required,
          Validators.minLength(10),
          Validators.maxLength(16)
        ]
      ],
      currency: ['', Validators.required],
      glAccountNo: [
        '',
        [
          Validators.required,
          Validators.minLength(20),
          Validators.maxLength(20)
        ]
      ],
      // paymentMethod: ['', Validators.required],
      status: ['', Validators.required],
    });
  }

  reset() {
    this.frmGroup.reset();
    this.organizationBranchOptions = [];
    this.branchList = [];
    this.isEdit = false;
    this.uuid = null;
    this.buttonSetup();
  }

  submit() {

    if (this.frmGroup.invalid) {
      this.frmGroup.markAllAsTouched();
      // this.toast.warning('Please fill all required fields');
      return;
    }

    const payload = this.frmGroup.value;

    this.organizationBankInfoService.create(payload).subscribe({
      next: (res: any) => {
        if (res?.status) {
          this.toast.success('Organization bank information created successfully');
          this.reset();
        } else {
          this.toast.error(res?.message || 'Failed to create organization bank information');
        }
      },
      error: (err: any) => {
        const errorMsg = err?.error?.detail || err?.error?.message || 'Failed to create organization bank information';
        this.toast.error(errorMsg);
        console.error('Create error:', err);
      }
    });
  }

  update() {

    if (this.frmGroup.invalid) {
      this.frmGroup.markAllAsTouched();
      this.toast.warning('Please fill all required fields');
      return;
    }

    if (!this.uuid) {
      this.toast.error('No record selected for update');
      return;
    }

    const payload = {
      uuid: this.uuid,
      ...this.frmGroup.value
    };

    this.organizationBankInfoService.update(payload).subscribe({
      next: (res: any) => {
        if (res?.status) {
          this.toast.success(res?.message || 'Organization bank information updated successfully');
          this.reset();
        } else {
          this.toast.error(res?.message || 'Failed to update organization bank information');
        }
      },
      error: (err: any) => {
        const errorMsg = err?.error?.detail || err?.error?.message || 'Failed to update organization bank information';
        this.toast.error(errorMsg);
        console.error('Update error:', err);
      }
    });
  }

}
