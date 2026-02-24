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
import { InputTextBox } from '../../../../shared/common-components/input-types/input-text-box/input-text-box';
import { OrgCashInfoService } from '../../service/org-cash-info.service';

@Component({
  selector: 'app-organization-bank-information-list',
  imports: [
    ReactiveFormsModule,
    GenericButton,
    GenericDataGrid,
    ConfirmationDialogue,
    InputSelectOptionField,
    InputTextBox,
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
  templateUrl: './organization-cash-information-list.html',
  styleUrl: './organization-cash-information-list.scss'
})
export default class OrganizationCashInformationList implements OnInit {
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
  orgCashInfo = signal(true);
  branchList: any[] = [];
  branchOptions: any[] = [];
  organizationBranchOptions: any[] = [];
  organizationOptions: any[] = [];
  branchMap = new Map<number, any[]>(); // companyId → array of branches

  statusOptions = [
    { key: 'ACTIVE', value: 'Active' },
    { key: 'INACTIVE', value: 'Inactive' }
  ];

  selectedColumns = [
    'companyValue',
    'branchValue',
    'cashRegName',
    'cashRegNumber',
    'statusValue'
  ];

  customColumnNames = {
    companyValue: 'Organization',
    branchValue: 'Branch',
    cashRegName: 'Cash Registration Name',
    cashRegNumber: 'Cash Registration Number',
    statusValue: 'Status'
  }

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private orgCashInfoService: OrgCashInfoService,
    private toast: ToastHelperService,
    private centinoService: CentrinoConfigService
  ) { }

  ngOnInit() {
    this.populateForm();
    this.loadDrowpdownOptions();
  }

  populateForm() {
    this.frmGroup = this.fb.group({
      companyId: [''],
      branchId: [''],
      cashRegName: [''],
      cashRegNumber: [''],
    });
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
    this.orgCashInfoService
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
            cashRegName: s.cashRegName,
            cashRegNumber: s.cashRegNumber,
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
      !formValue.cashRegName &&
      !formValue.cashRegNumber
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
      !formValue.cashRegName &&
      !formValue.cashRegNumber
    ) {
      this.loadData();
      return;
    }

    const payload = {
      companyId: formValue.companyId ? Number(String(formValue.companyId).trim()) : null,
      branchId: formValue.branchId ? Number(String(formValue.branchId).trim()) : null,
      cashRegName: formValue.cashRegName ? formValue.cashRegName.trim() : null,
      cashRegNumber: formValue.cashRegNumber ? formValue.cashRegNumber.trim() : null,
      pageIndex: DEFAULT_PAGE,
      pageSize: DEFAULT_SIZE,
    };

    // Call search API
    this.orgCashInfoService.criteriaSearch(payload).subscribe({
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
      this.router.navigate(['/payroll/organization-cash-information'], {
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
      this.orgCashInfoService.delete(element.uuid).subscribe({
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
