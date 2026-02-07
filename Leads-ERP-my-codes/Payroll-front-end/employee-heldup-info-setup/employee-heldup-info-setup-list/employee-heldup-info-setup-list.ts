import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  signal,
} from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import {
  DEFAULT_PAGE,
  DEFAULT_SIZE,
} from '../../../../shared/constant/constant';
import { Router } from '@angular/router';
import { ToastHelperService } from '../../../../shared/services/toast-helper.service';
import { BUTTON_VISIBILITY } from '../../../../shared/constant/button-signals.constant';
import { EmployeeHeldupInfoSetupService } from '../../service/employee-heldup-info-setup.service';
import { ExpansionPanelHeader } from '../../../../shared/common-components/expansion-panel-header/expansion-panel-header';
import { ExpansionSubPanelHeader } from '../../../../shared/common-components/expansion-sub-panel-header/expansion-sub-panel-header';
import { InputTextBox } from '../../../../shared/common-components/input-types/input-text-box/input-text-box';
import { InputDate } from '../../../../shared/common-components/input-types/input-date/input-date';
import { InputSelectOptionField } from '../../../../shared/common-components/input-types/input-select-option-field/input-select-option-field';
import { GenericButton } from '../../../../shared/common-components/generic-component-type/generic-button/generic-button';
import { GenericDataGrid } from '../../../../shared/common-components/generic-component-type/generic-data-grid';
import { ConfirmationDialogue } from '../../../../shared/common-components/confirmation-dialogue/confirmation-dialogue';
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
import { CentrinoConfigService } from '../../../../shared/services/centrino-config.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-employee-heldup-info-setup-list',
  imports: [
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
    InputTextBox,
    GenericDataGrid,
    InputSelectOptionField,
    ConfirmationDialogue,
    InputDate,
  ],
  templateUrl: './employee-heldup-info-setup-list.html',
  styleUrl: './employee-heldup-info-setup-list.scss',
})
export class EmployeeHeldupInfoSetupList implements OnInit {
  frmGroup: FormGroup;
  today: Date = new Date();
  dataSource = signal<any[]>([]);
  totalElements = 0;
  pageSize = DEFAULT_SIZE;
  pageIndex = DEFAULT_PAGE;
  @Output() modalResult = new EventEmitter<any>();
  @Input() isModalMode: boolean = false;
  showModal = false;
  modalComponent: any = null;
  modalComponentData: any = null;
  businessHeaderPanel = signal(true);
  searchCriteriaPanel = signal(true);
  heldupTypeList: any[] = [];

  heldupType: any[] = [
    { key: 1, value: 'Salary Heldup' },
    { key: 2, value: 'Bonus Heldup' },
    { key: 3, value: 'Increment Heldup' },
    { key: 4, value: 'LFA Provision Heldup' },
    { key: 5, value: 'LFA Distribution Heldup' },
  ];

  selectedColumns = [
    'employeeId',
    'heldupTypeValue',
    'fromDate',
    'toDate'
  ];

  customColumnNames = {
    employeeId: 'Employee Id',
    heldupTypeValue: 'Heldup Type',
    fromDate: 'From Date',
    toDate: 'To Date',
  };

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private toastHelper: ToastHelperService,
    private heldupService: EmployeeHeldupInfoSetupService,
    private centinoService: CentrinoConfigService,
  ) { }

  ngOnInit() {
    this.populateForm();
    // this.loadDropdowns();
    this.loadData();
  }

  populateForm() {
    this.frmGroup = this.fb.group({
      employeeId: [''],
      heldupType: [''],
      fromDate: [''],
      toDate: [''],
    });
  }

  // Get minimum To Date based on From Date selection
  getMinToDate(): Date | null {
    const reqDate = this.frmGroup.get('fromDate')?.value;
    if (!reqDate) {
      return this.today;
    }

    const minDate = new Date(reqDate);  // present + Next day
    minDate.setHours(0, 0, 0, 0);
    // minDate.setDate(minDate.getDate() + 1);  // Next day
    return minDate;
  }

  loadDropdowns() {
    forkJoin({
      // vehicleType: this.centrinoService.getVehicleTypeList()
    }).subscribe({
      next: ({ vehicleType }) => {
        // this.vehicleTypeList = vehicleType?.payload || [];

        // this.amountTypeOptions = this.vehicleTypeList.map(vt => ({
        //   key: vt.id.toString(),
        //   value: vt.nameEn
        // }));

        this.loadData();
      },
      error: () => {
        // this.vehicleTypeList = [];
        // this.amountTypeOptions = [];
        this.loadData();
      },
    });
  }

  loadData(): void {
    this.heldupService
      .getListWithPagination(this.pageIndex, this.pageSize)
      .subscribe((res) => this.handleListResponse(res));
  }

  private handleListResponse(res: any): void {
    const content = res?.payload?.content ?? res?.content;

    if (!Array.isArray(content)) {
      this.dataSource.set([]);
      this.totalElements =
        res?.payload?.totalElements ?? res?.totalElements ?? 0;
      return;
    }

    this.dataSource.set(
      content.map((s) => {
        const heldupTypeOption = this.heldupType.find(
          (opt) => opt.key === s.heldupType,
        );

        return {
          ...s,
          heldupTypeKey: s.heldupType, // Keep the key
          heldupTypeValue: heldupTypeOption ? heldupTypeOption.value : 'N/A', // Add display value
        };
      }),
    );

    this.totalElements =
      res?.payload?.totalElements ?? res?.totalElements ?? content.length;
  }

  // getVehicleTypeList(): Observable<any> {
  //   return this.httpClient.get<CommonResponseObject<any>>(this._BASE_URL + `vehicle-type`, {});
  // }

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
      !formValue.employeeId &&
      !formValue.heldupType &&
      !formValue.fromDate &&
      !formValue.toDate
    ) {
      this.loadData();
    } else {
      this.search();
    }
  }

  reset(): void {
    this.frmGroup.reset();
    this.pageIndex = DEFAULT_PAGE;
    this.pageSize = DEFAULT_SIZE;
    this.loadData();
  }

  search(): void {
    const formValue = this.frmGroup.value;

    // if no filter, load all
    if (
      !formValue.employeeId &&
      !formValue.heldupType &&
      !formValue.fromDate &&
      !formValue.toDate
    ) {
      this.loadData();
      return;
    }

    const payload = {
      employeeId: formValue.employeeId ? formValue.employeeId : null,
      heldupType: formValue.heldupType ? Number(formValue.heldupType) : null,
      fromDate: formValue.fromDate ? formValue.fromDate : null,
      toDate: formValue.toDate ? formValue.toDate : null,
      pageIndex: DEFAULT_PAGE,
      pageSize: DEFAULT_SIZE,
    };

    this.heldupService
      .search(payload)
      .subscribe((res) => this.handleListResponse(res));
  }

  onEditClick(itemJson: string): void {

    const element = JSON.parse(itemJson);

    this.heldupService.getHeldupDetailsData(element.uuid).subscribe({
      next: (res: any) => {
        if (res?.status) {

          const payload = res.payload;

          if (this.modalResult.observed) {
            this.modalResult.emit({
              heldupData: payload,
              isEdit: true,
            });
          } else {
            // Navigate to form page
            this.router.navigate(['/payroll/employee-heldup-info-setup'], {
              state: { heldupData: payload },
            });
          }
        } else {
          this.toastHelper.error(
            res?.message || 'Failed to fetch heldup details',
          );
        }
      },
      error: (err: any) => {
        console.error('Error fetching heldup details:', err);
      },
    });
  }

}
