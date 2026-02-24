import { Component, effect, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ToastHelperService } from '../../../../shared/services/toast-helper.service';
import { BUTTON_VISIBILITY, ONCLICK_RESET, ONCLICK_SAVE, ONCLICK_UPDATE, ONCLICK_VIEW } from '../../../../shared/constant/button-signals.constant';
import { ExpansionPanelHeader } from '../../../../shared/common-components/expansion-panel-header/expansion-panel-header';
import { GenericModal } from '../../../../shared/common-components/generic-component-type/generic-modal/generic-modal';
import { InputSelectOptionField } from '../../../../shared/common-components/input-types/input-select-option-field/input-select-option-field';
import { InputTextBox } from '../../../../shared/common-components/input-types/input-text-box/input-text-box';
import { CentrinoConfigService } from '../../../../shared/services/centrino-config.service';
import { forkJoin } from 'rxjs';
import { OrgCashInfoService } from '../../service/org-cash-info.service';
import OrganizationCashInformationList from '../organization-cash-information-list/organization-cash-information-list';

@Component({
  selector: 'app-organization-bank-information',
  imports: [
    ReactiveFormsModule,
    ExpansionPanelHeader,
    GenericModal,
    InputSelectOptionField,
    InputTextBox,
  ],
  templateUrl: './organization-cash-information.html',
  styleUrl: './organization-cash-information.scss'
})
export class OrganizationCashInformation implements OnInit {
  frmGroup: FormGroup;
  isEdit = false;
  isUpdate = false;
  uuid: string | null = null;
  submittedData: any = null;
  organizationCashInformation = signal(true);
  isModalShow: boolean = false;
  organizationInfoModal: any = null;
  organizationBranchOptions: any[] = [];
  organizationOptions: any[] = [];
  branchList: any[] = [];

  statusOptions = [
    { key: 'ACTIVE', value: 'Active' },
    { key: 'INACTIVE', value: 'Inactive' }
  ];

  constructor(
    private fb: FormBuilder,
    private toast: ToastHelperService,
    private centinoService: CentrinoConfigService,
    private orgCashInfoService: OrgCashInfoService,
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
        this.organizationInfoModal = OrganizationCashInformationList;
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
      companies: this.centinoService.getCompanyList()
    }).subscribe({
      next: ({ companies }) => {

        // Organization dropdown
        this.organizationOptions = (companies || []).map((i: any) => ({
          key: i.COMPANY_ID,
          value: i.COMPANY_NAME
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
      cashRegName: data.cashRegName,
      cashRegNumber: data.cashRegNumber,
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
      cashRegName: ['', Validators.required],
      cashRegNumber: [
        '',
        [
          Validators.required,
          Validators.minLength(20),
          Validators.maxLength(20)
        ]
      ],
      status: ['', Validators.required],
    });
  }

  submit() {

    if (this.frmGroup.invalid) {
      this.frmGroup.markAllAsTouched();
      this.toast.warning('Please fill all required fields');
      return;
    }

    const payload = this.frmGroup.value;

    this.orgCashInfoService.create(payload).subscribe({
      next: (res: any) => {
        if (res?.status) {
          this.toast.success('Organization cash information created successfully');
          this.reset();
        } else {
          this.toast.error(res?.message || 'Failed to create organization cash information');
        }
      },
      error: (err: any) => {
        const errorMsg = err?.error?.detail || err?.error?.message || 'Failed to create organization cash information';
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

    this.orgCashInfoService.update(payload).subscribe({
      next: (res: any) => {
        if (res?.status) {
          this.toast.success(res?.message || 'Organization cash information updated successfully');
          this.reset();
        } else {
          this.toast.error(res?.message || 'Failed to update organization cash information');
        }
      },
      error: (err: any) => {
        const errorMsg = err?.error?.detail || err?.error?.message || 'Failed to update organization cash information';
        this.toast.error(errorMsg);
        console.error('Update error:', err);
      }
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

}
