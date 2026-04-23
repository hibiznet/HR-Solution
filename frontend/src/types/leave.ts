import type { PageResponse } from './common';

export interface LeavePolicySummary {
  id: number;
  policyName: string;
  accrualBasisCode: string;
  monthlyLeaveForFirstYear: boolean;
  annualLeaveDays: number;
  isActive: boolean;
}
export interface LeavePolicySearchParams { keyword?: string; isActive?: string; page?: number; size?: number; }
export interface LeavePolicyUpsertRequest {
  policyName: string;
  accrualBasisCode: string;
  monthlyLeaveForFirstYear: boolean;
  annualLeaveDays: number;
  isActive: boolean;
}
export interface LeaveRequestSummary {
  id: number;
  employeeId: number;
  employeeName: string;
  departmentId?: number | null;
  departmentName?: string | null;
  leaveTypeCode: string;
  startDate: string;
  endDate: string;
  startTime?: string | null;
  endTime?: string | null;
  daysCount: number;
  reason?: string | null;
  statusCode: string;
}
export interface LeaveRequestSearchParams { keyword?: string; employeeId?: string; departmentId?: string; leaveTypeCode?: string; statusCode?: string; page?: number; size?: number; }
export interface LeaveRequestUpsertRequest {
  employeeId: number;
  leaveTypeCode: string;
  startDate: string;
  endDate: string;
  startTime?: string | null;
  endTime?: string | null;
  daysCount: number;
  reason?: string;
  statusCode: string;
}
export type LeavePolicyPage = PageResponse<LeavePolicySummary>;
export type LeaveRequestPage = PageResponse<LeaveRequestSummary>;
