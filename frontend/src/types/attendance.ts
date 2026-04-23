export interface AttendanceSummary {
  id: number;
  employeeId: number;
  employeeNo: string;
  employeeName: string;
  departmentId?: number | null;
  departmentName?: string | null;
  workDate: string;
  plannedStartTime?: string | null;
  plannedEndTime?: string | null;
  checkInTime?: string | null;
  checkOutTime?: string | null;
  breakMinutes: number;
  totalWorkMinutes: number;
  overtimeMinutes: number;
  attendanceStatusCode: string;
  isClosed: boolean;
}

export interface AttendanceSearchParams {
  keyword?: string;
  employeeId?: number | '';
  departmentId?: number | '';
  attendanceStatusCode?: string;
  isClosed?: boolean | '';
  startDate?: string;
  endDate?: string;
  page: number;
  size: number;
}

export interface AttendanceUpsertRequest {
  employeeId: number;
  workDate: string;
  plannedStartTime?: string | null;
  plannedEndTime?: string | null;
  checkInTime?: string | null;
  checkOutTime?: string | null;
  breakMinutes: number;
  overtimeMinutes: number;
  attendanceStatusCode: string;
  isClosed: boolean;
}
