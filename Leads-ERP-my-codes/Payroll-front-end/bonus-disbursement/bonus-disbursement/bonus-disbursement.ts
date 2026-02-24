import { Component, signal, Input, Output, EventEmitter, OnInit, effect } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ExpansionPanelHeader } from '../../../../shared/common-components/expansion-panel-header/expansion-panel-header';
import { GenericButton } from '../../../../shared/common-components/generic-component-type/generic-button/generic-button';
import { GenericDataGrid } from '../../../../shared/common-components/generic-component-type/generic-data-grid';
import { InputSelectOptionField } from '../../../../shared/common-components/input-types/input-select-option-field/input-select-option-field';
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
import { BonusProcessingService } from '../../service/bonus-processing.service';
import { GenericModal } from '../../../../shared/common-components/generic-component-type/generic-modal/generic-modal';
import { BUTTON_VISIBILITY, ONCLICK_RESET, ONCLICK_SAVE, ONCLICK_VIEW } from '../../../../shared/constant/button-signals.constant';
import { BonusDisbursementList } from '../bonus-disbursement-list/bonus-disbursement-list';

@Component({
  selector: 'app-bonus-disbursement',
  imports: [
    ReactiveFormsModule,
    ExpansionPanelHeader,
    GenericButton,
    GenericDataGrid,
    InputSelectOptionField,
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
    GenericModal
  ],
  templateUrl: './bonus-disbursement.html',
  styleUrl: './bonus-disbursement.scss'
})
export class BonusDisbursement implements OnInit {

  @Input() viewObject: any | null;
  @Input() isViewDetails: boolean | false;
  @Input() highlightMap: Record<string, boolean> | null = null;
  @Input() isApprovalView: boolean = false;

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
  isEdit = false;
  isUpdate = false;
  uuid: string | null = null;
  submittedData: any = null;
  isModalShow: boolean = false;
  bonusDisbursementModal: any = null;

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
    'employeeId',
    'bonusAmount',
    'bonusYear',
    'bonusType',
    'bonusDate'
  ];

  customColumnNames = {
    empOrgValue: 'Organization',
    empBranchValue: 'Branch',
    employeeId: 'Employee Id',
    bonusAmount: 'Bonus Amount',
    bonusYear: 'Bonus Year',
    bonusType: 'Bonus Type',
    bonusDate: 'Bonus Date'
  }

  numberColumns = [
    'bonusAmount'
  ];

  constructor(
    private fb: FormBuilder,
    private disbursementService: BonusDisbursementService,
    private toast: ToastHelperService,
    private centinoService: CentrinoConfigService,
    private bonusYearService: YearlyBonusSetupService,
    private bonusProcessingService: BonusProcessingService
  ) {
    effect(() => {
      if (ONCLICK_SAVE()) {
        this.submit();
        ONCLICK_SAVE.set(false);
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
        this.bonusDisbursementModal = BonusDisbursementList;
        this.isModalShow = true;
        // this.reset();
        ONCLICK_VIEW.set(false);
      }
    });
    this.buttonSetup();
  }

  ngOnInit() {
    this.populateForm();
    this.loadDrowpdownOptions();

    if (this.isViewDetails) {
      this.frmGroup.disable();

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
        this.loadViewData(this.viewObject);
      }
    }
  }

  private loadViewData(viewObject: any): void {
    if (!viewObject) return;

    // Patch master form fields
    this.frmGroup.patchValue({
      empOrgId: viewObject.empOrgId,
      empBranchId: viewObject.empBranchId,
      bonusYear: viewObject.bonusYear,
      bonusType: viewObject.bonusType,
    });

    // Preload branches for this organization
    this.preloadBranches([viewObject.empOrgId]).subscribe(() => {
      const orgName = this.organizationOptions.find(o => o.key === viewObject.empOrgId)?.value ?? 'N/A';
      const branchName = (this.branchMap.get(viewObject.empOrgId) || [])
        .find(b => b.OFFICE_ID === viewObject.empBranchId)?.OFFICE_NAME ?? 'N/A';

      // Flatten bonusDisbursementDetails for grid
      const rows = (viewObject.bonusDisbursementDetails || []).map((detail: any) => ({
        ...viewObject,
        empOrgValue: orgName,
        empBranchValue: branchName,
        employeeId: detail.employeeId,
        bonusAmount: detail.bonusAmount,
        bonusYear: viewObject.bonusYear,
        bonusType: viewObject.bonusType,
        bonusDate: viewObject.bonusDate
      }));

      this.dataSource.set(rows);
      this.totalElements = rows.length;
    });
  }

  populateForm() {
    this.frmGroup = this.fb.group({
      empOrgId: ['', Validators.required],
      empBranchId: [''],
      bonusYear: ['', Validators.required],
      bonusType: ['', Validators.required],
      // bonusDate: [''],
    });
  }

  buttonSetup() {
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
        save: true,
        update: false,
        view: true,
        delete: false,
        exit: false,
        reset: true,
      });
    }

  }

  getModalResult(result: any) {
    if (result?.data) {
      this.isModalShow = false;
    }
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
        // const content = bonusYears?.payload?.content || [];
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
      },
      error: (err) => {
        console.error('Dropdown load failed', err);
        this.yearlyBonusSetups = [];
        this.bonusYearOptions = [];
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
      this.toast.error(res.message);
      return;
    }

    const companyIds = content.map((s) => Number(s.empOrgId));

    // preload all branches before mapping
    this.preloadBranches(companyIds).subscribe(() => {

      // Flatten master + detail
      const rows = content.flatMap((master) => {
        const organizationOption = this.organizationOptions.find(
          (opt) => opt.key === Number(master.empOrgId)
        );

        const branches = this.branchMap.get(Number(master.empOrgId)) || [];
        const branchOption = branches.find((b) => b.OFFICE_ID === Number(master.empBranchId));

        // Map each detail to a separate row
        return master.bonusProfileDetails.map((detail: any) => ({
          ...master,
          empOrgValue: organizationOption?.value ?? 'N/A',
          empBranchValue: branchOption?.OFFICE_NAME ?? 'N/A',
          employeeId: detail.employeeId,
          bonusAmount: detail.bonusAmount
        }));
      });

      this.dataSource.set(rows);

      // Total elements = total details count
      const totalDetails = content.reduce(
        (sum, m) => sum + (m.bonusProfileDetails?.length ?? 1),
        0
      );

      this.totalElements = totalDetails;
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
    if (this.bonusTypeOptions.length === 1) {
      const onlyType = this.bonusTypeOptions[0].key;
      this.frmGroup.patchValue({ bonusType: onlyType }, { emitEvent: true });
    } else {
      this.frmGroup.patchValue({ bonusType: '' }, { emitEvent: false });
    }

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
      this.reset();
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
      !formValue.bonusType
    ) {
      // this.loadData();
      this.frmGroup.markAllAsTouched();
      this.toast.warning('Please fill all required fields');
      return;
    }

    const payload = {
      empOrgId: formValue.empOrgId ? String(formValue.empOrgId).trim() : null,
      empBranchId: formValue.empBranchId ? String(formValue.empBranchId).trim() : null,
      bonusYear: formValue.bonusYear ? Number(String(formValue.bonusYear).trim()) : null,
      bonusType: formValue.bonusType ? formValue.bonusType.trim() : null,
      pageIndex: DEFAULT_PAGE,
      pageSize: DEFAULT_SIZE,
    };

    // Call search API
    this.bonusProcessingService.getDataForBonusDisbursement(payload).subscribe({
      next: (res) => this.handleListResponse(res),
      error: (err: any) => {
        this.dataSource.set([]);
        this.totalElements = 0;
        const errorMsg = err?.error?.detail || err?.error?.message || 'No Data found';
        this.toast.error(errorMsg);
        console.error(err);
      }
    });
  }

  submit(): void {
    const gridData = this.dataSource();

    if (!gridData || !gridData.length) {
      this.toast.error('No data to submit');
      return;
    }

    // Group by master fields
    const grouped = gridData.reduce((acc, row) => {
      const key = `${row.empOrgId}|${row.empBranchId}|${row.bonusYear}|${row.bonusType}|${row.bonusDate}`;
      if (!acc[key]) {
        acc[key] = {
          empOrgId: row.empOrgId,
          empBranchId: row.empBranchId,
          bonusYear: row.bonusYear,
          bonusType: row.bonusType,
          bonusDate: row.bonusDate,
          bonusDisbursementDetails: []
        };
      }

      acc[key].bonusDisbursementDetails.push({
        employeeId: row.employeeId,
        bonusAmount: row.bonusAmount
      });

      return acc;
    }, {} as Record<string, any>);

    const payload = Object.values(grouped);

    this.disbursementService.bulkSave(payload).subscribe({
      next: (res: any) => {
        if (res?.status) {
          this.toast.success('Bonus disbursement created successfully');
          this.reset();
        } else {
          this.toast.error(res?.message || 'Failed to create bonus disbursement');
        }
      },
      error: (err: any) => {
        const errorMsg = err?.error?.detail || err?.error?.message || 'Failed to create bonus disbursement';
        this.toast.error(errorMsg);
        console.error('Create error:', err);
      }
    });
  }

  reset(): void {
    this.frmGroup.reset();
    this.branchOptions = [];
    this.bonusTypeOptions = [];
    this.dataSource.set([]);
    this.pageIndex = DEFAULT_PAGE;
    this.pageSize = DEFAULT_SIZE;
  }

}
