import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { getMe, logout } from '../api/authApi';
import { useAuthStore } from '../features/auth/authStore';
import { getRefreshToken } from '../utils/tokenStorage';
import { toastInfo } from '../utils/toast';

const menuItems = [
  { to: '/dashboard', label: '대시보드', roles: ['ROLE_ADMIN','ROLE_HR_MANAGER','ROLE_SYS_ADMIN','ROLE_TEAM_MANAGER','ROLE_EMPLOYEE'] },
  { to: '/employees', label: '직원 관리', roles: ['ROLE_ADMIN','ROLE_HR_MANAGER','ROLE_SYS_ADMIN','ROLE_TEAM_MANAGER'] },
  { to: '/departments', label: '부서 관리', roles: ['ROLE_ADMIN','ROLE_HR_MANAGER','ROLE_SYS_ADMIN','ROLE_TEAM_MANAGER'] },
  { to: '/attendance', label: '근태 관리', roles: ['ROLE_ADMIN','ROLE_HR_MANAGER','ROLE_SYS_ADMIN','ROLE_TEAM_MANAGER'] },
  { to: '/leave', label: '휴가 관리', roles: ['ROLE_ADMIN','ROLE_HR_MANAGER','ROLE_SYS_ADMIN','ROLE_TEAM_MANAGER','ROLE_EMPLOYEE'] },
  { to: '/users', label: '사용자/권한', roles: ['ROLE_ADMIN','ROLE_HR_MANAGER','ROLE_SYS_ADMIN'] },
  { to: '/codes', label: '코드 관리', roles: ['ROLE_ADMIN','ROLE_HR_MANAGER','ROLE_SYS_ADMIN'] },
  { to: '/audit-logs', label: '감사 로그', roles: ['ROLE_ADMIN','ROLE_HR_MANAGER','ROLE_SYS_ADMIN'] },
];

export function AppShell() {
  const navigate = useNavigate();
  const user = useAuthStore((state) => state.user);
  const setUser = useAuthStore((state) => state.setUser);
  const logoutLocal = useAuthStore((state) => state.logoutLocal);
  const hasAnyRole = useAuthStore((state) => state.hasAnyRole);

  useEffect(() => {
    if (user) return;
    getMe().then((me) => setUser(me)).catch(() => { logoutLocal(); navigate('/login'); });
  }, [logoutLocal, navigate, setUser, user]);

  const handleLogout = async () => {
    try { const refreshToken = getRefreshToken(); if (refreshToken) await logout(refreshToken); toastInfo('로그아웃되었습니다.'); }
    finally { logoutLocal(); navigate('/login'); }
  };

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand"><Link to="/dashboard">HiBizNet HR</Link></div>
        <nav className="nav-menu">
          {menuItems.filter((item) => hasAnyRole(item.roles)).map((item) => <NavLink key={item.to} to={item.to}>{item.label}</NavLink>)}
        </nav>
      </aside>
      <div className="content-area">
        <header className="topbar">
          <div>
            <strong>{user?.displayName ?? user?.username ?? '사용자'}</strong>
            <div className="muted small-text">{user?.email ?? ''} · {user?.roles?.join(', ') ?? ''}</div>
          </div>
          <button type="button" className="secondary-button" onClick={handleLogout}>로그아웃</button>
        </header>
        <main className="page-container"><Outlet /></main>
      </div>
    </div>
  );
}
