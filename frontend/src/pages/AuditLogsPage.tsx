import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useQuery } from '@tanstack/react-query';
import { fetchAuditLogs } from '../api/auditApi';
import { EmptyState } from '../components/EmptyState';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { PageHeader } from '../components/PageHeader';
import { Pagination } from '../components/Pagination';
import type { AuditLogSearchParams } from '../types/audit';

const defaultFilter: AuditLogSearchParams = { actionType: '', targetTable: '', keyword: '', page: 0, size: 20 };

export function AuditLogsPage() {
  const [filters, setFilters] = useState<AuditLogSearchParams>(defaultFilter);
  const filterForm = useForm<AuditLogSearchParams>({ defaultValues: defaultFilter });
  const query = useQuery({ queryKey: ['audit-logs', filters], queryFn: () => fetchAuditLogs(filters) });
  const submit = filterForm.handleSubmit((values) => setFilters({ ...filters, ...values, page: 0 }));

  return <div>
    <PageHeader title="감사 로그" description="운영 추적용 감사 로그를 조회합니다." />
    <section className="card">
      <form className="filter-grid four" onSubmit={submit}>
        <select {...filterForm.register('actionType')}><option value="">전체 작업</option><option value="CREATE">CREATE</option><option value="UPDATE">UPDATE</option></select>
        <select {...filterForm.register('targetTable')}><option value="">전체 대상</option><option value="employee">employee</option><option value="department">department</option></select>
        <input placeholder="JSON/브라우저 정보 검색" {...filterForm.register('keyword')} />
        <button type="submit" className="secondary-button">검색</button>
      </form>
    </section>
    <section className="card">
      {query.isLoading ? <LoadingSpinner text="감사 로그를 불러오는 중입니다." /> : null}
      {!query.isLoading && !(query.data?.content.length) ? <EmptyState title="감사 로그가 없습니다." description="조건을 바꾸어 다시 조회해 보세요." /> : null}
      {!!query.data?.content.length && <>
        <table className="data-table"><thead><tr><th>ID</th><th>작업</th><th>대상</th><th>IP</th><th>일시</th></tr></thead><tbody>
          {query.data.content.map((item) => <tr key={item.id}><td>{item.id}</td><td>{item.actionType}</td><td>{item.targetTable}#{item.targetId}</td><td>{item.ipAddress ?? '-'}</td><td>{item.createdAt?.replace('T',' ')}</td></tr>)}
        </tbody></table>
        <Pagination page={filters.page} size={filters.size} totalPages={query.data.totalPages} totalElements={query.data.totalElements} onChange={(page) => setFilters({ ...filters, page })} />
      </>}
    </section>
  </div>;
}
