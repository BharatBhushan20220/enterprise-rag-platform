import { apiRequest } from './client'
import type {
  ChatAskResponse,
  DocumentResponse,
  LoginResponse,
  RegisterResponse,
  Role,
  UserResponse,
} from '../types/api'

export const authApi = {
  register: (body: {
    firstName: string
    lastName: string
    email: string
    password: string
  }) => apiRequest<RegisterResponse>('/api/v1/auth/register', { method: 'POST', body }),

  login: (body: { email: string; password: string }) =>
    apiRequest<LoginResponse>('/api/v1/auth/login', { method: 'POST', body }),

  refresh: (refreshToken: string) =>
    apiRequest<LoginResponse>('/api/v1/auth/refresh', {
      method: 'POST',
      body: { refreshToken },
    }),

  logout: (accessToken: string, refreshToken?: string) =>
    apiRequest<null>('/api/v1/auth/logout', {
      method: 'POST',
      token: accessToken,
      body: { refreshToken },
    }),

  me: (token: string) => apiRequest<UserResponse>('/api/v1/auth/me', { token }),

  forgotPassword: (email: string) =>
    apiRequest<null>('/api/v1/auth/forgot-password', {
      method: 'POST',
      body: { email },
    }),

  resetPassword: (token: string, newPassword: string) =>
    apiRequest<null>('/api/v1/auth/reset-password', {
      method: 'POST',
      body: { token, newPassword },
    }),
}

export const documentApi = {
  list: (token: string) => apiRequest<DocumentResponse[]>('/api/v1/documents', { token }),

  upload: (token: string, file: File, title?: string) => {
    const formData = new FormData()
    formData.append('file', file)
    if (title) formData.append('title', title)
    return apiRequest<DocumentResponse>('/api/v1/documents/upload', {
      method: 'POST',
      token,
      formData,
    })
  },
}

export const chatApi = {
  ask: (token: string, sessionId: string, question: string) =>
    apiRequest<ChatAskResponse>('/api/v1/chat/ask', {
      method: 'POST',
      token,
      body: { sessionId, question },
    }),

  history: (token: string, sessionId: string) =>
    apiRequest<ChatAskResponse[]>(`/api/v1/chat/sessions/${sessionId}`, { token }),
}

export const adminApi = {
  listUsers: (token: string) => apiRequest<UserResponse[]>('/api/v1/admin/users', { token }),

  updateRole: (token: string, userId: string, role: Role) =>
    apiRequest<UserResponse>(`/api/v1/admin/users/${userId}/role`, {
      method: 'PATCH',
      token,
      body: { role },
    }),

  updateStatus: (
    token: string,
    userId: string,
    enabled: boolean,
    accountNonLocked: boolean,
  ) =>
    apiRequest<UserResponse>(`/api/v1/admin/users/${userId}/status`, {
      method: 'PATCH',
      token,
      body: { enabled, accountNonLocked },
    }),
}
