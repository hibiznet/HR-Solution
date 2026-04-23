import type { PageResponse } from './common';

export interface CodeGroupSummary {
  id: number;
  groupCode: string;
  groupName: string;
  description?: string | null;
  isActive: boolean;
}
export interface CodeDetailSummary {
  id: number;
  groupId: number;
  groupCode: string;
  detailCode: string;
  detailName: string;
  sortOrder: number;
  isActive: boolean;
}
export interface CodeGroupSearchParams { keyword?: string; isActive?: string; page?: number; size?: number; }
export interface CodeDetailSearchParams { keyword?: string; groupId?: string; isActive?: string; page?: number; size?: number; }
export interface CodeGroupUpsertRequest {
  groupCode: string;
  groupName: string;
  description?: string;
  isActive: boolean;
}
export interface CodeDetailUpsertRequest {
  groupId: number;
  detailCode: string;
  detailName: string;
  sortOrder: number;
  isActive: boolean;
}
export type CodeGroupPage = PageResponse<CodeGroupSummary>;
export type CodeDetailPage = PageResponse<CodeDetailSummary>;
