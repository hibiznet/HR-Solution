import { useToastStore } from '../stores/toastStore';

export function toastSuccess(message: string): void {
  useToastStore.getState().push('success', message);
}

export function toastError(message: string): void {
  useToastStore.getState().push('error', message);
}

export function toastInfo(message: string): void {
  useToastStore.getState().push('info', message);
}
