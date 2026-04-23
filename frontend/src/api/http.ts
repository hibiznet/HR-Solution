import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { env } from '../app/env';
import { useAuthStore } from '../features/auth/authStore';
import { clearTokens, getAccessToken, getRefreshToken, setTokens } from '../utils/tokenStorage';
import { toastError } from '../utils/toast';
import type { ApiEnvelope } from '../types/common';
import type { AuthTokenResponse } from '../types/auth';

type ExtendedRequestConfig = InternalAxiosRequestConfig & { _retry?: boolean; silent?: boolean };

export const http = axios.create({
  baseURL: env.apiBaseUrl,
  withCredentials: false,
});

let isRefreshing = false;
let pendingQueue: Array<(token: string | null) => void> = [];

function processQueue(token: string | null) {
  pendingQueue.forEach((callback) => callback(token));
  pendingQueue = [];
}

function extractServerMessage(error: AxiosError): string {
  const data = error.response?.data as { message?: string; error?: string } | undefined;
  return data?.message || data?.error || '요청 처리 중 오류가 발생했습니다.';
}

http.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

http.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = (error.config || {}) as ExtendedRequestConfig;

    if (error.response?.status === 401 && !originalRequest._retry) {
      const refreshToken = getRefreshToken();
      if (!refreshToken) {
        clearTokens();
        useAuthStore.getState().logoutLocal();
        return Promise.reject(error);
      }

      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          pendingQueue.push((token) => {
            if (!token) {
              reject(error);
              return;
            }
            if (!originalRequest.headers) originalRequest.headers = {};
            originalRequest.headers.Authorization = `Bearer ${token}`;
            resolve(http(originalRequest));
          });
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;
      try {
        const response = await axios.post<ApiEnvelope<AuthTokenResponse>>(`${env.apiBaseUrl}/api/auth/refresh`, { refreshToken });
        const payload = response.data.data ?? (response.data as unknown as AuthTokenResponse);
        setTokens(payload.accessToken, payload.refreshToken);
        processQueue(payload.accessToken);
        if (!originalRequest.headers) originalRequest.headers = {};
        originalRequest.headers.Authorization = `Bearer ${payload.accessToken}`;
        return http(originalRequest);
      } catch (refreshError) {
        processQueue(null);
        clearTokens();
        useAuthStore.getState().logoutLocal();
        if (!originalRequest.silent) {
          toastError('세션이 만료되어 다시 로그인해야 합니다.');
        }
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    if (!originalRequest.silent && error.response?.status && error.response.status >= 400) {
      toastError(extractServerMessage(error));
    }
    return Promise.reject(error);
  },
);
