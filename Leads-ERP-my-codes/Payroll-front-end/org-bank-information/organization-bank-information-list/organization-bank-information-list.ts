import { Component, EventEmitter, Input, OnInit, Output, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { GenericButton } from '../../../../shared/common-components/generic-component-type/generic-button/generic-button';
import { GenericDataGrid } from '../../../../shared/common-components/generic-component-type/generic-data-grid';
import { DEFAULT_PAGE, DEFAULT_SIZE } from '../../../../shared/constant/constant';
import { Router } from '@angular/router';
import { ToastHelperService } from '../../../../shared/services/toast-helper.service';
import { CentrinoConfigService } from '../../../../shared/services/centrino-config.service';
import { forkJoin, map, Observable, of, tap } from 'rxjs';
import { ConfirmationDialogue } from '../../../../shared/common-components/confirmation-dialogue/confirmation-dialogue';
import { InputSelectOptionField } from '../../../../shared/common-components/input-types/input-select-option-field/input-select-option-field';
import { OrganizationBankInfoService } from '../../service/organization-bank-info.service';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { TextFieldModule } from '@angular/cdk/text-field';
import { ExpansionPanelHeader } from '../../../../shared/common-components/expansion-panel-header/expansion-panel-header';

@Component({
  selector: 'app-organization-bank-information-list',
  imports: [
    ReactiveFormsModule,
    GenericButton,
    GenericDataGrid,
    ConfirmationDialogue,
    InputSelectOptionField,
    CommonModule,
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    TextFieldModule,
    ExpansionPanelHeader,
    GenericButton,
    GenericDataGrid,
    InputSelectOptionField,
    ConfirmationDialogue,
  ],
  templateUrl: './organization-bank-information-list.html',
  styleUrl: './organization-bank-information-list.scss'
})
export class OrganizationBankInformationList implements OnInit {
  frmGroup: FormGroup;
  pageIndex = DEFAULT_PAGE;
  pageSize = DEFAULT_SIZE;
  dataSource = signal<any[]>([]);
  totalElements = 0;
  @Output() modalResult = new EventEmitter<any>();
  @Input() isModalMode: boolean = false;
  showModal = false;
  modalComponent: any = null;
  modalComponentData: any = null;
  showDeleteConfirmation = signal(false);
  deleteItemData = signal<any>(null);
  isEdit = signal(false);

  // Panel expansion states
  organizationalBankInfoList = signal(true);
  searchCriteria = signal(true);
  organizationalBankInfoListGrid = signal(true);
  branchList: any[] = [];
  branchOptions: any[] = [];
  organizationBranchOptions: any[] = [];
  organizationOptions: any[] = [];
  currencyOptions: any[] = [];
  branchMap = new Map<number, any[]>(); // companyId → array of branches

  bankNameOptions = [
    { key: 1, value: 'Sonali Bank Limited' },
    { key: 2, value: 'Janata Bank Limited' },
    { key: 3, value: 'Agrani Bank Limited' },
    { key: 4, value: 'Rupali Bank Limited' },
    { key: 5, value: 'Islami Bank Bangladesh Limited' }
  ];

  branchNameOptions = [
    { key: 1, value: 'Motijheel Branch' },
    { key: 2, value: 'Gulshan Branch' },
    { key: 3, value: 'Dhanmondi Branch' },
    { key: 4, value: 'Uttara Branch' },
    { key: 5, value: 'Mirpur Branch' }
  ];

  accountTypeOptions = [
    { key: 1, value: 'Savings Account' },
    { key: 2, value: 'Current Account' },
    { key: 3, value: 'salary Account' },
    { key: 4, value: 'Corporate Account' },
    { key: 5, value: 'Join Account' },
    { key: 6, value: 'Fixed Deposit Account' }
  ];

  statusOptions = [
    { key: 'ACTIVE', value: 'Active' },
    { key: 'INACTIVE', value: 'Inactive' }
  ];

  selectedColumns = [
    'companyValue',
    'branchValue',
    'bankNameValue',
    'bankBranchValue',
    'accountTypeValue',
    'accountName',
    'currencyValue',
    'statusValue'
  ];

  customColumnNames = {
    companyValue: 'Organization',
    branchValue: 'Branch',
    bankNameValue: 'Bank Name',
    bankBranchValue: 'Bank Branch Name',
    accountTypeValue: 'Account Type',
    accountName: 'Account Name',
    currencyValue: 'Currency',
    statusValue: 'Status'
  }

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private organizationBankInfoService: OrganizationBankInfoService,
    private toast: ToastHelperService,
    private centinoService: CentrinoConfigService,

  ) { }

  ngOnInit() {
    this.populateForm();
    this.loadDrowpdownOptions();
  }

  populateForm() {
    this.frmGroup = this.fb.group({
      companyId: [""],
      branchId: [""],
      bankName: [""],
      bankBranch: [""],
    });
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

        this.loadData();
      },
      error: (err) => {
        console.error('Dropdown load failed', err);
        // this.toast.error('Failed to load dropdown data');
        this.loadData();
      }
    });
  }

  loadData(): void {
    this.organizationBankInfoService
      .getListWithPagination(this.pageIndex, this.pageSize)
      .subscribe({
        next: res => this.handleListResponse(res),
        error: err => {
          this.dataSource.set([]);
          this.totalElements = 0;
          this.toast.error('Server error! Please try again');
          console.error(err);
        }
      });
  }

  // ---------------- Preload branches ----------------
  private preloadBranches(companyIds: number[]): Observable<void> {
    const uniqueCompanyIds = [...new Set(companyIds)];

    const calls = uniqueCompanyIds
      .filter((id) => !this.branchMap.has(id))
      .map((id) =>
        this.centinoService.getOfficeListByCompanyId({ pCompanyId: id }).pipe(
          tap((res: any[]) => {
            this.branchMap.set(id, Array.isArray(res) ? res : []);
          })
        )
      );

    return calls.length ? forkJoin(calls).pipe(map(() => void 0)) : of(void 0);
  }

  // ---------------- Handle API list response ----------------
  private handleListResponse(res: any): void {

    const content = res?.payload?.content ?? res?.content ?? [];

    if (!Array.isArray(content) || !content.length) {
      this.dataSource.set([]);
      this.totalElements = res?.payload?.totalElements ?? res?.totalElements ?? 0;
      return;
    }

    const companyIds = content.map((s) => s.companyId);

    // preload all branches before mapping
    this.preloadBranches(companyIds).subscribe(() => {
      this.dataSource.set(
        content.map((s) => {
          //-- organization ---
          const organizationOption = this.organizationOptions.find((opt) => opt.key === s.companyId);
          const bankNameObj = this.bankNameOptions.find((opt) => opt.key === s.bankName);
          const branchNameObj = this.branchNameOptions.find((opt) => opt.key === s.bankBranch);
          const currencyObj = this.currencyOptions.find((opt) => opt.key === s.currency);
          const accountTypeObj = this.accountTypeOptions.find((opt) => opt.key === s.accountType);
          const statusObj = this.statusOptions.find((opt) => opt.key === s.status);

          //-- branch ---
          const branches = this.branchMap.get(s.companyId) || [];
          const branchOption = branches.find((b) => b.OFFICE_ID === s.branchId);

          return {
            ...s,
            companyIdKey: s.companyId,
            companyValue: organizationOption?.value ?? 'N/A',
            branchIdKey: s.branchId,
            branchValue: branchOption?.OFFICE_NAME ?? 'N/A',
            bankNameKey: s.bankName,
            bankNameValue: bankNameObj?.value ?? 'N/A',
            bankBranchKey: s.bankBranch,
            bankBranchValue: branchNameObj?.value ?? 'N/A',
            accountTypeKey: s.accountType,
            accountTypeValue: accountTypeObj?.value ?? 'N/A',
            currencyKey: s.currency,
            currencyValue: currencyObj?.value ?? 'N/A',
            statusKey: s.status,
            statusValue: statusObj?.value ?? 'N/A'
          };
        })
      );

      this.totalElements = res?.payload?.totalElements ?? res?.totalElements ?? content.length;
    });
  }

  onOrganizationChange(organizationId: any): void {
    if (!organizationId) {
      this.organizationBranchOptions = [];
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
          this.organizationBranchOptions = res.map((branch: any) => ({
            key: branch.OFFICE_ID || branch.id,
            value: branch.OFFICE_NAME || branch.name,
          }));
          //this.toast.success(`${this.organizationBranchOptions.length} branches loaded`);
        } else {
          this.organizationBranchOptions = [];
          //this.toast.warning('No branches found for this organization');
        }
      },
      error: (err: any) => {
        this.organizationBranchOptions = [];
        const errorMsg = err?.error?.detail || err?.error?.message || 'Failed to load branches';
        //this.toast.error(errorMsg);
      }
    });
  }

  onModalClose(result: any) {
    this.showModal = false;
    this.modalComponent = null;
    this.modalComponentData = null;
  }

  onPageChange(event: any): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    const formValue = this.frmGroup.value;

    if (
      !formValue.companyId &&
      !formValue.branchId &&
      !formValue.bankName &&
      !formValue.bankBranch
    ) {
      this.loadData();
    } else {
      this.search();
    }
  }

  search(): void {
    const formValue = this.frmGroup.value;

    // if no filter, load all
    if (
      !formValue.companyId &&
      !formValue.branchId &&
      !formValue.bankName &&
      !formValue.bankBranch
    ) {
      this.loadData();
      return;
    }

    const payload = {
      companyId: formValue.companyId ? Number(String(formValue.companyId).trim()) : null,
      branchId: formValue.branchId ? Number(String(formValue.branchId).trim()) : null,
      bankName: formValue.bankName ? Number(String(formValue.bankName).trim()) : null,
      bankBranch: formValue.bankBranch ? Number(String(formValue.bankBranch).trim()) : null,
      pageIndex: DEFAULT_PAGE,
      pageSize: DEFAULT_SIZE,
    };

    // Call search API
    this.organizationBankInfoService.search(payload).subscribe({
      next: (res) => this.handleListResponse(res),
      error: (err) => {
        this.dataSource.set([]);
        this.totalElements = 0;
        this.toast.error('Search failed! Please try again');
        console.error(err);
      }
    });
  }

  reset(): void {
    this.frmGroup.reset();
    this.organizationBranchOptions = [];
    this.branchList = [];
    this.pageIndex = DEFAULT_PAGE;
    this.pageSize = DEFAULT_SIZE;
    this.loadData();
  }

  editItem(event: any): void {
    const itemData = typeof event === 'string' ? JSON.parse(event) : event;

    // If being used in a modal, emit the result instead of navigating
    if (this.modalResult.observed) {
      this.modalResult.emit({
        data: itemData,
        isEdit: true,
      });
    } else {
      // Navigate to edit page with member data (standalone list page mode)
      this.router.navigate(['/payroll/organization-bank-information'], {
        state: { data: itemData }, // Fix key name
      });
    }
  }

  deleteItem(itemJson: any) {
    const element = JSON.parse(itemJson);
    this.deleteItemData.set(element);
    this.showDeleteConfirmation.set(true);
  }

  onDeleteConfirm(event: any) {
    if (event.action === 'confirm') {
      const element = this.deleteItemData();
      this.organizationBankInfoService.deleteOrganizationBankInfo(element.uuid).subscribe({
        next: (res: any) => {

          if (res?.status) {
            // UI remove
            const updatedData = this.dataSource().filter(
              (item) => item.uuid !== element.uuid
            );
            this.dataSource.set(updatedData);
            this.toast.success('Organization Bank Information deleted successfully');
          } else {
            //Backend responded but not success
            this.toast.error(res?.message || 'Failed to delete');
          }
        },
        error: (err) => {
          this.toast.error(
            err?.error?.message || 'Delete failed. Please try again'
          );
        }
      });
    }
    this.showDeleteConfirmation.set(false);
    this.deleteItemData.set(null);
  }

}
