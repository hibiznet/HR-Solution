import { http } from './http';
import { normalizePage, unwrapData } from './helpers';
import type { PageResponse } from '../types/common';
import type { EmployeeSearchParams, EmployeeSummary, EmployeeUpsertRequest } from '../types/employee';

function toParams(params: EmployeeSearchParams) {
  return {
    keyword: params.keyword || undefined,
    departmentId: params.departmentId || undefined,
    statusCode: params.statusCode || undefined,
    employmentTypeCode: params.employmentTypeCode || undefined,
    page: params.page,
    size: params.size,
  };
}

export async function fetchEmployees(params: EmployeeSearchParams): Promise<PageResponse<EmployeeSummary>> {
  const response = await http.get('/api/employees', { params: toParams(params) });
  return normalizePage<EmployeeSummary>(unwrapData<unknown>(response), params.page, params.size);
}

export async function fetchEmployee(id: number): Promise<EmployeeSummary> {
  const response = await http.get(`/api/employees/${id}`);
  return unwrapData<EmployeeSummary>(response);
}

export async function createEmployee(payload: EmployeeUpsertRequest): Promise<EmployeeSummary> {
  const response = await http.post('/api/employees', payload);
  return unwrapData<EmployeeSummary>(response);
}

export async function updateEmployee(id: number, payload: EmployeeUpsertRequest): Promise<EmployeeSummary> {
  const response = await http.put(`/api/employees/${id}`, payload);
  return unwrapData<EmployeeSummary>(response);
}

export async function deleteEmployee(id: number): Promise<void> {
  await http.delete(`/api/employees/${id}`);
}
