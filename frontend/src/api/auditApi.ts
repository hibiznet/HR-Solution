import { http } from './http';
import { normalizePage, unwrapData } from './helpers';
import type { AuditLogItem, AuditLogSearchParams } from '../types/audit';
import type { ApiEnvelope, PageResponse } from '../types/common';

export async function fetchAuditLogs(params: AuditLogSearchParams): Promise<PageResponse<AuditLogItem>> {
  const response = await http.get<ApiEnvelope<PageResponse<AuditLogItem>> | PageResponse<AuditLogItem>>('/api/admin/audit-logs', { params });
  return normalizePage<AuditLogItem>(unwrapData(response), params.page, params.size);
}
