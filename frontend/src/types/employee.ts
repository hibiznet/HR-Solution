export interface EmployeeSummary {
  id: number;
  employeeNo: string;
  name: string;
  email: string;
  phone?: string | null;
  statusCode: string;
  employmentTypeCode: string;
  departmentId?: number | null;
  departmentName: string;
  jobTitleId?: number | null;
  jobTitleName?: string | null;
  hireDate?: string | null;
  managerEmployeeId?: number | null;
  managerName?: string | null;
}

export interface EmployeeSearchParams {
  keyword?: string;
  departmentId?: number | '';
  statusCode?: string;
  employmentTypeCode?: string;
  page: number;
  size: number;
}

export interface EmployeeUpsertRequest {
  employeeNo: string;
  name: string;
  email: string;
  phone?: string;
  statusCode: string;
  employmentTypeCode: string;
  departmentId: number;
  jobTitleId?: number | null;
  managerEmployeeId?: number | null;
  hireDate: string;
  probationEndDate?: string;
  workTypeCode?: string;
}
