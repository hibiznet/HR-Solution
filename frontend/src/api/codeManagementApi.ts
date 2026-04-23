import { http } from './http';
import { normalizePage, unwrapData } from './helpers';
import type { CodeDetailPage, CodeDetailSearchParams, CodeDetailSummary, CodeDetailUpsertRequest, CodeGroupPage, CodeGroupSearchParams, CodeGroupSummary, CodeGroupUpsertRequest } from '../types/codeManagement';

export async function fetchCodeGroups(params: CodeGroupSearchParams): Promise<CodeGroupPage> {
  const response = await http.get('/api/codes/groups', { params });
  return normalizePage<CodeGroupSummary>(unwrapData(response), Number(params.page ?? 0), Number(params.size ?? 10));
}
export async function fetchCodeGroup(id: number): Promise<CodeGroupSummary> {
  const response = await http.get(`/api/codes/groups/${id}`);
  return unwrapData(response);
}
export async function createCodeGroup(payload: CodeGroupUpsertRequest): Promise<CodeGroupSummary> {
  const response = await http.post('/api/codes/groups', payload);
  return unwrapData(response);
}
export async function updateCodeGroup(id: number, payload: CodeGroupUpsertRequest): Promise<CodeGroupSummary> {
  const response = await http.put(`/api/codes/groups/${id}`, payload);
  return unwrapData(response);
}
export async function deleteCodeGroup(id: number): Promise<void> {
  await http.delete(`/api/codes/groups/${id}`);
}

export async function fetchCodeDetails(params: CodeDetailSearchParams): Promise<CodeDetailPage> {
  const response = await http.get('/api/codes/details', { params });
  return normalizePage<CodeDetailSummary>(unwrapData(response), Number(params.page ?? 0), Number(params.size ?? 10));
}
export async function fetchCodeDetail(id: number): Promise<CodeDetailSummary> {
  const response = await http.get(`/api/codes/details/${id}`);
  return unwrapData(response);
}
export async function createCodeDetail(payload: CodeDetailUpsertRequest): Promise<CodeDetailSummary> {
  const response = await http.post('/api/codes/details', payload);
  return unwrapData(response);
}
export async function updateCodeDetail(id: number, payload: CodeDetailUpsertRequest): Promise<CodeDetailSummary> {
  const response = await http.put(`/api/codes/details/${id}`, payload);
  return unwrapData(response);
}
export async function deleteCodeDetail(id: number): Promise<void> {
  await http.delete(`/api/codes/details/${id}`);
}
