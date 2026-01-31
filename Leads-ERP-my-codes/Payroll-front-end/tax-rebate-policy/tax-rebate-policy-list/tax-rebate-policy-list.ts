import { Component, EventEmitter, OnInit, Output, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { Router } from '@angular/router';
import { DEFAULT_PAGE, DEFAULT_SIZE } from '../../../../shared/constant/constant';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { TextFieldModule } from '@angular/cdk/text-field';
import { ExpansionPanelHeader } from '../../../../shared/common-components/expansion-panel-header/expansion-panel-header';
import { GenericButton } from '../../../../shared/common-components/generic-component-type/generic-button/generic-button';
import { InputTextBox } from '../../../../shared/common-components/input-types/input-text-box/input-text-box';
import { GenericDataGrid } from '../../../../shared/common-components/generic-component-type/generic-data-grid';
import { InputSelectOptionField } from '../../../../shared/common-components/input-types/input-select-option-field/input-select-option-field';
import { ToastHelperService } from '../../../../shared/services/toast-helper.service';
import { CentrinoConfigService } from '../../../../shared/services/centrino-config.service';
import { forkJoin } from 'rxjs';
import { TaxRebatePolicyService } from '../../services/tax-rebate-policy.service';
import { ConfirmationDialogue } from '../../../../shared/common-components/confirmation-dialogue/confirmation-dialogue';

@Component({
  selector: 'app-tax-rebate-policy-list',
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
    ConfirmationDialogue
  ],
  templateUrl: './tax-rebate-policy-list.html',
  styleUrl: './tax-rebate-policy-list.scss'
})
export class TaxRebatePolicyList implements OnInit {

  frmGroup: FormGroup;
  dataSource = signal<any[]>([]);
  totalElements = 0;
  pageSize = DEFAULT_SIZE;
  pageIndex = DEFAULT_PAGE;
  deleteItemData = signal<any>(null);
  showDeleteConfirmation = signal(false);
  @Output() modalResult = new EventEmitter<any>();
  // Modal state
  showModal = false;
  modalComponent: any = null;
  modalComponentData: any = null;
  businessHeaderPanel = signal(true);
  searchCriteriaPanel = signal(true);
  amountTypeList: any[] = [];
  transactionActionVisibility = (row: any) =>
    this.getTransactionActionVisibility(row);

  amountTypeOptions: any[] = [
    { key: '1', value: 'Taxable Income' },
    { key: '2', value: 'Investment Amount' },
    { key: '3', value: 'Max Rebate from Investment' }
  ];

  // Column configuration for data grid
  selectedColumns = [
    'policySerialNo',
    'taxYear',
    'amountTypeValue',
    'totalAmount'
  ];

  customColumnNames = {
    policySerialNo: 'Policy Serial No',
    taxYear: 'Tax Year',
    amountTypeValue: 'Amount Type',
    totalAmount: 'Value'
  };

  numberColumns = [
    'totalAmount'
  ];

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private toastHelper: ToastHelperService,
    private taxRebateService: TaxRebatePolicyService,
    private centinoService: CentrinoConfigService,
  ) { }

  ngOnInit() {
    this.populateForm();
    // this.loadDropdowns();
    this.loadData();
  }

  populateForm() {
    this.frmGroup = this.fb.group({
      taxYear: [''],
      amountType: ['']
    });
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
      }
    });
  }

  getCurrentYear(): number {
    return new Date().getFullYear();
  }


  private getTransactionActionVisibility(row: any) {
    const currentYear = this.getCurrentYear();
    const rowTaxYear = Number(row.taxYear);
    return {
      edit: true,
      delete: rowTaxYear >= currentYear
    };
  }

  loadData(): void {
    this.taxRebateService
      .getListWithPagination(this.pageIndex, this.pageSize)
      .subscribe(res => this.handleListResponse(res));
  }

  private handleListResponse(res: any): void {

    const content = res?.payload?.content ?? res?.content;

    if (!Array.isArray(content)) {
      this.dataSource.set([]);
      this.totalElements = res?.payload?.totalElements ?? res?.totalElements ?? 0;
      return;
    }

    this.dataSource.set(
      content.map(s => {
        const amountTypeOption = this.amountTypeOptions.find(
          opt => opt.key === s.amountType.toString()
        );

        return {
          ...s,
          amountTypeKey: s.amountType, // Keep the key
          amountTypeValue: amountTypeOption ? amountTypeOption.value : 'N/A' // Add display value
        };
      })
    );

    this.totalElements = res?.payload?.totalElements ?? res?.totalElements ?? content.length;

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

    if (!formValue.taxYear && !formValue.amountType) {
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
    if (!formValue.taxYear && !formValue.amountType) {
      this.loadData();
      return;
    }

    const payload = {
      taxYear: formValue.taxYear ? Number(formValue.taxYear) : null,
      amountType: formValue.amountType ? Number(formValue.amountType) : null,
      pageIndex: DEFAULT_PAGE,
      pageSize: DEFAULT_SIZE
    };

    this.taxRebateService
      .search(payload)
      .subscribe(res => this.handleListResponse(res));
  }

  onEditClick(event: any): void {
    const itemData = typeof event === 'string' ? JSON.parse(event) : event;

    // If being used in a modal, emit the result instead of navigating
    if (this.modalResult.observed) {
      this.modalResult.emit({ taxRebatePolicyData: itemData }); // Fix key name
    } else {
      // Navigate to edit page with member data (standalone list page mode)
      this.router.navigate(['/tax/tax-rebate-policy'], {
        state: { taxRebatePolicyData: itemData }, // Fix key name
      });
    }
  }

  onDeleteClick(itemJson: string) {
    const element = JSON.parse(itemJson);
    this.deleteItemData.set(element);
    this.showDeleteConfirmation.set(true);
  }

  onDeleteConfirm(event: any) {
    if (event.action === 'confirm') {
      const element = this.deleteItemData();
      this.taxRebateService.delete(element.uuid).subscribe({
        next: (res: any) => {

          if (res?.status) {
            // UI remove
            const updatedData = this.dataSource().filter(
              (item) => item.uuid !== element.uuid
            );
            this.dataSource.set(updatedData);
            this.toastHelper.success('Data deleted successfully');
          } else {
            //Backend responded but not success
            this.toastHelper.error(res?.message || 'Failed to delete');
          }
        },
        error: (err) => {
          this.toastHelper.error(
            err?.error?.message || 'Delete failed. Please try again'
          );
        }
      });
    }
    this.showDeleteConfirmation.set(false);
    this.deleteItemData.set(null);
  }

}
