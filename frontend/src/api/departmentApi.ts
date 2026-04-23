import { http } from './http';
import { normalizePage, unwrapData } from './helpers';
import type { PageResponse } from '../types/common';
import type { DepartmentSearchParams, DepartmentSummary, DepartmentUpsertRequest } from '../types/department';

function toParams(params: DepartmentSearchParams) {
  return {
    keyword: params.keyword || undefined,
    parentId: params.parentId || undefined,
    isActive: params.isActive === '' ? undefined : params.isActive,
    page: params.page,
    size: params.size,
  };
}

export async function fetchDepartments(params: DepartmentSearchParams): Promise<PageResponse<DepartmentSummary>> {
  const response = await http.get('/api/departments', { params: toParams(params) });
  return normalizePage<DepartmentSummary>(unwrapData<unknown>(response), params.page, params.size);
}
export async function fetchDepartment(id: number): Promise<DepartmentSummary> { const response = await http.get(`/api/departments/${id}`); return unwrapData<DepartmentSummary>(response); }
export async function createDepartment(payload: DepartmentUpsertRequest): Promise<DepartmentSummary> { const response = await http.post('/api/departments', payload); return unwrapData<DepartmentSummary>(response); }
export async function updateDepartment(id: number, payload: DepartmentUpsertRequest): Promise<DepartmentSummary> { const response = await http.put(`/api/departments/${id}`, payload); return unwrapData<DepartmentSummary>(response); }
export async function deleteDepartment(id: number): Promise<void> { await http.delete(`/api/departments/${id}`); }
