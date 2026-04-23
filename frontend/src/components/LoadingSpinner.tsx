export function LoadingSpinner({ text = '불러오는 중입니다.' }: { text?: string }) {
  return <div className="loading-box">{text}</div>;
}
