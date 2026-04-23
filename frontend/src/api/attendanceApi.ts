import { http } from './http';
import { normalizePage, unwrapData } from './helpers';
import type { PageResponse } from '../types/common';
import type { AttendanceSearchParams, AttendanceSummary, AttendanceUpsertRequest } from '../types/attendance';

function toParams(params: AttendanceSearchParams) {
  return {
    keyword: params.keyword || undefined,
    employeeId: params.employeeId || undefined,
    departmentId: params.departmentId || undefined,
    attendanceStatusCode: params.attendanceStatusCode || undefined,
    isClosed: params.isClosed === '' ? undefined : params.isClosed,
    startDate: params.startDate || undefined,
    endDate: params.endDate || undefined,
    page: params.page,
    size: params.size,
  };
}

export async function fetchAttendance(params: AttendanceSearchParams): Promise<PageResponse<AttendanceSummary>> {
  const response = await http.get('/api/attendance', { params: toParams(params) });
  return normalizePage<AttendanceSummary>(unwrapData<unknown>(response), params.page, params.size);
}

export async function fetchAttendanceRecord(id: number): Promise<AttendanceSummary> {
  const response = await http.get(`/api/attendance/${id}`);
  return unwrapData<AttendanceSummary>(response);
}

export async function createAttendance(payload: AttendanceUpsertRequest): Promise<AttendanceSummary> {
  const response = await http.post('/api/attendance', payload);
  return unwrapData<AttendanceSummary>(response);
}

export async function updateAttendance(id: number, payload: AttendanceUpsertRequest): Promise<AttendanceSummary> {
  const response = await http.put(`/api/attendance/${id}`, payload);
  return unwrapData<AttendanceSummary>(response);
}

export async function deleteAttendance(id: number): Promise<void> {
  await http.delete(`/api/attendance/${id}`);
}
