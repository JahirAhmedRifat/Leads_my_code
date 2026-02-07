import { Component, effect, Input, OnInit, signal } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { forkJoin, Subject, takeUntil } from 'rxjs';
import {
  GenericSearchConfig,
  GenericSearchModal,
} from '../../../../shared/common-components/generic-component-type/generic-search-modal/generic-search-modal';
import { Router } from '@angular/router';
import { ToastHelperService } from '../../../../shared/services/toast-helper.service';
import { EmployeeService } from '../../../tax/service/employee-service.service';
import { CentrinoConfigService } from '../../../../shared/services/centrino-config.service';
import {
  BUTTON_VISIBILITY,
  ONCLICK_RESET,
  ONCLICK_SAVE,
  ONCLICK_UPDATE,
  ONCLICK_VIEW,
} from '../../../../shared/constant/button-signals.constant';
import { EmployeeHeldupInfoSetupList } from '../employee-heldup-info-setup-list/employee-heldup-info-setup-list';
import { GenericModal } from '../../../../shared/common-components/generic-component-type/generic-modal/generic-modal';
import { InputSearchBox } from '../../../../shared/common-components/input-types/input-search-box/input-search-box';
import { InputSelectOptionField } from '../../../../shared/common-components/input-types/input-select-option-field/input-select-option-field';
import { InputDate } from '../../../../shared/common-components/input-types/input-date/input-date';
import { InputDisplayField } from '../../../../shared/common-components/input-types/input-display-field/input-display-field';
import { InputTextArea } from '../../../../shared/common-components/input-types/input-text-area/input-text-area';
import { ExpansionPanelHeader } from '../../../../shared/common-components/expansion-panel-header/expansion-panel-header';
import { EmployeeHeldupInfoSetupService } from '../../service/employee-heldup-info-setup.service';
import { CommonModule } from '@angular/common';
import { GenericDataGrid } from '../../../../shared/common-components/generic-component-type/generic-data-grid';
import {
  DEFAULT_PAGE,
  DEFAULT_SIZE,
} from '../../../../shared/constant/constant';

@Component({
  selector: 'app-employee-heldup-info-setup',
  imports: [
    ReactiveFormsModule,
    GenericModal,
    InputSearchBox,
    InputSelectOptionField,
    InputDate,
    InputDisplayField,
    InputTextArea,
    ExpansionPanelHeader,
    CommonModule,
    GenericDataGrid,
  ],
  templateUrl: './employee-heldup-info-setup.html',
  styleUrl: './employee-heldup-info-setup.scss',
})
export class EmployeeHeldupInfoSetup implements OnInit {
  private destroy$ = new Subject<void>();
  frmGroup: FormGroup;
  employeeFrm!: FormGroup;
  today: Date = new Date();
  isUpdate = false;
  isEdit = false;
  uuid: string | null = null;
  dataSource = signal<any[]>([]);
  pageSize = DEFAULT_SIZE;
  pageIndex = DEFAULT_PAGE;
  employeeInformationSetup = signal(true);
  heldupSetup = signal(true);
  isModalShow: boolean = false;
  memberListModal: any = null;
  employeeSearchModalData: any = null;
  selectedEmployee = signal<any>(null);
  showEmployeeModal = signal(false);
  EmployeeSearchModal = GenericSearchModal;
  salaryBreakups = signal<any[]>([]);
  selectedRows = signal<Set<string>>(new Set());

  heldupType: any[] = [
    { key: 1, value: 'Salary Heldup' },
    { key: 2, value: 'Bonus Heldup' },
    { key: 3, value: 'Increment Heldup' },
    { key: 4, value: 'LFA Provision Heldup' },
    { key: 5, value: 'LFA Distribution Heldup' },
  ];

  selectedColumns = ['salaryHead', 'amount', 'value'];

  customColumnNames = {
    salaryHead: 'Salary Head',
    amount: 'Amount',
    value: 'Heldup Value(%)',
  };

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private toast: ToastHelperService,
    private employeeService: EmployeeService,
    private centinoService: CentrinoConfigService,
    private employeeHeldupInfoSetupService: EmployeeHeldupInfoSetupService
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
        this.memberListModal = EmployeeHeldupInfoSetupList;
        this.isModalShow = true;
        this.reset();
        ONCLICK_VIEW.set(false);
      }
    });
    this.buttonSetup();
  }

  ngOnInit() {
    this.populateForm();
    this.buttonSetup();
    this.loadEmployeeSearchOptions();

    const heldupData = history.state?.heldupData;
    if (heldupData) {
      this.loadHeldupDataFromList(heldupData);
    }

    if (this.isEdit === false) {
      //--- when heldupType tigger------
      this.frmGroup
        .get('heldupType')
        ?.valueChanges.pipe(takeUntil(this.destroy$))
        .subscribe((type) => {
          if (type === 1) {
            // 1 = Salary Heldup
            this.loadEmployeeSalaryBreakup();
          } else {
            this.salaryBreakups.set([]);
            this.dataSource.set([]);
          }
        });
    }
    
  }

  private loadHeldupDataFromList(heldupData: any) {
    if (!heldupData) return;

    this.isEdit = true;
    this.uuid = heldupData.uuid;

    // Patch main form
    this.frmGroup.patchValue({
      heldupType: Number(heldupData.heldupType),
      fromDate: new Date(heldupData.fromDate),
      toDate: new Date(heldupData.toDate),
      remark: heldupData.remarks || '',
    },
      // { emitEvent: false } //heldupType-valueChanges is not calling
    );

    // Force a change detection cycle
    // setTimeout(() => {
    //   // Manually trigger the display update
    //   const control = this.frmGroup.get('heldupType');
    //   if (control) {
    //     control.updateValueAndValidity({ onlySelf: true, emitEvent: true });
    //   }
    // }, 0);

    // Patch employee form
    if (heldupData.employeeId) {
      this.employeeService
        .getEmployeeBasicInfo(heldupData.employeeId)
        .subscribe({
          next: (res: any) => {
            if (res?.status && res.payload) {
              const employee = {
                employeeId: heldupData.employeeId,
                name: res.payload.employeeNameEn || res.payload.name,
                designation: this.getDesignationName(res.payload.corporateDesignation),
                department: this.getDepartmentName(res.payload.departmentId),
              };
              this.onEmployeeSelected(employee);
            } else {
              this.toast.warning('Employee info not found for heldup record');
            }
          },
          error: () => {
            this.toast.error('Failed to fetch employee information');
          },
        });
    }

    // Patch datagrid if heldupDetails exist
    if (heldupData.heldupDetails && heldupData.heldupDetails.length) {

      const gridData = heldupData.heldupDetails.map((item: any) => ({
        uuid: item.uuid,
        id: item.salaryStructureBreakupId,
        salaryHead: item.salaryHead,
        amount: item.amount,
        value: item.heldupValue,
        _selected: true,
      }));

      // Update salaryBreakups signal as well
      const salaryBreakups = heldupData.heldupDetails.map((item: any) => ({
        salaryStructureBreakup: {
          id: item.salaryStructureBreakupId,
          breakupName: item.salaryHead,
          uuid: item.uuid,
        },
        finalAmount: item.amount,
      }));
      this.salaryBreakups.set(salaryBreakups);

      // Add dynamic controls for percentage if not exists
      salaryBreakups.forEach((item: any, index: number) => {
        const controlName = 'percentage_' + item.salaryStructureBreakup.uuid;
        if (!this.frmGroup.get(controlName)) {
          this.frmGroup.addControl(
            controlName,
            this.fb.control(
              {
                value: heldupData.heldupDetails[index].heldupValue,
                disabled: true,
              },
              [
                Validators.min(0),
                Validators.max(100),
              ]),
          );
        }
      });

      this.dataSource.set(gridData);
    }

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

  // From Date selection
  getMinFromDate(): Date {
    const today = new Date();

    // Previous month
    const minDate = new Date(today.getFullYear(), today.getMonth() - 1, 1);

    // Normalize time
    minDate.setHours(0, 0, 0, 0);

    return minDate;
  }


  // Get minimum To Date based on From Date selection
  getMinToDate(): Date | null {

    const reqDate = this.frmGroup.get('fromDate')?.value;

    if (!reqDate) {
      // return this.today;
      return null;
    }

    const minDate = new Date(reqDate);  // present + Next day
    minDate.setHours(0, 0, 0, 0);
    // minDate.setDate(minDate.getDate() + 1);  // Next day
    return minDate;
  }

  populateForm() {
    this.frmGroup = this.fb.group({
      heldupType: ['', [Validators.required]],
      fromDate: ['', [Validators.required]],
      toDate: ['', [Validators.required]],
      remark: [''],
    });
    this.employeeFrm = this.fb.group({
      id: ['', Validators.required],
      name: [{ value: '', disabled: true }],
      designation: [{ value: '', disabled: true }],
      department: [{ value: '', disabled: true }],
    });
  }

  private loadEmployeeSalaryBreakup(): void {

    const employeeId = this.employeeFrm.get('id')?.value;

    if (!employeeId) {
      // this.toast.warning('Please select employee first');
      this.employeeFrm.markAllAsTouched();
      return;
    }

    const payload = {
      referenceCodes: [employeeId]
    };

    this.employeeHeldupInfoSetupService
      .getEmployeeSalaryBreakup(payload)
      .subscribe({
        next: (res: any) => {
          const breakups = res?.payload?.[0]?.breakups || [];
          this.salaryBreakups.set(breakups);

          breakups.forEach((item: any) => {
            const controlName =
              'percentage_' + item.salaryStructureBreakup.uuid;
            if (!this.frmGroup.get(controlName)) {
              this.frmGroup.addControl(
                controlName,
                this.fb.control({ value: 100, disabled: true }, [
                  Validators.min(0),
                  Validators.max(100),
                ]),
              );
            }
          });

          this.buildGridData();
        },
        error: () => {
          this.salaryBreakups.set([]);
          this.dataSource.set([]);
        },
      });
  }

  private buildGridData(): void {
    const rows = this.salaryBreakups().map((item) => ({
      id: item.salaryStructureBreakup.id,
      salaryHead: item.salaryStructureBreakup.breakupName,
      amount: item.finalAmount,
      value:
        this.frmGroup.get('percentage_' + item.salaryStructureBreakup.uuid)
          ?.value ?? 100,
      _selected: this.selectedRows().has(item.salaryStructureBreakup.uuid),
    }));

    this.dataSource.set(rows);
  }

  isSelected(uuid: string): boolean {
    return this.selectedRows().has(uuid);
  }

  onGridDataChanged(updatedRows: any[]): void {
    let hasError = false;

    const validatedRows = updatedRows.map((row) => {
      let value = Number(row.value);

      if (value > 100) {
        value = 100;
        hasError = true;
      }

      if (value < 0) {
        value = 100;
        hasError = true;
      }

      // sync form control
      const controlName = 'percentage_' + row.uuid;
      this.frmGroup.get(controlName)?.setValue(value, { emitEvent: false });

      return {
        ...row,
        value,
      };
    });

    if (hasError) {
      this.toast.warning('Heldup value must be between 0% and 100%');
    }

    this.dataSource.set(validatedRows);
  }

  submit() {
    if (!this.frmGroup.valid || !this.employeeFrm.valid) {
      this.frmGroup.markAllAsTouched();
      this.employeeFrm.markAllAsTouched();
      // this.toast.error('Please fill all required fields');
      return;
    }

    const formValue = this.frmGroup.value;
    const empData = this.employeeFrm.getRawValue();

    const payload: any = {
      employeeId: empData.id,
      heldupType: Number(formValue.heldupType),
      // fromDate: this.formatDate(formValue.fromDate),
      // toDate: this.formatDate(formValue.toDate),
      fromDate: formValue.fromDate,
      toDate: formValue.toDate,
      remarks: formValue.remark || '',
    };

    if (this.dataSource().length) {
      payload.heldupDetails = this.dataSource().map((row) => ({
        salaryStructureBreakupId: row.id,
        salaryHead: row.salaryHead,
        amount: row.amount,
        heldupValue: Number(row.value),
      }));
    }

    this.employeeHeldupInfoSetupService.createHeldupInfo(payload).subscribe({
      next: (res: any) => {
        if (res?.status) {
          this.toast.success('Employee heldup information saved successfully');
          this.reset();
          this.dataSource.set([]);
          this.salaryBreakups.set([]);
        } else {
          this.toast.error(
            res?.message || 'Failed to save employee heldup information',
          );
        }
      },
      error: (err: any) => {
        const errorMsg =
          err?.error?.detail ||
          err?.error?.message ||
          'Error saving employee heldup information';
        this.toast.error(errorMsg);
        console.error(err);
      },
    });
  }

  //-- transfer only date , delete timezone--------
  // formatDate(date: Date | string): string {
  //   const d = date instanceof Date ? date : new Date(date);
  //   const year = d.getFullYear();
  //   const month = String(d.getMonth() + 1).padStart(2, '0');
  //   const day = String(d.getDate()).padStart(2, '0');
  //   return `${year}-${month}-${day}`;
  // }

  update() {

    if (!this.frmGroup.valid || !this.employeeFrm.valid) {
      this.frmGroup.markAllAsTouched();
      this.employeeFrm.markAllAsTouched();
      return;
    }

    if (!this.uuid) {
      this.toast.error('No record selected for update');
      return;
    }

    const formValue = this.frmGroup.value;
    const empData = this.employeeFrm.getRawValue();

    // 🔹 MASTER PAYLOAD (uuid based)
    const payload: any = {
      uuid: this.uuid, // master uuid
      employeeId: empData.id,
      heldupType: Number(formValue.heldupType),
      // fromDate: this.formatDate(formValue.fromDate),
      // toDate: this.formatDate(formValue.toDate),
      fromDate: formValue.fromDate,
      toDate: formValue.toDate,
      remarks: formValue.remark || '',
      isDeleted: false,
    };

    // 🔹 DETAILS PAYLOAD (update only heldupValue by uuid)
    if (this.dataSource().length) {
      payload.heldupDetails = this.dataSource().map((row) => ({
        uuid: row.uuid,
        salaryStructureBreakupId: row.id,
        salaryHead: row.salaryHead,
        amount: row.amount,
        heldupValue: Number(row.value),
      }));
    }

    this.employeeHeldupInfoSetupService
      .updateHeldupInfo(payload)
      .subscribe({
        next: (res: any) => {
          if (res?.status) {
            this.toast.success('Employee heldup information updated successfully');

            this.isEdit = false;
            this.uuid = null;

            this.frmGroup.reset();
            this.employeeFrm.reset();
            this.dataSource.set([]);
            this.salaryBreakups.set([]);

            this.buttonSetup();
          } else {
            this.toast.error(
              res?.message || 'Failed to update employee heldup information'
            );
          }
        },
        error: (err: any) => {
          const errorMsg =
            err?.error?.detail ||
            err?.error?.message ||
            'Error updating employee heldup information';
          this.toast.error(errorMsg);
          console.error(err);
        },
      });
  }

  reset() {
    this.frmGroup.reset();
    this.employeeFrm.reset();
    this.dataSource.set([]);
    this.salaryBreakups.set([]);
    this.isEdit = false;
    this.uuid = null;
    this.buttonSetup();
  }

  // Dropdown options for employee search
  departmentOptions: any[] = [];
  designationOptions: any[] = [];
  employmentTypeOptions: any[] = [];


  private loadEmployeeSearchOptions(): void {
    forkJoin({
      departments: this.centinoService.getDepartmentList(),
      designations: this.centinoService.getDesignationList(),
      employmentTypes: this.centinoService.getEmploymentTypeList(),
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res) => {
          this.departmentOptions = (res.departments || []).map((i: any) => ({
            key: i.DEPT_SL,
            value: i.DEPT_NM,
          }));

          this.designationOptions = (res.designations?.payload || []).map(
            (i: any) => ({
              key: i.id,
              value: i.nameEn,
            }),
          );

          this.employmentTypeOptions = (res.employmentTypes?.payload || []).map(
            (i: any) => ({
              key: i.id,
              value: i.nameEn,
            }),
          );

          // at last config update
          this.updateEmployeeSearchConfig();
        },
        error: () => {
          this.toast.error('Failed to load employee search options');
        },
      });
  }

  private updateEmployeeSearchConfig(): void {
    const searchConfig: GenericSearchConfig = {
      searchFields: [
        {
          type: 'text',
          controlName: 'employeeId',
          label: 'Employee ID',
          placeholder: 'Enter Employee ID',
        },
        {
          type: 'text',
          controlName: 'name',
          label: 'Name',
          placeholder: 'Enter Name',
        },
        {
          type: 'select',
          controlName: 'department',
          label: 'Department',
          options: this.departmentOptions,
          searchable: true,
          placeholder: 'Select Department',
        },
        {
          type: 'select',
          controlName: 'designation',
          label: 'Designation',
          options: this.designationOptions,
          searchable: true,
          placeholder: 'Select Designation',
        },
      ],
      gridConfig: {
        selectedColumns: ['employeeId', 'name', 'department', 'designation'],
        customColumnNames: {
          employeeId: 'Employee ID',
          name: 'Name',
          department: 'Department',
          designation: 'Designation',
        },
        pageSize: 10,
        showEditButton: false,
        enableSelection: false,
        gridTitle: 'Employee List',
      },
      autoSearchOnOpen: false,
      formGridCss: 'grid md:grid-cols-3 gap-4 mb-6',
    };

    this.employeeSearchModalData = {
      config: searchConfig,
      searchCallback: this.handleEmployeeSearch.bind(this),
    };
  }

  private async handleEmployeeSearch(filters: any): Promise<any[]> {
    const safeFilters = {
      employeeId: filters.employeeId?.trim() || '',
      name: filters.name?.trim() || '',
      department: filters.department || '',
      designation: filters.designation || '',
    };

    const isEmpty = Object.values(safeFilters).every((v) => v === '');

    const obs = isEmpty
      ? this.employeeService.getAllEmployees()
      : this.employeeService.employeeSearch(
        safeFilters.employeeId,
        safeFilters.name,
        safeFilters.department,
        safeFilters.designation,
      );

    return new Promise((resolve) => {
      obs.subscribe({
        next: (res: any) => {
          if (res?.status && Array.isArray(res.payload)) {
            const employees = res.payload.map((emp: any) => ({
              ...emp,
              employeeId: emp.employeeId || '',
              name: emp.name || emp.employeeNameEn || '',
              department:
                this.getDepartmentName(emp.departmentId) ||
                emp.departmentName ||
                '',
              designation:
                this.getDesignationName(emp.corporateDesignationId) ||
                emp.corporateDesignationName ||
                '',
              employmentTypeName:
                this.getEmploymentTypeName(emp.employmentType) ||
                emp.employmentTypeName ||
                '',
              joiningDate: emp.joiningDate || '',
              location: emp.workingUnit || emp.location || '',
              departmentId: emp.departmentId || emp.department || '',
              corporateDesignationId:
                emp.corporateDesignationId || emp.corporateDesignation || '',
            }));

            resolve(employees);
          } else {
            resolve([]);
          }
        },
        error: () => {
          resolve([]);
        },
      });
    });
  }


  // Get department name from ID
  private getDepartmentName(departmentId: string): string {
    if (!departmentId) return '';
    const dept = this.departmentOptions.find(
      (opt) => String(opt.key) === String(departmentId),
    );
    return dept?.value || '';
  }

  // Get designation name from ID
  private getDesignationName(designationId: string): string {
    if (!designationId) return '';
    const desig = this.designationOptions.find(
      (opt) => String(opt.key) === String(designationId),
    );
    return desig?.value || '';
  }

  // Get employment type name from ID
  private getEmploymentTypeName(employmentTypeId: string): string {
    if (!employmentTypeId) return '';
    const empType = this.employmentTypeOptions.find(
      (opt) => String(opt.key) === String(employmentTypeId),
    );
    return empType?.value || '';
  }

  // Employee selection method
  onEmployeeSelected(employee: any): void {

    if (typeof employee == 'string') employee = JSON.parse(employee);

    this.selectedEmployee.set(employee);

    // Patch employee form with auto-fetched data from HR/Employee Master
    this.employeeFrm.patchValue({
      id: employee.employeeId,
      name: employee.name || '',
      designation: employee.designation || '',
      department: employee.department || '',
      // joiningDate: employee.joiningDate || '',
      // location: employee.location || '',
    });

    // Close modal
    this.showEmployeeModal.set(false);
  }

  onEmployeeIdEnter(employeeId: string) {
    if (!employeeId || employeeId.trim() === '') {
      this.toast.warning('Please enter an Employee ID', 'Warning');
      return;
    }

    // Call the API to fetch employee basic information
    this.employeeService
      .getEmployeeBasicInfo(employeeId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          if (response.status && response.payload) {
            const employee = response.payload;

            // Fetch department name from centrino using departmentId
            this.centinoService.getDepartmentList().subscribe({
              next: (deptResult: any) => {
                const departments = deptResult || [];
                // Convert both to string for comparison to handle type mismatch
                const deptId = String(
                  employee.departmentId || employee.department || '',
                );
                const department = departments.find(
                  (d: any) => String(d.DEPT_SL) === deptId,
                );
                const departmentName = department ? department.DEPT_NM : '';

                // Populate employee form with the fetched data
                this.employeeFrm.patchValue({
                  id: employeeId,
                  name: employee.employeeNameEn || '',
                  designation: employee.corporateDesignation || '',
                  department: departmentName,
                  joiningDate: employee.joiningDate || '',
                  location: employee.location || '',
                });

                // Auto-fill location in main form from employee master
                this.frmGroup.patchValue({
                  location: employee.location || '',
                });

                this.selectedEmployee.set(employee);
                this.toast.success(
                  `Employee information loaded successfully`,
                  'Success',
                );
              },
            });
          } else {
            this.toast.error(response.message || 'Employee not found', 'Error');
            // Clear employee form if not found
            this.clearEmployeeForm();
          }
        },
        error: (error) => {
          console.error('Error fetching employee information:', error);
          this.toast.error('Failed to fetch employee information', 'Error');
          // Clear employee form on error
          this.clearEmployeeForm();
        },
      });
  }

  getModalResult(result: any) {
    if (result?.heldupData) {
      this.isModalShow = false;
      this.loadHeldupDataFromList(result.heldupData);
    }
  }

  private clearEmployeeForm() {
    this.employeeFrm.patchValue({
      name: '',
      designation: '',
      department: '',
    });
    this.selectedEmployee.set(null);
  }

}
