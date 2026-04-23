import { http } from './http';
import { normalizePage, unwrapData } from './helpers';
import type { RoleSummary, UserPage, UserSearchParams, UserSummary, UserUpsertRequest } from '../types/userAdmin';

export async function fetchUsers(params: UserSearchParams): Promise<UserPage> {
  const response = await http.get('/api/admin/users', { params });
  return normalizePage<UserSummary>(unwrapData(response), Number(params.page ?? 0), Number(params.size ?? 10));
}
export async function fetchUser(id: number): Promise<UserSummary> {
  const response = await http.get(`/api/admin/users/${id}`);
  return unwrapData(response);
}
export async function createUser(payload: UserUpsertRequest): Promise<UserSummary> {
  const response = await http.post('/api/admin/users', payload);
  return unwrapData(response);
}
export async function updateUser(id: number, payload: UserUpsertRequest): Promise<UserSummary> {
  const response = await http.put(`/api/admin/users/${id}`, payload);
  return unwrapData(response);
}
export async function deleteUser(id: number): Promise<void> {
  await http.delete(`/api/admin/users/${id}`);
}
export async function fetchRoles(): Promise<RoleSummary[]> {
  const response = await http.get('/api/admin/roles');
  return unwrapData(response);
}
