import { createBrowserRouter, Navigate } from 'react-router-dom';
import { AppShell } from '../components/AppShell';
import { ProtectedRoute } from '../components/ProtectedRoute';
import { DashboardPage } from '../pages/DashboardPage';
import { DepartmentsPage } from '../pages/DepartmentsPage';
import { EmployeesPage } from '../pages/EmployeesPage';
import { AuditLogsPage } from '../pages/AuditLogsPage';
import { AttendancePage } from '../pages/AttendancePage';
import { LeavePage } from '../pages/LeavePage';
import { UserAdminPage } from '../pages/UserAdminPage';
import { CodeManagementPage } from '../pages/CodeManagementPage';
import { LoginPage } from '../pages/LoginPage';
import { NotFoundPage } from '../pages/NotFoundPage';

export const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  { path: '/', element: <ProtectedRoute><AppShell /></ProtectedRoute>, children: [
    { index: true, element: <Navigate to="/dashboard" replace /> },
    { path: 'dashboard', element: <DashboardPage /> },
    { path: 'employees', element: <EmployeesPage /> },
    { path: 'departments', element: <DepartmentsPage /> },
    { path: 'attendance', element: <AttendancePage /> },
    { path: 'leave', element: <LeavePage /> },
    { path: 'users', element: <UserAdminPage /> },
    { path: 'codes', element: <CodeManagementPage /> },
    { path: 'audit-logs', element: <AuditLogsPage /> },
  ]},
  { path: '*', element: <NotFoundPage /> },
]);
