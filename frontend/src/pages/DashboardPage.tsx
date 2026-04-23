import { useQuery } from '@tanstack/react-query';
import { fetchDepartments } from '../api/departmentApi';
import { fetchEmployees } from '../api/employeeApi';
import { fetchAttendance } from '../api/attendanceApi';
import { PageHeader } from '../components/PageHeader';
import { LoadingSpinner } from '../components/LoadingSpinner';

const today = new Date().toISOString().slice(0, 10);

export function DashboardPage() {
  const employeeQuery = useQuery({ queryKey: ['dashboard', 'employees'], queryFn: () => fetchEmployees({ page: 0, size: 5, keyword: '', departmentId: '', statusCode: '', employmentTypeCode: '' }) });
  const departmentQuery = useQuery({ queryKey: ['dashboard', 'departments'], queryFn: () => fetchDepartments({ page: 0, size: 5, keyword: '', parentId: '', isActive: '' }) });
  const attendanceQuery = useQuery({ queryKey: ['dashboard', 'attendance'], queryFn: () => fetchAttendance({ page: 0, size: 5, keyword: '', employeeId: '', departmentId: '', attendanceStatusCode: '', isClosed: '', startDate: today, endDate: today }) });

  if (employeeQuery.isLoading || departmentQuery.isLoading || attendanceQuery.isLoading) {
    return <LoadingSpinner text="대시보드를 준비하는 중입니다." />;
  }

  return (
    <div>
      <PageHeader title="대시보드" description="직원/부서/근태 운영 현황과 관리자 기능 바로가기를 제공합니다." />
      <div className="stats-grid three-col">
        <section className="card stat-card">
          <span className="stat-label">직원 수</span>
          <strong className="stat-value">{employeeQuery.data?.totalElements ?? 0}</strong>
        </section>
        <section className="card stat-card">
          <span className="stat-label">부서 수</span>
          <strong className="stat-value">{departmentQuery.data?.totalElements ?? 0}</strong>
        </section>
        <section className="card stat-card">
          <span className="stat-label">금일 근태 기록</span>
          <strong className="stat-value">{attendanceQuery.data?.totalElements ?? 0}</strong>
        </section>
      </div>
      <div className="grid two-col">
        <section className="card">
          <h2>최근 직원</h2>
          <ul className="list-box">
            {employeeQuery.data?.content.map((employee) => (
              <li key={employee.id}><strong>{employee.name}</strong><span>{employee.departmentName} · {employee.statusCode}</span></li>
            ))}
          </ul>
        </section>
        <section className="card">
          <h2>금일 근태 현황</h2>
          <ul className="list-box">
            {attendanceQuery.data?.content.map((attendance) => (
              <li key={attendance.id}><strong>{attendance.employeeName}</strong><span>{attendance.attendanceStatusCode} · {attendance.totalWorkMinutes}분</span></li>
            ))}
          </ul>
        </section>
      </div>
    </div>
  );
}
