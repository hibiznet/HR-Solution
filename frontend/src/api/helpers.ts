import type { AxiosResponse } from 'axios';
import type { ApiEnvelope, PageResponse } from '../types/common';

export function unwrapData<T>(response: AxiosResponse<ApiEnvelope<T> | T>): T {
  const payload = response.data as ApiEnvelope<T> | T;
  if (typeof payload === 'object' && payload !== null && 'data' in payload) {
    return (payload as ApiEnvelope<T>).data;
  }
  return payload as T;
}

export function normalizePage<T>(data: unknown, page: number, size: number): PageResponse<T> {
  if (data && typeof data === 'object' && 'content' in (data as Record<string, unknown>)) {
    return data as PageResponse<T>;
  }
  const list = Array.isArray(data) ? (data as T[]) : [];
  return {
    content: list,
    page,
    size,
    totalElements: list.length,
    totalPages: 1,
  };
}
