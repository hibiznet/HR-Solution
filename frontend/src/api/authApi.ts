import { http } from './http';
import { unwrapData } from './helpers';
import type { AuthTokenResponse, LoginRequest, MeResponse } from '../types/auth';

export async function login(payload: LoginRequest): Promise<AuthTokenResponse> {
  const response = await http.post('/api/auth/login', payload, { silent: true } as never);
  return unwrapData<AuthTokenResponse>(response);
}

export async function logout(refreshToken: string): Promise<void> {
  await http.post('/api/auth/logout', { refreshToken }, { silent: true } as never);
}

export async function getMe(): Promise<MeResponse> {
  const response = await http.get('/api/auth/me', { silent: true } as never);
  return unwrapData<MeResponse>(response);
}
