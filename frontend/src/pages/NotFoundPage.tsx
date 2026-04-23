import { Link } from 'react-router-dom';

export function NotFoundPage() {
  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>페이지를 찾을 수 없습니다.</h1>
        <Link className="primary-button link-button" to="/dashboard">대시보드로 이동</Link>
      </div>
    </div>
  );
}
