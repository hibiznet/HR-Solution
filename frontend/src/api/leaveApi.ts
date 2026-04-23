import { http } from './http';
import { normalizePage, unwrapData } from './helpers';
import type { LeavePolicyPage, LeavePolicySearchParams, LeavePolicySummary, LeavePolicyUpsertRequest, LeaveRequestPage, LeaveRequestSearchParams, LeaveRequestSummary, LeaveRequestUpsertRequest } from '../types/leave';

export async function fetchLeavePolicies(params: LeavePolicySearchParams): Promise<LeavePolicyPage> {
  const response = await http.get('/api/leave/policies', { params });
  return normalizePage<LeavePolicySummary>(unwrapData(response), Number(params.page ?? 0), Number(params.size ?? 10));
}
export async function fetchLeavePolicy(id: number): Promise<LeavePolicySummary> {
  const response = await http.get(`/api/leave/policies/${id}`);
  return unwrapData(response);
}
export async function createLeavePolicy(payload: LeavePolicyUpsertRequest): Promise<LeavePolicySummary> {
  const response = await http.post('/api/leave/policies', payload);
  return unwrapData(response);
}
export async function updateLeavePolicy(id: number, payload: LeavePolicyUpsertRequest): Promise<LeavePolicySummary> {
  const response = await http.put(`/api/leave/policies/${id}`, payload);
  return unwrapData(response);
}
export async function deleteLeavePolicy(id: number): Promise<void> {
  await http.delete(`/api/leave/policies/${id}`);
}

export async function fetchLeaveRequests(params: LeaveRequestSearchParams): Promise<LeaveRequestPage> {
  const response = await http.get('/api/leave/requests', { params });
  return normalizePage<LeaveRequestSummary>(unwrapData(response), Number(params.page ?? 0), Number(params.size ?? 10));
}
export async function fetchLeaveRequest(id: number): Promise<LeaveRequestSummary> {
  const response = await http.get(`/api/leave/requests/${id}`);
  return unwrapData(response);
}
export async function createLeaveRequest(payload: LeaveRequestUpsertRequest): Promise<LeaveRequestSummary> {
  const response = await http.post('/api/leave/requests', payload);
  return unwrapData(response);
}
export async function updateLeaveRequest(id: number, payload: LeaveRequestUpsertRequest): Promise<LeaveRequestSummary> {
  const response = await http.put(`/api/leave/requests/${id}`, payload);
  return unwrapData(response);
}
export async function deleteLeaveRequest(id: number): Promise<void> {
  await http.delete(`/api/leave/requests/${id}`);
}
