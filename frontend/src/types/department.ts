export interface DepartmentSummary {
  id: number;
  deptCode: string;
  deptName: string;
  deptType?: string | null;
  parentId?: number | null;
  parentName?: string | null;
  sortOrder?: number;
  isActive?: boolean;
}

export interface DepartmentSearchParams {
  keyword?: string;
  parentId?: number | '';
  isActive?: boolean | '';
  page: number;
  size: number;
}

export interface DepartmentUpsertRequest {
  deptCode: string;
  deptName: string;
  deptType?: string;
  parentId?: number | null;
  sortOrder?: number;
  isActive?: boolean;
}
