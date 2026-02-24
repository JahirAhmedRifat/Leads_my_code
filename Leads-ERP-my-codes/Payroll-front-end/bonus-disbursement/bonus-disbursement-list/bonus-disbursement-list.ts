import { Component, signal, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ExpansionPanelHeader } from '../../../../shared/common-components/expansion-panel-header/expansion-panel-header';
import { GenericButton } from '../../../../shared/common-components/generic-component-type/generic-button/generic-button';
import { GenericDataGrid } from '../../../../shared/common-components/generic-component-type/generic-data-grid';
import { InputSelectOptionField } from '../../../../shared/common-components/input-types/input-select-option-field/input-select-option-field';
import { InputDate } from '../../../../shared/common-components/input-types/input-date/input-date';
import { CentrinoConfigService } from '../../../../shared/services/centrino-config.service';
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
import { DEFAULT_PAGE, DEFAULT_SIZE } from '../../../../shared/constant/constant';
import { BonusDisbursementService } from '../../service/bonus-disbursement.service';
import { ToastHelperService } from '../../../../shared/services/toast-helper.service';
import { forkJoin, map, Observable, of, tap } from 'rxjs';
import { YearlyBonusSetupResponse, YearlyBonusSetupService } from '../../service/yearly-bonus-setup.service';

@Component({
  selector: 'app-bonus-disbursement-list',
  imports: [
    ReactiveFormsModule,
    ExpansionPanelHeader,
    GenericButton,
    GenericDataGrid,
    InputSelectOptionField,
    InputDate,
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    TextFieldModule,
  ],
  templateUrl: './bonus-disbursement-list.html',
  styleUrl: './bonus-disbursement-list.scss'
})
export class BonusDisbursementList implements OnInit {
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
  isEdit = signal(false);

  // Panel expansion states
  searchCriteria = signal(true);
  bonusDisbursementInfo = signal(true);
  organizationOptions: any[] = [];
  branchOptions: any[] = [];
  bonusYearOptions: any[] = [];
  bonusTypeOptions: any[] = [];
  branchMap = new Map<number, any[]>(); // companyId → array of branches
  private yearlyBonusSetups: YearlyBonusSetupResponse[] = [];

  selectedColumns = [
    'empOrgValue',
    'empBranchValue',
    'bonusYear',
    'bonusType',
    'bonusDate',
    'disbursementDate',
    'totalAmount'
  ];

  customColumnNames = {
    empOrgValue: 'Organization',
    empBranchValue: 'Branch',
    bonusYear: 'Bonus Year',
    bonusType: 'Bonus Type',
    bonusDate: 'Bonus Date',
    disbursementDate: 'Disbursement Date',
    totalAmount: 'Total Amount'
  }

  numberColumns = [
    'totalAmount'
  ];

  constructor(
    private fb: FormBuilder,
    private disbursementService: BonusDisbursementService,
    private toast: ToastHelperService,
    private centinoService: CentrinoConfigService,
    private bonusYearService: YearlyBonusSetupService
  ) { }

  ngOnInit() {
    this.populateForm();
    this.loadDrowpdownOptions();
  }

  populateForm() {
    this.frmGroup = this.fb.group({
      empOrgId: [''],
      empBranchId: [''],
      bonusYear: [''],
      bonusType: [''],
      bonusDate: [''],
    });
  }

  private loadDrowpdownOptions(): void {
    forkJoin({
      companies: this.centinoService.getCompanyList(),
      bonusYears: this.bonusYearService.getList(),
    }).subscribe({
      next: ({ companies, bonusYears }) => {

        // Organization dropdown
        this.organizationOptions = (companies || []).map((i: any) => ({
          key: i.COMPANY_ID,
          value: i.COMPANY_NAME
        }));

        // Bonus Year dropdown
        const content = bonusYears?.payload || [];
        this.yearlyBonusSetups = Array.isArray(content) ? content : [];
        const years = Array.from(
          new Set(
            this.yearlyBonusSetups
              .map((item: any) => item.bonusYear)
              .filter(Boolean)
          )
        ).sort((a, b) => Number(b) - Number(a));

        this.bonusYearOptions = years.map((year: number) => ({
          key: year,
          value: year
        }));

        this.loadData();
      },
      error: (err) => {
        console.error('Dropdown load failed', err);
        // this.toast.error('Failed to load dropdown data');
        this.yearlyBonusSetups = [];
        this.bonusYearOptions = [];
        this.loadData();
      }
    });
  }

  loadData(): void {
    this.disbursementService
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

    const companyIds = content.map((s) => Number(s.empOrgId));

    // preload all branches before mapping
    this.preloadBranches(companyIds).subscribe(() => {
      this.dataSource.set(
        content.map((s) => {
          //-- organization ---
          const organizationOption = this.organizationOptions.find((opt) => opt.key === Number(s.empOrgId));

          //-- branch ---
          const branches = this.branchMap.get(Number(s.empOrgId)) || [];
          const branchOption = branches.find((b) => b.OFFICE_ID === Number(s.empBranchId));

          return {
            ...s,
            empOrgValue: organizationOption?.value ?? 'N/A',
            empBranchValue: branchOption?.OFFICE_NAME ?? 'N/A',
            bonusYear: s.bonusYear,
            bonusType: s.bonusType,
            bonusDate: s.bonusDate,
            disbursementDate: s.disbursementDate,
            totalAmount: s.totalAmount,
          };
        })
      );

      this.totalElements = res?.payload?.totalElements ?? res?.totalElements ?? content.length;
    });
  }

  onOrganizationChange(organizationId: any): void {

    if (!organizationId) {
      this.branchOptions = [];
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
          this.branchOptions = res.map((branch: any) => ({
            key: branch.OFFICE_ID || branch.id,
            value: branch.OFFICE_NAME || branch.name,
          }));
        } else {
          this.branchOptions = [];
        }
      },
      error: (err: any) => {
        this.branchOptions = [];
        const errorMsg = err?.error?.detail || err?.error?.message || 'Failed to load branches';
        //this.toast.error(errorMsg);
      }
    });
  }

  onBonusYearChange(yearValue: any): void {

    const year = yearValue ? Number(yearValue) : null;
    const setupsForYear = year
      ? this.yearlyBonusSetups.filter((item) => Number(item.bonusYear) === year)
      : [];

    const typeMap = new Map<string, string>();
    setupsForYear.forEach((item) => {
      if (item?.bonusType) {
        const label = item.bonusTypeName || item.bonusType;
        typeMap.set(item.bonusType, label);
      }
    });

    this.bonusTypeOptions = Array.from(typeMap.entries()).map(([key, value]) => ({
      key,
      value
    }));

    //----- Optional / bonusType petch ---------
    // if (this.bonusTypeOptions.length === 1) {
    //   const onlyType = this.bonusTypeOptions[0].key;
    //   this.frmGroup.patchValue({ bonusType: onlyType }, { emitEvent: true });
    // } else {
    //   this.frmGroup.patchValue({ bonusType: '' }, { emitEvent: false });
    // }

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
      !formValue.empOrgId &&
      !formValue.empBranchId &&
      !formValue.bonusYear &&
      !formValue.bonusType &&
      !formValue.bonusDate
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
      !formValue.empOrgId &&
      !formValue.empBranchId &&
      !formValue.bonusYear &&
      !formValue.bonusType &&
      !formValue.bonusDate
    ) {
      this.loadData();
      return;
    }

    const payload = {
      empOrgId: formValue.empOrgId ? Number(String(formValue.empOrgId).trim()) : null,
      empBranchId: formValue.empBranchId ? Number(String(formValue.empBranchId).trim()) : null,
      bonusYear: formValue.bonusYear ? Number(String(formValue.bonusYear).trim()) : null,
      bonusType: formValue.bonusType ? formValue.bonusType.trim() : null,
      bonusDate: formValue.bonusDate ? formValue.bonusDate : null,
      pageIndex: DEFAULT_PAGE,
      pageSize: DEFAULT_SIZE,
    };

    // Call search API
    this.disbursementService.criteriaSearch(payload).subscribe({
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
    this.branchOptions = [];
    this.bonusTypeOptions = [];
    this.pageIndex = DEFAULT_PAGE;
    this.pageSize = DEFAULT_SIZE;
    this.loadData();
  }

}
