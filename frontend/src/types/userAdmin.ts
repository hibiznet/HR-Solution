import type { PageResponse } from './common';

export interface RoleSummary {
  id: number;
  roleCode: string;
  roleName: string;
  description?: string | null;
}
export interface UserSummary {
  id: number;
  username: string;
  email: string;
  isActive: boolean;
  roles: string[];
}
export interface UserSearchParams { keyword?: string; isActive?: string; page?: number; size?: number; }
export interface UserUpsertRequest {
  username: string;
  email: string;
  password?: string;
  isActive: boolean;
  roleCodes: string[];
}
export type UserPage = PageResponse<UserSummary>;
