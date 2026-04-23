import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { createCodeDetail, createCodeGroup, deleteCodeDetail, deleteCodeGroup, fetchCodeDetail, fetchCodeDetails, fetchCodeGroup, fetchCodeGroups, updateCodeDetail, updateCodeGroup } from '../api/codeManagementApi';
import { ConfirmDialog } from '../components/ConfirmDialog';
import { EmptyState } from '../components/EmptyState';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { PageHeader } from '../components/PageHeader';
import { Pagination } from '../components/Pagination';
import type { CodeDetailSearchParams, CodeDetailSummary, CodeDetailUpsertRequest, CodeGroupSearchParams, CodeGroupSummary, CodeGroupUpsertRequest } from '../types/codeManagement';
import { toastSuccess } from '../utils/toast';

const defaultGroupFilter: CodeGroupSearchParams = { keyword: '', isActive: '', page: 0, size: 5 };
const defaultGroupForm: CodeGroupUpsertRequest = { groupCode: '', groupName: '', description: '', isActive: true };
const defaultDetailFilter: CodeDetailSearchParams = { keyword: '', groupId: '', isActive: '', page: 0, size: 10 };
const defaultDetailForm: CodeDetailUpsertRequest = { groupId: 0, detailCode: '', detailName: '', sortOrder: 0, isActive: true };

export function CodeManagementPage() {
  const queryClient = useQueryClient();
  const [groupFilters, setGroupFilters] = useState<CodeGroupSearchParams>(defaultGroupFilter);
  const [detailFilters, setDetailFilters] = useState<CodeDetailSearchParams>(defaultDetailFilter);
  const [openGroupForm, setOpenGroupForm] = useState(false);
  const [openDetailForm, setOpenDetailForm] = useState(false);
  const [editingGroupId, setEditingGroupId] = useState<number | null>(null);
  const [editingDetailId, setEditingDetailId] = useState<number | null>(null);
  const [deleteGroupTarget, setDeleteGroupTarget] = useState<CodeGroupSummary | null>(null);
  const [deleteDetailTarget, setDeleteDetailTarget] = useState<CodeDetailSummary | null>(null);

  const groupQuery = useQuery({ queryKey: ['code-groups', groupFilters], queryFn: () => fetchCodeGroups(groupFilters) });
  const detailQuery = useQuery({ queryKey: ['code-details', detailFilters], queryFn: () => fetchCodeDetails(detailFilters) });
  const groupFilterForm = useForm<CodeGroupSearchParams>({ defaultValues: defaultGroupFilter });
  const groupForm = useForm<CodeGroupUpsertRequest>({ defaultValues: defaultGroupForm });
  const detailFilterForm = useForm<CodeDetailSearchParams>({ defaultValues: defaultDetailFilter });
  const detailForm = useForm<CodeDetailUpsertRequest>({ defaultValues: defaultDetailForm });

  const saveGroupMutation = useMutation({
    mutationFn: async (payload: CodeGroupUpsertRequest) => editingGroupId ? updateCodeGroup(editingGroupId, payload) : createCodeGroup(payload),
    onSuccess: () => { toastSuccess(editingGroupId ? '코드 그룹이 수정되었습니다.' : '코드 그룹이 등록되었습니다.'); setOpenGroupForm(false); setEditingGroupId(null); groupForm.reset(defaultGroupForm); queryClient.invalidateQueries({ queryKey: ['code-groups'] }); }
  });
  const deleteGroupMutation = useMutation({ mutationFn: (id: number) => deleteCodeGroup(id), onSuccess: () => { toastSuccess('코드 그룹이 삭제되었습니다.'); setDeleteGroupTarget(null); queryClient.invalidateQueries({ queryKey: ['code-groups'] }); }});

  const saveDetailMutation = useMutation({
    mutationFn: async (payload: CodeDetailUpsertRequest) => editingDetailId ? updateCodeDetail(editingDetailId, payload) : createCodeDetail(payload),
    onSuccess: () => { toastSuccess(editingDetailId ? '코드 상세가 수정되었습니다.' : '코드 상세가 등록되었습니다.'); setOpenDetailForm(false); setEditingDetailId(null); detailForm.reset(defaultDetailForm); queryClient.invalidateQueries({ queryKey: ['code-details'] }); }
  });
  const deleteDetailMutation = useMutation({ mutationFn: (id: number) => deleteCodeDetail(id), onSuccess: () => { toastSuccess('코드 상세가 삭제되었습니다.'); setDeleteDetailTarget(null); queryClient.invalidateQueries({ queryKey: ['code-details'] }); }});

  const openGroupCreate = () => { setEditingGroupId(null); groupForm.reset(defaultGroupForm); setOpenGroupForm(true); };
  const openGroupEdit = async (id: number) => {
    const detail = await fetchCodeGroup(id);
    setEditingGroupId(id);
    groupForm.reset({ groupCode: detail.groupCode, groupName: detail.groupName, description: detail.description ?? '', isActive: detail.isActive });
    setOpenGroupForm(true);
  };
  const openDetailCreate = () => { setEditingDetailId(null); detailForm.reset({ ...defaultDetailForm, groupId: Number(detailFilters.groupId) || 0 }); setOpenDetailForm(true); };
  const openDetailEdit = async (id: number) => {
    const detail = await fetchCodeDetail(id);
    setEditingDetailId(id);
    detailForm.reset({ groupId: detail.groupId, detailCode: detail.detailCode, detailName: detail.detailName, sortOrder: detail.sortOrder, isActive: detail.isActive });
    setOpenDetailForm(true);
  };

  const submitGroupFilter = groupFilterForm.handleSubmit((values) => setGroupFilters({ ...groupFilters, ...values, page: 0 }));
  const submitGroupForm = groupForm.handleSubmit((values) => saveGroupMutation.mutate(values));
  const submitDetailFilter = detailFilterForm.handleSubmit((values) => setDetailFilters({ ...detailFilters, ...values, page: 0 }));
  const submitDetailForm = detailForm.handleSubmit((values) => saveDetailMutation.mutate({ ...values, groupId: Number(values.groupId), sortOrder: Number(values.sortOrder) }));

  const groupOptions = groupQuery.data?.content ?? [];

  return <div>
    <PageHeader title="코드 관리" description="공통 코드 그룹/상세를 관리합니다." actions={<div className="inline-actions"><button type="button" className="secondary-button" onClick={openGroupCreate}>그룹 등록</button><button type="button" className="primary-button" onClick={openDetailCreate}>상세 등록</button></div>} />

    <section className="card">
      <h2>코드 그룹</h2>
      <form className="filter-grid" onSubmit={submitGroupFilter}>
        <input placeholder="그룹 코드/명 검색" {...groupFilterForm.register('keyword')} />
        <select {...groupFilterForm.register('isActive')}><option value="">상태 전체</option><option value="true">활성</option><option value="false">비활성</option></select>
        <button className="secondary-button" type="submit">조회</button>
      </form>
      {groupQuery.isLoading ? <LoadingSpinner /> : !groupQuery.data?.content.length ? <EmptyState title="코드 그룹이 없습니다." /> : <>
        <table className="data-table"><thead><tr><th>그룹코드</th><th>그룹명</th><th>설명</th><th>활성</th><th>작업</th></tr></thead><tbody>
          {groupQuery.data.content.map((item) => <tr key={item.id}><td>{item.groupCode}</td><td>{item.groupName}</td><td>{item.description ?? '-'}</td><td>{item.isActive ? 'Y' : 'N'}</td><td className="table-actions"><button type="button" className="text-button" onClick={() => openGroupEdit(item.id)}>수정</button><button type="button" className="text-button danger" onClick={() => setDeleteGroupTarget(item)}>삭제</button></td></tr>)}
        </tbody></table>
        <Pagination page={groupQuery.data.page} size={groupQuery.data.size} totalPages={groupQuery.data.totalPages} onChange={(nextPage) => setGroupFilters((prev) => ({ ...prev, page: nextPage }))} />
      </>}
    </section>

    <section className="card">
      <h2>코드 상세</h2>
      <form className="filter-grid" onSubmit={submitDetailFilter}>
        <input placeholder="상세 코드/명 검색" {...detailFilterForm.register('keyword')} />
        <select {...detailFilterForm.register('groupId')}><option value="">그룹 전체</option>{groupOptions.map((g) => <option key={g.id} value={g.id}>{g.groupCode}</option>)}</select>
        <select {...detailFilterForm.register('isActive')}><option value="">상태 전체</option><option value="true">활성</option><option value="false">비활성</option></select>
        <button className="secondary-button" type="submit">조회</button>
      </form>
      {detailQuery.isLoading ? <LoadingSpinner /> : !detailQuery.data?.content.length ? <EmptyState title="코드 상세가 없습니다." /> : <>
        <table className="data-table"><thead><tr><th>그룹</th><th>상세코드</th><th>상세명</th><th>정렬</th><th>활성</th><th>작업</th></tr></thead><tbody>
          {detailQuery.data.content.map((item) => <tr key={item.id}><td>{item.groupCode}</td><td>{item.detailCode}</td><td>{item.detailName}</td><td>{item.sortOrder}</td><td>{item.isActive ? 'Y' : 'N'}</td><td className="table-actions"><button type="button" className="text-button" onClick={() => openDetailEdit(item.id)}>수정</button><button type="button" className="text-button danger" onClick={() => setDeleteDetailTarget(item)}>삭제</button></td></tr>)}
        </tbody></table>
        <Pagination page={detailQuery.data.page} size={detailQuery.data.size} totalPages={detailQuery.data.totalPages} onChange={(nextPage) => setDetailFilters((prev) => ({ ...prev, page: nextPage }))} />
      </>}
    </section>

    {openGroupForm ? <div className="modal-backdrop"><div className="modal-card"><h2>{editingGroupId ? '코드 그룹 수정' : '코드 그룹 등록'}</h2><form className="modal-form" onSubmit={submitGroupForm}>
      <label><span>그룹코드</span><input {...groupForm.register('groupCode', { required: true })} /></label>
      <label><span>그룹명</span><input {...groupForm.register('groupName', { required: true })} /></label>
      <label><span>설명</span><textarea rows={3} {...groupForm.register('description')} /></label>
      <label className="checkbox-row"><input type="checkbox" {...groupForm.register('isActive')} /><span>활성</span></label>
      <div className="modal-actions"><button type="button" className="secondary-button" onClick={() => setOpenGroupForm(false)}>취소</button><button type="submit" className="primary-button" disabled={saveGroupMutation.isPending}>{saveGroupMutation.isPending ? '저장 중...' : '저장'}</button></div>
    </form></div></div> : null}

    {openDetailForm ? <div className="modal-backdrop"><div className="modal-card"><h2>{editingDetailId ? '코드 상세 수정' : '코드 상세 등록'}</h2><form className="modal-form" onSubmit={submitDetailForm}>
      <label><span>그룹</span><select {...detailForm.register('groupId', { valueAsNumber: true })}>{groupOptions.map((g) => <option key={g.id} value={g.id}>{g.groupCode}</option>)}</select></label>
      <label><span>상세코드</span><input {...detailForm.register('detailCode', { required: true })} /></label>
      <label><span>상세명</span><input {...detailForm.register('detailName', { required: true })} /></label>
      <label><span>정렬순서</span><input type="number" {...detailForm.register('sortOrder', { valueAsNumber: true })} /></label>
      <label className="checkbox-row"><input type="checkbox" {...detailForm.register('isActive')} /><span>활성</span></label>
      <div className="modal-actions"><button type="button" className="secondary-button" onClick={() => setOpenDetailForm(false)}>취소</button><button type="submit" className="primary-button" disabled={saveDetailMutation.isPending}>{saveDetailMutation.isPending ? '저장 중...' : '저장'}</button></div>
    </form></div></div> : null}

    <ConfirmDialog open={!!deleteGroupTarget} title="코드 그룹 삭제" description={`'${deleteGroupTarget?.groupCode ?? ''}' 그룹을 삭제하시겠습니까?`} confirmText="삭제" onCancel={() => setDeleteGroupTarget(null)} onConfirm={() => deleteGroupTarget && deleteGroupMutation.mutate(deleteGroupTarget.id)} />
    <ConfirmDialog open={!!deleteDetailTarget} title="코드 상세 삭제" description={`'${deleteDetailTarget?.detailCode ?? ''}' 상세 코드를 삭제하시겠습니까?`} confirmText="삭제" onCancel={() => setDeleteDetailTarget(null)} onConfirm={() => deleteDetailTarget && deleteDetailMutation.mutate(deleteDetailTarget.id)} />
  </div>;
}
