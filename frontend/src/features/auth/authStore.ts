import { create } from 'zustand';
import type { MeResponse } from '../../types/auth';
import { clearTokens, getAccessToken, setTokens } from '../../utils/tokenStorage';

interface AuthState {
  isAuthenticated: boolean;
  user: MeResponse | null;
  setAuth: (payload: { accessToken: string; refreshToken: string; user?: MeResponse | null }) => void;
  setUser: (user: MeResponse | null) => void;
  logoutLocal: () => void;
  hasAnyRole: (roles: string[]) => boolean;
  canManageOrg: () => boolean;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  isAuthenticated: Boolean(getAccessToken()),
  user: null,
  setAuth: ({ accessToken, refreshToken, user }) => { setTokens(accessToken, refreshToken); set({ isAuthenticated: true, user: user ?? null }); },
  setUser: (user) => set({ user, isAuthenticated: Boolean(getAccessToken()) && user != null }),
  logoutLocal: () => { clearTokens(); set({ isAuthenticated: false, user: null }); },
  hasAnyRole: (roles) => { const user = get().user; return !!user && roles.some((role) => user.roles.includes(role)); },
  canManageOrg: () => { const user = get().user; return !!user && ['ROLE_ADMIN','ROLE_HR_MANAGER','ROLE_SYS_ADMIN'].some((r) => user.roles.includes(r)); },
}));
