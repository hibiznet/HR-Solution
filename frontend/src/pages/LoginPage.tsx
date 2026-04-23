import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { getMe, login } from '../api/authApi';
import { useAuthStore } from '../features/auth/authStore';
import type { LoginRequest } from '../types/auth';

export function LoginPage() {
  const navigate = useNavigate();
  const setAuth = useAuthStore((state) => state.setAuth);
  const setUser = useAuthStore((state) => state.setUser);
  const [errorMessage, setErrorMessage] = useState('');
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginRequest>({ defaultValues: { username: 'admin', password: 'admin1234!' } });

  const onSubmit = async (values: LoginRequest) => {
    try {
      setErrorMessage('');
      const tokenResponse = await login(values);
      setAuth({ accessToken: tokenResponse.accessToken, refreshToken: tokenResponse.refreshToken, user: null });
      const me = await getMe();
      setUser(me);
      navigate('/dashboard');
    } catch (e) {
      console.error(e);
      setErrorMessage('로그인에 실패했습니다. 계정 정보를 확인하세요.');
    }
  };

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit(onSubmit)}>
        <h1>HiBizNet HR</h1>
        <p className="muted">JWT + Redis Refresh Token 백엔드와 연결되는 관리자 포털입니다.</p>
        <label>
          <span>아이디</span>
          <input {...register('username', { required: '아이디는 필수입니다.' })} placeholder="admin" />
          {errors.username ? <small className="error-text">{errors.username.message}</small> : null}
        </label>
        <label>
          <span>비밀번호</span>
          <input type="password" {...register('password', { required: '비밀번호는 필수입니다.' })} placeholder="••••••••" />
          {errors.password ? <small className="error-text">{errors.password.message}</small> : null}
        </label>
        {errorMessage ? <div className="error-banner">{errorMessage}</div> : null}
        <button className="primary-button" type="submit" disabled={isSubmitting}>{isSubmitting ? '로그인 중...' : '로그인'}</button>
      </form>
    </div>
  );
}
