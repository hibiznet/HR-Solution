export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthTokenResponse {
  accessToken: string;
  refreshToken: string;
  accessTokenExpiresIn: number;
  refreshTokenExpiresIn: number;
}

export interface MeResponse {
  userId: number;
  employeeId?: number | null;
  departmentId?: number | null;
  username: string;
  displayName: string;
  email: string;
  roles: string[];
}
