import { useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { createDepartment, deleteDepartment, fetchDepartment, fetchDepartments, updateDepartment } from '../api/departmentApi';
import { ConfirmDialog } from '../components/ConfirmDialog';
import { EmptyState } from '../components/EmptyState';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { PageHeader } from '../components/PageHeader';
import { Pagination } from '../components/Pagination';
import { useAuthStore } from '../features/auth/authStore';
import type { DepartmentSearchParams, DepartmentSummary, DepartmentUpsertRequest } from '../types/department';
import { toastSuccess } from '../utils/toast';

const defaultFilter: DepartmentSearchParams = { keyword: '', parentId: '', isActive: '', page: 0, size: 10 };
const defaultForm: DepartmentUpsertRequest = { deptCode: '', deptName: '', deptType: '', parentId: null, sortOrder: 0, isActive: true };

export function DepartmentsPage() {
  const canManageOrg = useAuthStore((state) => state.canManageOrg);
  const queryClient = useQueryClient();
  const [filters, setFilters] = useState<DepartmentSearchParams>(defaultFilter);
  const [openForm, setOpenForm] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<DepartmentSummary | null>(null);

  const listQuery = useQuery({ queryKey: ['departments', filters], queryFn: () => fetchDepartments(filters) });
  const departmentOptions = useMemo(() => listQuery.data?.content ?? [], [listQuery.data]);
  const filterForm = useForm<DepartmentSearchParams>({ defaultValues: defaultFilter });
  const form = useForm<DepartmentUpsertRequest>({ defaultValues: defaultForm });

  const saveMutation = useMutation({ mutationFn: async (payload: DepartmentUpsertRequest) => editingId ? updateDepartment(editingId, payload) : createDepartment(payload), onSuccess: () => {
    toastSuccess(editingId ? '부서가 수정되었습니다.' : '부서가 등록되었습니다.'); setOpenForm(false); setEditingId(null); form.reset(defaultForm); queryClient.invalidateQueries({ queryKey: ['departments'] });
  }});
  const deleteMutation = useMutation({ mutationFn: (id: number) => deleteDepartment(id), onSuccess: () => { toastSuccess('부서가 삭제되었습니다.'); setDeleteTarget(null); queryClient.invalidateQueries({ queryKey: ['departments'] }); }});

  const openCreate = () => { setEditingId(null); form.reset(defaultForm); setOpenForm(true); };
  const openEdit = async (id: number) => { const detail = await fetchDepartment(id); setEditingId(id); form.reset({ deptCode: detail.deptCode, deptName: detail.deptName, deptType: detail.deptType ?? '', parentId: detail.parentId ?? null, sortOrder: detail.sortOrder ?? 0, isActive: detail.isActive ?? true }); setOpenForm(true); };
  const submitFilter = filterForm.handleSubmit((values) => setFilters({ ...filters, ...values, page: 0 }));
  const submitForm = form.handleSubmit((values) => saveMutation.mutate({ ...values, parentId: values.parentId ? Number(values.parentId) : null }));

  return <div>
    <PageHeader title="부서 관리" description="조직 구조를 조회하고, 권한이 있으면 등록/수정/삭제할 수 있습니다." actions={canManageOrg() ? <button type="button" className="primary-button" onClick={openCreate}>부서 등록</button> : undefined} />
    <section className="card"><form className="filter-grid" onSubmit={submitFilter}><input placeholder="부서명/코드 검색" {...filterForm.register('keyword')} /><select {...filterForm.register('isActive', { setValueAs: (v) => v === '' ? '' : v === 'true' })}><option value="">전체 상태</option><option value="true">활성</option><option value="false">비활성</option></select><button type="submit" className="secondary-button">검색</button></form></section>
    <section className="card">{listQuery.isLoading ? <LoadingSpinner text="부서 목록을 불러오는 중입니다." /> : null}{!listQuery.isLoading && !(listQuery.data?.content.length) ? <EmptyState title="등록된 부서가 없습니다." description="새 부서를 등록해 보세요." /> : null}{!!listQuery.data?.content.length && <><table className="data-table"><thead><tr><th>부서코드</th><th>부서명</th><th>유형</th><th>상위부서</th><th>상태</th><th>작업</th></tr></thead><tbody>{listQuery.data.content.map((item) => <tr key={item.id}><td>{item.deptCode}</td><td>{item.deptName}</td><td>{item.deptType ?? '-'}</td><td>{item.parentName ?? '-'}</td><td>{item.isActive === false ? '비활성' : '활성'}</td><td>{canManageOrg() ? <div className="inline-actions"><button type="button" className="secondary-button small" onClick={() => openEdit(item.id)}>수정</button><button type="button" className="danger-button small" onClick={() => setDeleteTarget(item)}>삭제</button></div> : '-'}</td></tr>)}</tbody></table><Pagination page={filters.page} size={filters.size} totalPages={listQuery.data.totalPages} totalElements={listQuery.data.totalElements} onChange={(page) => setFilters({ ...filters, page })} /></>}</section>
    {openForm && <div className="modal-overlay"><div className="modal-card"><h2>{editingId ? '부서 수정' : '부서 등록'}</h2><form className="form-grid" onSubmit={submitForm}><label><span>부서코드</span><input {...form.register('deptCode', { required: true })} /></label><label><span>부서명</span><input {...form.register('deptName', { required: true })} /></label><label><span>부서유형</span><input {...form.register('deptType')} placeholder="TEAM" /></label><label><span>상위부서</span><select {...form.register('parentId')}><option value="">없음</option>{departmentOptions.filter((item) => item.id !== editingId).map((item) => <option key={item.id} value={item.id}>{item.deptName}</option>)}</select></label><label><span>정렬순서</span><input type="number" {...form.register('sortOrder', { valueAsNumber: true })} /></label><label><span>사용여부</span><select {...form.register('isActive', { setValueAs: (v) => v === 'true' || v === true })}><option value="true">활성</option><option value="false">비활성</option></select></label><div className="form-actions"><button type="button" className="secondary-button" onClick={() => setOpenForm(false)}>취소</button><button type="submit" className="primary-button" disabled={saveMutation.isPending}>{saveMutation.isPending ? '저장 중...' : '저장'}</button></div></form></div></div>}
    <ConfirmDialog open={!!deleteTarget} title="부서 삭제" description={deleteTarget ? `${deleteTarget.deptName} 부서를 삭제하시겠습니까?` : ''} onClose={() => setDeleteTarget(null)} onConfirm={() => deleteTarget && deleteMutation.mutate(deleteTarget.id)} />
  </div>;
}
