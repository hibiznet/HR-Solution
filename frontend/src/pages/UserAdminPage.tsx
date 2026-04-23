import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { createUser, deleteUser, fetchRoles, fetchUser, fetchUsers, updateUser } from '../api/userAdminApi';
import { ConfirmDialog } from '../components/ConfirmDialog';
import { EmptyState } from '../components/EmptyState';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { PageHeader } from '../components/PageHeader';
import { Pagination } from '../components/Pagination';
import type { UserSearchParams, UserSummary, UserUpsertRequest } from '../types/userAdmin';
import { toastSuccess } from '../utils/toast';

const defaultFilter: UserSearchParams = { keyword: '', isActive: '', page: 0, size: 10 };
const defaultForm: UserUpsertRequest = { username: '', email: '', password: '', isActive: true, roleCodes: ['ROLE_EMPLOYEE'] };

export function UserAdminPage() {
  const queryClient = useQueryClient();
  const [filters, setFilters] = useState<UserSearchParams>(defaultFilter);
  const [openForm, setOpenForm] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<UserSummary | null>(null);
  const listQuery = useQuery({ queryKey: ['admin-users', filters], queryFn: () => fetchUsers(filters) });
  const rolesQuery = useQuery({ queryKey: ['admin-roles'], queryFn: fetchRoles });
  const filterForm = useForm<UserSearchParams>({ defaultValues: defaultFilter });
  const form = useForm<UserUpsertRequest>({ defaultValues: defaultForm });

  const saveMutation = useMutation({
    mutationFn: async (payload: UserUpsertRequest) => editingId ? updateUser(editingId, payload) : createUser(payload),
    onSuccess: () => { toastSuccess(editingId ? '사용자가 수정되었습니다.' : '사용자가 등록되었습니다.'); setOpenForm(false); setEditingId(null); form.reset(defaultForm); queryClient.invalidateQueries({ queryKey: ['admin-users'] }); }
  });
  const deleteMutation = useMutation({ mutationFn: (id: number) => deleteUser(id), onSuccess: () => { toastSuccess('사용자가 삭제되었습니다.'); setDeleteTarget(null); queryClient.invalidateQueries({ queryKey: ['admin-users'] }); }});

  const openCreate = () => { setEditingId(null); form.reset(defaultForm); setOpenForm(true); };
  const openEdit = async (id: number) => {
    const detail = await fetchUser(id);
    setEditingId(id);
    form.reset({ username: detail.username, email: detail.email, password: '', isActive: detail.isActive, roleCodes: detail.roles });
    setOpenForm(true);
  };

  const submitFilter = filterForm.handleSubmit((values) => setFilters({ ...filters, ...values, page: 0 }));
  const submitForm = form.handleSubmit((values) => saveMutation.mutate({ ...values, roleCodes: values.roleCodes.filter(Boolean) }));

  return <div>
    <PageHeader title="사용자 / 권한 관리" description="사용자 계정, 활성 상태, 역할 매핑을 관리합니다." actions={<button type="button" className="primary-button" onClick={openCreate}>사용자 등록</button>} />
    <section className="card">
      <form className="filter-grid" onSubmit={submitFilter}>
        <input placeholder="아이디/이메일 검색" {...filterForm.register('keyword')} />
        <select {...filterForm.register('isActive')}><option value="">상태 전체</option><option value="true">활성</option><option value="false">비활성</option></select>
        <button className="secondary-button" type="submit">조회</button>
      </form>
      {listQuery.isLoading ? <LoadingSpinner /> : !listQuery.data?.content.length ? <EmptyState title="사용자 계정이 없습니다." /> : (
        <>
          <table className="data-table"><thead><tr><th>아이디</th><th>이메일</th><th>상태</th><th>역할</th><th>작업</th></tr></thead><tbody>
            {listQuery.data.content.map((item) => <tr key={item.id}><td>{item.username}</td><td>{item.email}</td><td>{item.isActive ? '활성' : '비활성'}</td><td>{item.roles.join(', ')}</td><td className="table-actions"><button type="button" className="text-button" onClick={() => openEdit(item.id)}>수정</button><button type="button" className="text-button danger" onClick={() => setDeleteTarget(item)}>삭제</button></td></tr>)}
          </tbody></table>
          <Pagination page={listQuery.data.page} size={listQuery.data.size} totalPages={listQuery.data.totalPages} onChange={(nextPage) => setFilters((prev) => ({ ...prev, page: nextPage }))} />
        </>
      )}
    </section>

    {openForm ? <div className="modal-backdrop"><div className="modal-card"><h2>{editingId ? '사용자 수정' : '사용자 등록'}</h2><form className="modal-form" onSubmit={submitForm}>
      <label><span>아이디</span><input {...form.register('username', { required: true })} /></label>
      <label><span>이메일</span><input type="email" {...form.register('email', { required: true })} /></label>
      <label><span>비밀번호 {editingId ? '(변경 시 입력)' : ''}</span><input type="password" {...form.register('password')} /></label>
      <label className="checkbox-row"><input type="checkbox" {...form.register('isActive')} /><span>활성</span></label>
      <label><span>역할</span><div className="checkbox-group">
        {(rolesQuery.data ?? []).map((role) => <label key={role.roleCode} className="checkbox-row"><input type="checkbox" value={role.roleCode} {...form.register('roleCodes')} /><span>{role.roleCode}</span></label>)}
      </div></label>
      <div className="modal-actions"><button type="button" className="secondary-button" onClick={() => setOpenForm(false)}>취소</button><button type="submit" className="primary-button" disabled={saveMutation.isPending}>{saveMutation.isPending ? '저장 중...' : '저장'}</button></div>
    </form></div></div> : null}

    <ConfirmDialog open={!!deleteTarget} title="사용자 삭제" description={`'${deleteTarget?.username ?? ''}' 계정을 삭제하시겠습니까?`} confirmText="삭제" onCancel={() => setDeleteTarget(null)} onConfirm={() => deleteTarget && deleteMutation.mutate(deleteTarget.id)} />
  </div>;
}
