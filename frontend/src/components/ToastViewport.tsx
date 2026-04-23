import { useToastStore } from '../stores/toastStore';

export function ToastViewport() {
  const items = useToastStore((state) => state.items);
  const remove = useToastStore((state) => state.remove);

  return (
    <div className="toast-viewport">
      {items.map((item) => (
        <button key={item.id} type="button" className={`toast ${item.type}`} onClick={() => remove(item.id)}>
          {item.message}
        </button>
      ))}
    </div>
  );
}
