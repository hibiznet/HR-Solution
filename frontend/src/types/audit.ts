export interface AuditLogItem {
  id: number;
  actorUserId?: number | null;
  actionType: string;
  targetTable: string;
  targetId: number;
  beforeDataJson?: string | null;
  afterDataJson?: string | null;
  ipAddress?: string | null;
  userAgent?: string | null;
  createdAt: string;
}

export interface AuditLogSearchParams {
  actionType?: string;
  targetTable?: string;
  keyword?: string;
  page: number;
  size: number;
}
