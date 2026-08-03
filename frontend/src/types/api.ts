export type ApiResponse<T> = {
  success: boolean
  message: string
  data: T
  timestamp?: string
}

export type ErrorResponse = {
  success: false
  message: string
  status: number
  path?: string
  errors?: string[]
}

export type Role = 'USER' | 'ADMIN'

export type LoginResponse = {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
}

export type RegisterResponse = {
  id: string
  firstName: string
  lastName: string
  email: string
  role: Role
}

export type UserResponse = {
  id: string
  firstName: string
  lastName: string
  email: string
  role: Role
  emailVerified: boolean
  enabled: boolean
  accountNonLocked: boolean
}

export type DocumentResponse = {
  id: string
  title: string
  fileName: string
  contentType: string
  sizeBytes: number
  chunkCount: number
  status: 'UPLOADED' | 'PROCESSING' | 'INDEXED' | 'FAILED'
}

export type ChatAskResponse = {
  messageId: string
  sessionId: string
  question: string
  answer: string
  sources: string[]
}
