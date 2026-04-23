import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { fetchDepartments } from '../api/departmentApi';
import { fetchEmployees } from '../api/employeeApi';
import { createLeavePolicy, createLeaveRequest, deleteLeavePolicy, deleteLeaveRequest, fetchLeavePolicies, fetchLeavePolicy, fetchLeaveRequest, fetchLeaveRequests, updateLeavePolicy, updateLeaveRequest } from '../api/leaveApi';
import { ConfirmDialog } from '../components/ConfirmDialog';
import { EmptyState } from '../components/EmptyState';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { PageHeader } from '../components/PageHeader';
import { Pagination } from '../components/Pagination';
import { useAuthStore } from '../features/auth/authStore';
import type { LeavePolicySearchParams, LeavePolicySummary, LeavePolicyUpsertRequest, LeaveRequestSearchParams, LeaveRequestSummary, LeaveRequestUpsertRequest } from '../types/leave';
import { toastSuccess } from '../utils/toast';

const defaultPolicyFilter: LeavePolicySearchParams = { keyword: '', isActive: '', page: 0, size: 5 };
const defaultPolicyForm: LeavePolicyUpsertRequest = { policyName: '', accrualBasisCode: 'HIRE_DATE', monthlyLeaveForFirstYear: true, annualLeaveDays: 15, isActive: true };
const defaultRequestFilter: LeaveRequestSearchParams = { keyword: '', employeeId: '', departmentId: '', leaveTypeCode: '', statusCode: '', page: 0, size: 10 };
const defaultRequestForm: LeaveRequestUpsertRequest = { employeeId: 0, leaveTypeCode: 'ANNUAL', startDate: new Date().toISOString().slice(0,10), endDate: new Date().toISOString().slice(0,10), startTime: null, endTime: null, daysCount: 1, reason: '', statusCode: 'APPROVED' };

export function LeavePage() {
  const canManage = useAuthStore((state) => state.hasAnyRole(['ROLE_ADMIN','ROLE_HR_MANAGER','ROLE_SYS_ADMIN']));
  const queryClient = useQueryClient();

  const [policyFilters, setPolicyFilters] = useState<LeavePolicySearchParams>(defaultPolicyFilter);
  const [requestFilters, setRequestFilters] = useState<LeaveRequestSearchParams>(defaultRequestFilter);
  const [openPolicyForm, setOpenPolicyForm] = useState(false);
  const [openRequestForm, setOpenRequestForm] = useState(false);
  const [editingPolicyId, setEditingPolicyId] = useState<number | null>(null);
  const [editingRequestId, setEditingRequestId] = useState<number | null>(null);
  const [deletePolicyTarget, setDeletePolicyTarget] = useState<LeavePolicySummary | null>(null);
  const [deleteRequestTarget, setDeleteRequestTarget] = useState<LeaveRequestSummary | null>(null);

  const policyQuery = useQuery({ queryKey: ['leave-policies', policyFilters], queryFn: () => fetchLeavePolicies(policyFilters) });
  const requestQuery = useQuery({ queryKey: ['leave-requests', requestFilters], queryFn: () => fetchLeaveRequests(requestFilters) });
  const departmentQuery = useQuery({ queryKey: ['leave-form', 'departments'], queryFn: () => fetchDepartments({ page: 0, size: 200, keyword: '', parentId: '', isActive: '' }) });
  const employeeQuery = useQuery({ queryKey: ['leave-form', 'employees'], queryFn: () => fetchEmployees({ page: 0, size: 200, keyword: '', departmentId: '', statusCode: '', employmentTypeCode: '' }) });

  const policyFilterForm = useForm<LeavePolicySearchParams>({ defaultValues: defaultPolicyFilter });
  const policyForm = useForm<LeavePolicyUpsertRequest>({ defaultValues: defaultPolicyForm });
  const requestFilterForm = useForm<LeaveRequestSearchParams>({ defaultValues: defaultRequestFilter });
  const requestForm = useForm<LeaveRequestUpsertRequest>({ defaultValues: defaultRequestForm });

  const savePolicyMutation = useMutation({
    mutationFn: async (payload: LeavePolicyUpsertRequest) => editingPolicyId ? updateLeavePolicy(editingPolicyId, payload) : createLeavePolicy(payload),
    onSuccess: () => { toastSuccess(editingPolicyId ? '휴가 정책이 수정되었습니다.' : '휴가 정책이 등록되었습니다.'); setOpenPolicyForm(false); setEditingPolicyId(null); policyForm.reset(defaultPolicyForm); queryClient.invalidateQueries({ queryKey: ['leave-policies'] }); }
  });
  const deletePolicyMutation = useMutation({ mutationFn: (id: number) => deleteLeavePolicy(id), onSuccess: () => { toastSuccess('휴가 정책이 삭제되었습니다.'); setDeletePolicyTarget(null); queryClient.invalidateQueries({ queryKey: ['leave-policies'] }); }});

  const saveRequestMutation = useMutation({
    mutationFn: async (payload: LeaveRequestUpsertRequest) => editingRequestId ? updateLeaveRequest(editingRequestId, payload) : createLeaveRequest(payload),
    onSuccess: () => { toastSuccess(editingRequestId ? '휴가 요청이 수정되었습니다.' : '휴가 요청이 등록되었습니다.'); setOpenRequestForm(false); setEditingRequestId(null); requestForm.reset(defaultRequestForm); queryClient.invalidateQueries({ queryKey: ['leave-requests'] }); }
  });
  const deleteRequestMutation = useMutation({ mutationFn: (id: number) => deleteLeaveRequest(id), onSuccess: () => { toastSuccess('휴가 요청이 삭제되었습니다.'); setDeleteRequestTarget(null); queryClient.invalidateQueries({ queryKey: ['leave-requests'] }); }});

  const openPolicyCreate = () => { setEditingPolicyId(null); policyForm.reset(defaultPolicyForm); setOpenPolicyForm(true); };
  const openPolicyEdit = async (id: number) => {
    const detail = await fetchLeavePolicy(id);
    setEditingPolicyId(id);
    policyForm.reset({ policyName: detail.policyName, accrualBasisCode: detail.accrualBasisCode, monthlyLeaveForFirstYear: detail.monthlyLeaveForFirstYear, annualLeaveDays: detail.annualLeaveDays, isActive: detail.isActive });
    setOpenPolicyForm(true);
  };
  const openRequestCreate = () => { setEditingRequestId(null); requestForm.reset(defaultRequestForm); setOpenRequestForm(true); };
  const openRequestEdit = async (id: number) => {
    const detail = await fetchLeaveRequest(id);
    setEditingRequestId(id);
    requestForm.reset({ employeeId: detail.employeeId, leaveTypeCode: detail.leaveTypeCode, startDate: detail.startDate, endDate: detail.endDate, startTime: detail.startTime ?? null, endTime: detail.endTime ?? null, daysCount: detail.daysCount, reason: detail.reason ?? '', statusCode: detail.statusCode });
    setOpenRequestForm(true);
  };

  const submitPolicyFilter = policyFilterForm.handleSubmit((values) => setPolicyFilters({ ...policyFilters, ...values, page: 0 }));
  const submitPolicyForm = policyForm.handleSubmit((values) => savePolicyMutation.mutate({ ...values, annualLeaveDays: Number(values.annualLeaveDays) }));
  const submitRequestFilter = requestFilterForm.handleSubmit((values) => setRequestFilters({ ...requestFilters, ...values, page: 0 }));
  const submitRequestForm = requestForm.handleSubmit((values) => saveRequestMutation.mutate({ ...values, employeeId: Number(values.employeeId), daysCount: Number(values.daysCount), startTime: values.startTime || null, endTime: values.endTime || null }));

  const departments = departmentQuery.data?.content ?? [];
  const employees = employeeQuery.data?.content ?? [];

  return <div>
    <PageHeader title="휴가 관리" description="휴가 정책과 휴가 요청을 조회/등록/수정/삭제합니다." actions={canManage ? <div className="inline-actions"><button type="button" className="secondary-button" onClick={openPolicyCreate}>정책 등록</button><button type="button" className="primary-button" onClick={openRequestCreate}>휴가 요청 등록</button></div> : undefined} />

    <section className="card">
      <h2>휴가 정책</h2>
      <form className="filter-grid" onSubmit={submitPolicyFilter}>
        <input placeholder="정책명/기준 검색" {...policyFilterForm.register('keyword')} />
        <select {...policyFilterForm.register('isActive')}>
          <option value="">활성 전체</option><option value="true">활성</option><option value="false">비활성</option>
        </select>
        <button className="secondary-button" type="submit">정책 조회</button>
      </form>
      {policyQuery.isLoading ? <LoadingSpinner /> : !policyQuery.data?.content.length ? <EmptyState title="휴가 정책이 없습니다." /> : (
        <>
          <table className="data-table"><thead><tr><th>정책명</th><th>기준</th><th>1년 미만 월차</th><th>연차</th><th>활성</th>{canManage ? <th>작업</th> : null}</tr></thead><tbody>
            {policyQuery.data.content.map((item) => <tr key={item.id}><td>{item.policyName}</td><td>{item.accrualBasisCode}</td><td>{item.monthlyLeaveForFirstYear ? 'Y' : 'N'}</td><td>{item.annualLeaveDays}</td><td>{item.isActive ? 'Y' : 'N'}</td>{canManage ? <td className="table-actions"><button type="button" className="text-button" onClick={() => openPolicyEdit(item.id)}>수정</button><button type="button" className="text-button danger" onClick={() => setDeletePolicyTarget(item)}>삭제</button></td> : null}</tr>)}
          </tbody></table>
          <Pagination page={policyQuery.data.page} size={policyQuery.data.size} totalPages={policyQuery.data.totalPages} onChange={(nextPage) => setPolicyFilters((prev) => ({ ...prev, page: nextPage }))} />
        </>
      )}
    </section>

    <section className="card">
      <h2>휴가 요청</h2>
      <form className="filter-grid" onSubmit={submitRequestFilter}>
        <input placeholder="직원명/사유 검색" {...requestFilterForm.register('keyword')} />
        <select {...requestFilterForm.register('departmentId')}><option value="">부서 전체</option>{departments.map((d) => <option key={d.id} value={d.id}>{d.deptName}</option>)}</select>
        <select {...requestFilterForm.register('employeeId')}><option value="">직원 전체</option>{employees.map((e) => <option key={e.id} value={e.id}>{e.name}</option>)}</select>
        <select {...requestFilterForm.register('leaveTypeCode')}><option value="">휴가종류 전체</option><option value="ANNUAL">연차</option><option value="SICK">병가</option><option value="SPECIAL">특별휴가</option></select>
        <select {...requestFilterForm.register('statusCode')}><option value="">상태 전체</option><option value="REQUESTED">신청</option><option value="APPROVED">승인</option><option value="REJECTED">반려</option></select>
        <button className="secondary-button" type="submit">요청 조회</button>
      </form>
      {requestQuery.isLoading ? <LoadingSpinner /> : !requestQuery.data?.content.length ? <EmptyState title="휴가 요청이 없습니다." /> : (
        <>
          <table className="data-table"><thead><tr><th>직원</th><th>부서</th><th>종류</th><th>기간</th><th>일수</th><th>상태</th>{canManage ? <th>작업</th> : null}</tr></thead><tbody>
            {requestQuery.data.content.map((item) => <tr key={item.id}><td>{item.employeeName}</td><td>{item.departmentName ?? '-'}</td><td>{item.leaveTypeCode}</td><td>{item.startDate} ~ {item.endDate}</td><td>{item.daysCount}</td><td>{item.statusCode}</td>{canManage ? <td className="table-actions"><button type="button" className="text-button" onClick={() => openRequestEdit(item.id)}>수정</button><button type="button" className="text-button danger" onClick={() => setDeleteRequestTarget(item)}>삭제</button></td> : null}</tr>)}
          </tbody></table>
          <Pagination page={requestQuery.data.page} size={requestQuery.data.size} totalPages={requestQuery.data.totalPages} onChange={(nextPage) => setRequestFilters((prev) => ({ ...prev, page: nextPage }))} />
        </>
      )}
    </section>

    {openPolicyForm ? <div className="modal-backdrop"><div className="modal-card"><h2>{editingPolicyId ? '휴가 정책 수정' : '휴가 정책 등록'}</h2><form className="modal-form" onSubmit={submitPolicyForm}>
      <label><span>정책명</span><input {...policyForm.register('policyName', { required: true })} /></label>
      <label><span>발생 기준</span><select {...policyForm.register('accrualBasisCode')}><option value="HIRE_DATE">입사일 기준</option><option value="FISCAL_YEAR">회계연도 기준</option></select></label>
      <label className="checkbox-row"><input type="checkbox" {...policyForm.register('monthlyLeaveForFirstYear')} /><span>1년 미만 월차 사용</span></label>
      <label><span>연차 일수</span><input type="number" step="0.5" {...policyForm.register('annualLeaveDays', { valueAsNumber: true })} /></label>
      <label className="checkbox-row"><input type="checkbox" {...policyForm.register('isActive')} /><span>활성</span></label>
      <div className="modal-actions"><button type="button" className="secondary-button" onClick={() => setOpenPolicyForm(false)}>취소</button><button type="submit" className="primary-button" disabled={savePolicyMutation.isPending}>{savePolicyMutation.isPending ? '저장 중...' : '저장'}</button></div>
    </form></div></div> : null}

    {openRequestForm ? <div className="modal-backdrop"><div className="modal-card"><h2>{editingRequestId ? '휴가 요청 수정' : '휴가 요청 등록'}</h2><form className="modal-form" onSubmit={submitRequestForm}>
      <label><span>직원</span><select {...requestForm.register('employeeId', { valueAsNumber: true })}>{employees.map((e) => <option key={e.id} value={e.id}>{e.name}</option>)}</select></label>
      <label><span>휴가종류</span><select {...requestForm.register('leaveTypeCode')}><option value="ANNUAL">연차</option><option value="SICK">병가</option><option value="SPECIAL">특별휴가</option></select></label>
      <label><span>시작일</span><input type="date" {...requestForm.register('startDate')} /></label>
      <label><span>종료일</span><input type="date" {...requestForm.register('endDate')} /></label>
      <label><span>시작시간(선택)</span><input type="time" {...requestForm.register('startTime')} /></label>
      <label><span>종료시간(선택)</span><input type="time" {...requestForm.register('endTime')} /></label>
      <label><span>사용일수</span><input type="number" step="0.5" {...requestForm.register('daysCount', { valueAsNumber: true })} /></label>
      <label><span>상태</span><select {...requestForm.register('statusCode')}><option value="REQUESTED">신청</option><option value="APPROVED">승인</option><option value="REJECTED">반려</option></select></label>
      <label><span>사유</span><textarea rows={3} {...requestForm.register('reason')} /></label>
      <div className="modal-actions"><button type="button" className="secondary-button" onClick={() => setOpenRequestForm(false)}>취소</button><button type="submit" className="primary-button" disabled={saveRequestMutation.isPending}>{saveRequestMutation.isPending ? '저장 중...' : '저장'}</button></div>
    </form></div></div> : null}

    <ConfirmDialog open={!!deletePolicyTarget} title="휴가 정책 삭제" description={`'${deletePolicyTarget?.policyName ?? ''}' 정책을 삭제하시겠습니까?`} confirmText="삭제" onCancel={() => setDeletePolicyTarget(null)} onConfirm={() => deletePolicyTarget && deletePolicyMutation.mutate(deletePolicyTarget.id)} />
    <ConfirmDialog open={!!deleteRequestTarget} title="휴가 요청 삭제" description={`${deleteRequestTarget?.employeeName ?? ''}의 휴가 요청을 삭제하시겠습니까?`} confirmText="삭제" onCancel={() => setDeleteRequestTarget(null)} onConfirm={() => deleteRequestTarget && deleteRequestMutation.mutate(deleteRequestTarget.id)} />
  </div>;
}
