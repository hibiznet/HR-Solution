import { useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { createAttendance, deleteAttendance, fetchAttendance, fetchAttendanceRecord, updateAttendance } from '../api/attendanceApi';
import { fetchDepartments } from '../api/departmentApi';
import { fetchEmployees } from '../api/employeeApi';
import { ConfirmDialog } from '../components/ConfirmDialog';
import { EmptyState } from '../components/EmptyState';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { PageHeader } from '../components/PageHeader';
import { Pagination } from '../components/Pagination';
import { useAuthStore } from '../features/auth/authStore';
import type { AttendanceSearchParams, AttendanceSummary, AttendanceUpsertRequest } from '../types/attendance';
import { toastSuccess } from '../utils/toast';

const today = new Date().toISOString().slice(0, 10);
const defaultFilter: AttendanceSearchParams = { keyword: '', employeeId: '', departmentId: '', attendanceStatusCode: '', isClosed: '', startDate: today, endDate: today, page: 0, size: 10 };
const defaultForm: AttendanceUpsertRequest = {
  employeeId: 0,
  workDate: today,
  plannedStartTime: `${today}T09:00`,
  plannedEndTime: `${today}T18:00`,
  checkInTime: `${today}T09:00`,
  checkOutTime: `${today}T18:00`,
  breakMinutes: 60,
  overtimeMinutes: 0,
  attendanceStatusCode: 'NORMAL',
  isClosed: false,
};

function toDateTimeLocal(value?: string | null) {
  return value ? value.slice(0, 16) : '';
}

function toPayload(values: AttendanceUpsertRequest): AttendanceUpsertRequest {
  return {
    ...values,
    employeeId: Number(values.employeeId),
    plannedStartTime: values.plannedStartTime || null,
    plannedEndTime: values.plannedEndTime || null,
    checkInTime: values.checkInTime || null,
    checkOutTime: values.checkOutTime || null,
  };
}

export function AttendancePage() {
  const canManageOrg = useAuthStore((state) => state.hasAnyRole(['ROLE_ADMIN', 'ROLE_HR_MANAGER', 'ROLE_SYS_ADMIN', 'ROLE_TEAM_MANAGER']));
  const queryClient = useQueryClient();
  const [filters, setFilters] = useState<AttendanceSearchParams>(defaultFilter);
  const [openForm, setOpenForm] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<AttendanceSummary | null>(null);

  const listQuery = useQuery({ queryKey: ['attendance', filters], queryFn: () => fetchAttendance(filters) });
  const departmentQuery = useQuery({ queryKey: ['attendance-form', 'departments'], queryFn: () => fetchDepartments({ page: 0, size: 200, keyword: '', parentId: '', isActive: '', }) });
  const employeeQuery = useQuery({ queryKey: ['attendance-form', 'employees'], queryFn: () => fetchEmployees({ page: 0, size: 200, keyword: '', departmentId: '', statusCode: '', employmentTypeCode: '' }) });
  const filterForm = useForm<AttendanceSearchParams>({ defaultValues: defaultFilter });
  const form = useForm<AttendanceUpsertRequest>({ defaultValues: defaultForm });

  const departmentOptions = departmentQuery.data?.content ?? [];
  const employeeOptions = useMemo(() => employeeQuery.data?.content ?? [], [employeeQuery.data]);

  const saveMutation = useMutation({
    mutationFn: async (payload: AttendanceUpsertRequest) => editingId ? updateAttendance(editingId, payload) : createAttendance(payload),
    onSuccess: () => {
      toastSuccess(editingId ? '근태 기록이 수정되었습니다.' : '근태 기록이 등록되었습니다.');
      setOpenForm(false);
      setEditingId(null);
      form.reset(defaultForm);
      queryClient.invalidateQueries({ queryKey: ['attendance'] });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => deleteAttendance(id),
    onSuccess: () => {
      toastSuccess('근태 기록이 삭제되었습니다.');
      setDeleteTarget(null);
      queryClient.invalidateQueries({ queryKey: ['attendance'] });
    },
  });

  const openCreate = () => {
    setEditingId(null);
    form.reset(defaultForm);
    setOpenForm(true);
  };

  const openEdit = async (id: number) => {
    const detail = await fetchAttendanceRecord(id);
    setEditingId(id);
    form.reset({
      employeeId: detail.employeeId,
      workDate: detail.workDate,
      plannedStartTime: toDateTimeLocal(detail.plannedStartTime),
      plannedEndTime: toDateTimeLocal(detail.plannedEndTime),
      checkInTime: toDateTimeLocal(detail.checkInTime),
      checkOutTime: toDateTimeLocal(detail.checkOutTime),
      breakMinutes: detail.breakMinutes,
      overtimeMinutes: detail.overtimeMinutes,
      attendanceStatusCode: detail.attendanceStatusCode,
      isClosed: detail.isClosed,
    });
    setOpenForm(true);
  };

  const submitFilter = filterForm.handleSubmit((values) => setFilters({ ...filters, ...values, page: 0 }));
  const submitForm = form.handleSubmit((values) => saveMutation.mutate(toPayload(values)));

  return (
    <div>
      <PageHeader
        title="근태 관리"
        description="근태 기록을 조회하고 등록/수정/삭제할 수 있습니다. 팀 관리자는 소속 부서 범위만 관리할 수 있습니다."
        actions={canManageOrg ? <button type="button" className="primary-button" onClick={openCreate}>근태 등록</button> : undefined}
      />

      <section className="card">
        <form className="filter-grid four" onSubmit={submitFilter}>
          <input placeholder="직원/사번/부서 검색" {...filterForm.register('keyword')} />
          <select {...filterForm.register('departmentId')}>
            <option value="">전체 부서</option>
            {departmentOptions.map((item) => <option key={item.id} value={item.id}>{item.deptName}</option>)}
          </select>
          <select {...filterForm.register('employeeId')}>
            <option value="">전체 직원</option>
            {employeeOptions.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}
          </select>
          <select {...filterForm.register('attendanceStatusCode')}>
            <option value="">전체 상태</option>
            <option value="NORMAL">정상</option>
            <option value="LATE">지각</option>
            <option value="REMOTE">재택</option>
            <option value="OVERTIME">연장근무</option>
            <option value="ABSENT">결근</option>
          </select>
          <select {...filterForm.register('isClosed', { setValueAs: (v) => v === '' ? '' : v === 'true' })}>
            <option value="">마감 여부 전체</option>
            <option value="false">미마감</option>
            <option value="true">마감</option>
          </select>
          <input type="date" {...filterForm.register('startDate')} />
          <input type="date" {...filterForm.register('endDate')} />
          <button type="submit" className="secondary-button">검색</button>
        </form>
      </section>

      <section className="card">
        {listQuery.isLoading ? <LoadingSpinner text="근태 목록을 불러오는 중입니다." /> : null}
        {!listQuery.isLoading && !(listQuery.data?.content.length) ? <EmptyState title="조회된 근태 기록이 없습니다." description="필터를 변경하거나 새 근태 기록을 등록해 보세요." /> : null}
        {!!listQuery.data?.content.length && (
          <>
            <table className="data-table">
              <thead>
                <tr>
                  <th>근무일</th>
                  <th>직원</th>
                  <th>부서</th>
                  <th>출근</th>
                  <th>퇴근</th>
                  <th>휴게(분)</th>
                  <th>총근무(분)</th>
                  <th>연장(분)</th>
                  <th>상태</th>
                  <th>마감</th>
                  <th>작업</th>
                </tr>
              </thead>
              <tbody>
                {listQuery.data.content.map((item) => (
                  <tr key={item.id}>
                    <td>{item.workDate}</td>
                    <td>{item.employeeName} <span className="muted small-text">({item.employeeNo})</span></td>
                    <td>{item.departmentName ?? '-'}</td>
                    <td>{toDateTimeLocal(item.checkInTime) || '-'}</td>
                    <td>{toDateTimeLocal(item.checkOutTime) || '-'}</td>
                    <td>{item.breakMinutes}</td>
                    <td>{item.totalWorkMinutes}</td>
                    <td>{item.overtimeMinutes}</td>
                    <td>{item.attendanceStatusCode}</td>
                    <td>{item.isClosed ? 'Y' : 'N'}</td>
                    <td>
                      {canManageOrg ? (
                        <div className="inline-actions">
                          <button type="button" className="secondary-button small" onClick={() => openEdit(item.id)}>수정</button>
                          <button type="button" className="danger-button small" onClick={() => setDeleteTarget(item)}>삭제</button>
                        </div>
                      ) : '-'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <Pagination page={filters.page} size={filters.size} totalPages={listQuery.data.totalPages} totalElements={listQuery.data.totalElements} onChange={(page) => setFilters({ ...filters, page })} />
          </>
        )}
      </section>

      {openForm ? (
        <div className="modal-overlay">
          <div className="modal-card wide">
            <h2>{editingId ? '근태 기록 수정' : '근태 기록 등록'}</h2>
            <form className="form-grid two" onSubmit={submitForm}>
              <label>
                <span>직원</span>
                <select {...form.register('employeeId', { valueAsNumber: true, required: true })}>
                  <option value="">선택</option>
                  {employeeOptions.map((item) => <option key={item.id} value={item.id}>{item.name} ({item.employeeNo})</option>)}
                </select>
              </label>
              <label>
                <span>근무일</span>
                <input type="date" {...form.register('workDate', { required: true })} />
              </label>
              <label>
                <span>예정 출근</span>
                <input type="datetime-local" {...form.register('plannedStartTime')} />
              </label>
              <label>
                <span>예정 퇴근</span>
                <input type="datetime-local" {...form.register('plannedEndTime')} />
              </label>
              <label>
                <span>실제 출근</span>
                <input type="datetime-local" {...form.register('checkInTime')} />
              </label>
              <label>
                <span>실제 퇴근</span>
                <input type="datetime-local" {...form.register('checkOutTime')} />
              </label>
              <label>
                <span>휴게시간(분)</span>
                <input type="number" {...form.register('breakMinutes', { valueAsNumber: true })} />
              </label>
              <label>
                <span>연장근무(분)</span>
                <input type="number" {...form.register('overtimeMinutes', { valueAsNumber: true })} />
              </label>
              <label>
                <span>근태 상태</span>
                <select {...form.register('attendanceStatusCode')}>
                  <option value="NORMAL">정상</option>
                  <option value="LATE">지각</option>
                  <option value="REMOTE">재택</option>
                  <option value="OVERTIME">연장근무</option>
                  <option value="ABSENT">결근</option>
                </select>
              </label>
              <label>
                <span>마감 여부</span>
                <select {...form.register('isClosed', { setValueAs: (v) => v === 'true' || v === true })}>
                  <option value="false">미마감</option>
                  <option value="true">마감</option>
                </select>
              </label>
              <div className="form-actions">
                <button type="button" className="secondary-button" onClick={() => setOpenForm(false)}>취소</button>
                <button type="submit" className="primary-button" disabled={saveMutation.isPending}>{saveMutation.isPending ? '저장 중...' : '저장'}</button>
              </div>
            </form>
          </div>
        </div>
      ) : null}

      <ConfirmDialog
        open={!!deleteTarget}
        title="근태 기록 삭제"
        description={deleteTarget ? `${deleteTarget.employeeName} / ${deleteTarget.workDate} 근태 기록을 삭제하시겠습니까?` : ''}
        onClose={() => setDeleteTarget(null)}
        onConfirm={() => deleteTarget && deleteMutation.mutate(deleteTarget.id)}
      />
    </div>
  );
}
