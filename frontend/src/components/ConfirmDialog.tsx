interface ConfirmDialogProps {
  open: boolean;
  title: string;
  description: string;
  confirmLabel?: string;
  onClose: () => void;
  onConfirm: () => void;
}

export function ConfirmDialog({ open, title, description, confirmLabel = '삭제', onClose, onConfirm }: ConfirmDialogProps) {
  if (!open) return null;
  return (
    <div className="modal-overlay">
      <div className="modal-card small">
        <h2>{title}</h2>
        <p className="muted">{description}</p>
        <div className="form-actions">
          <button type="button" className="secondary-button" onClick={onClose}>취소</button>
          <button type="button" className="danger-button" onClick={onConfirm}>{confirmLabel}</button>
        </div>
      </div>
    </div>
  );
}
