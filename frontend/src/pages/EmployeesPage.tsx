import { useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { fetchDepartments } from '../api/departmentApi';
import { createEmployee, deleteEmployee, fetchEmployee, fetchEmployees, updateEmployee } from '../api/employeeApi';
import { ConfirmDialog } from '../components/ConfirmDialog';
import { EmptyState } from '../components/EmptyState';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { PageHeader } from '../components/PageHeader';
import { Pagination } from '../components/Pagination';
import { useAuthStore } from '../features/auth/authStore';
import type { EmployeeSearchParams, EmployeeSummary, EmployeeUpsertRequest } from '../types/employee';
import { toastSuccess } from '../utils/toast';

const defaultFilter: EmployeeSearchParams = { keyword: '', departmentId: '', statusCode: '', employmentTypeCode: '', page: 0, size: 10 };
const defaultForm: EmployeeUpsertRequest = { employeeNo: '', name: '', email: '', phone: '', statusCode: 'ACTIVE', employmentTypeCode: 'REGULAR', departmentId: 0, jobTitleId: null, managerEmployeeId: null, hireDate: new Date().toISOString().slice(0, 10), probationEndDate: '', workTypeCode: 'OFFICE' };

export function EmployeesPage() {
  const canManageOrg = useAuthStore((state) => state.canManageOrg);
  const queryClient = useQueryClient();
  const [filters, setFilters] = useState<EmployeeSearchParams>(defaultFilter);
  const [openForm, setOpenForm] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<EmployeeSummary | null>(null);
  const listQuery = useQuery({ queryKey: ['employees', filters], queryFn: () => fetchEmployees(filters) });
  const departmentQuery = useQuery({ queryKey: ['employee-form', 'departments'], queryFn: () => fetchDepartments({ page: 0, size: 200 }) });
  const employeeOptionQuery = useQuery({ queryKey: ['employee-form', 'employees'], queryFn: () => fetchEmployees({ page: 0, size: 200 }) });
  const filterForm = useForm<EmployeeSearchParams>({ defaultValues: defaultFilter });
  const form = useForm<EmployeeUpsertRequest>({ defaultValues: defaultForm });

  const saveMutation = useMutation({ mutationFn: async (payload: EmployeeUpsertRequest) => editingId ? updateEmployee(editingId, payload) : createEmployee(payload), onSuccess: () => { toastSuccess(editingId ? '직원 정보가 수정되었습니다.' : '직원이 등록되었습니다.'); setOpenForm(false); setEditingId(null); form.reset(defaultForm); queryClient.invalidateQueries({ queryKey: ['employees'] }); }});
  const deleteMutation = useMutation({ mutationFn: (id: number) => deleteEmployee(id), onSuccess: () => { toastSuccess('직원 정보가 삭제되었습니다.'); setDeleteTarget(null); queryClient.invalidateQueries({ queryKey: ['employees'] }); }});
  const departmentOptions = departmentQuery.data?.content ?? [];
  const managerOptions = useMemo(() => (employeeOptionQuery.data?.content ?? []).filter((item) => item.id !== editingId), [editingId, employeeOptionQuery.data?.content]);

  const openCreate = () => { setEditingId(null); form.reset(defaultForm); setOpenForm(true); };
  const openEdit = async (id: number) => { const detail = await fetchEmployee(id); setEditingId(id); form.reset({ employeeNo: detail.employeeNo, name: detail.name, email: detail.email, phone: detail.phone ?? '', statusCode: detail.statusCode, employmentTypeCode: detail.employmentTypeCode, departmentId: detail.departmentId ?? 0, jobTitleId: detail.jobTitleId ?? null, managerEmployeeId: detail.managerEmployeeId ?? null, hireDate: detail.hireDate?.slice(0, 10) ?? new Date().toISOString().slice(0, 10), probationEndDate: '', workTypeCode: 'OFFICE' }); setOpenForm(true); };
  const submitFilter = filterForm.handleSubmit((values) => setFilters({ ...filters, ...values, page: 0 }));
  const submitForm = form.handleSubmit((values) => saveMutation.mutate({ ...values, departmentId: Number(values.departmentId), managerEmployeeId: values.managerEmployeeId ? Number(values.managerEmployeeId) : null, jobTitleId: values.jobTitleId ? Number(values.jobTitleId) : null }));

  return <div>
    <PageHeader title="직원 관리" description="조직 권한에 따라 조회 범위가 제한되며, 인사 권한이 있으면 등록/수정/삭제가 가능합니다." actions={canManageOrg() ? <button type="button" className="primary-button" onClick={openCreate}>직원 등록</button> : undefined} />
    <section className="card"><form className="filter-grid four" onSubmit={submitFilter}><input placeholder="이름/사번/이메일 검색" {...filterForm.register('keyword')} /><select {...filterForm.register('departmentId')}><option value="">전체 부서</option>{departmentOptions.map((item) => <option key={item.id} value={item.id}>{item.deptName}</option>)}</select><select {...filterForm.register('statusCode')}><option value="">전체 상태</option><option value="ACTIVE">재직</option><option value="ON_LEAVE">휴직</option><option value="RESIGNED">퇴직</option></select><select {...filterForm.register('employmentTypeCode')}><option value="">전체 고용형태</option><option value="REGULAR">정규직</option><option value="CONTRACT">계약직</option></select><button type="submit" className="secondary-button">검색</button></form></section>
    <section className="card">{listQuery.isLoading ? <LoadingSpinner text="직원 목록을 불러오는 중입니다." /> : null}{!listQuery.isLoading && !(listQuery.data?.content.length) ? <EmptyState title="조회된 직원이 없습니다." description="조건을 바꾸어 다시 조회해 보세요." /> : null}{!!listQuery.data?.content.length && <><table className="data-table"><thead><tr><th>사번</th><th>이름</th><th>이메일</th><th>부서</th><th>직위</th><th>상태</th><th>작업</th></tr></thead><tbody>{listQuery.data.content.map((item) => <tr key={item.id}><td>{item.employeeNo}</td><td>{item.name}</td><td>{item.email}</td><td>{item.departmentName}</td><td>{item.jobTitleName ?? '-'}</td><td>{item.statusCode}</td><td>{canManageOrg() ? <div className="inline-actions"><button type="button" className="secondary-button small" onClick={() => openEdit(item.id)}>수정</button><button type="button" className="danger-button small" onClick={() => setDeleteTarget(item)}>삭제</button></div> : '-'}</td></tr>)}</tbody></table><Pagination page={filters.page} size={filters.size} totalPages={listQuery.data.totalPages} totalElements={listQuery.data.totalElements} onChange={(page) => setFilters({ ...filters, page })} /></>}</section>
    {openForm ? <div className="modal-overlay"><div className="modal-card wide"><h2>{editingId ? '직원 수정' : '직원 등록'}</h2><form className="form-grid two" onSubmit={submitForm}><label><span>사번</span><input {...form.register('employeeNo', { required: true })} /></label><label><span>이름</span><input {...form.register('name', { required: true })} /></label><label><span>이메일</span><input type="email" {...form.register('email', { required: true })} /></label><label><span>연락처</span><input {...form.register('phone')} /></label><label><span>입사일</span><input type="date" {...form.register('hireDate', { required: true })} /></label><label><span>수습종료일</span><input type="date" {...form.register('probationEndDate')} /></label><label><span>부서</span><select {...form.register('departmentId', { valueAsNumber: true, required: true })}><option value="">선택</option>{departmentOptions.map((item) => <option key={item.id} value={item.id}>{item.deptName}</option>)}</select></label><label><span>관리자</span><select {...form.register('managerEmployeeId')}><option value="">없음</option>{managerOptions.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}</select></label><label><span>재직상태</span><select {...form.register('statusCode')}><option value="ACTIVE">재직</option><option value="ON_LEAVE">휴직</option><option value="RESIGNED">퇴직</option></select></label><label><span>고용형태</span><select {...form.register('employmentTypeCode')}><option value="REGULAR">정규직</option><option value="CONTRACT">계약직</option></select></label><label><span>근무형태</span><select {...form.register('workTypeCode')}><option value="OFFICE">출근</option><option value="REMOTE">재택</option><option value="HYBRID">혼합</option></select></label><label><span>직위 ID</span><input type="number" {...form.register('jobTitleId', { setValueAs: (v) => (v === '' ? null : Number(v)) })} placeholder="선택사항" /></label><div className="form-actions"><button type="button" className="secondary-button" onClick={() => setOpenForm(false)}>취소</button><button type="submit" className="primary-button" disabled={saveMutation.isPending}>{saveMutation.isPending ? '저장 중...' : '저장'}</button></div></form></div></div> : null}
    <ConfirmDialog open={!!deleteTarget} title="직원 삭제" description={deleteTarget ? `${deleteTarget.name} 직원을 삭제하시겠습니까?` : ''} onClose={() => setDeleteTarget(null)} onConfirm={() => deleteTarget && deleteMutation.mutate(deleteTarget.id)} />
  </div>;
}
