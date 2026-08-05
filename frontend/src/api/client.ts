import type { ApiResponse, ErrorResponse } from '../types/api'

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? ''

type RequestOptions = {
  method?: string
  body?: unknown
  token?: string | null
  formData?: FormData
}

export class ApiError extends Error {
  status: number
  details?: string[]

  constructor(message: string, status: number, details?: string[]) {
    super(message)
    this.status = status
    this.details = details
  }
}

export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers: Record<string, string> = {}
  if (options.token) {
    headers.Authorization = `Bearer ${options.token}`
  }

  let body: BodyInit | undefined
  if (options.formData) {
    body = options.formData
  } else if (options.body !== undefined) {
    headers['Content-Type'] = 'application/json'
    body = JSON.stringify(options.body)
  }

  const response = await fetch(`${API_BASE}${path}`, {
    method: options.method ?? 'GET',
    headers,
    body,
  })

  const text = await response.text()
  const payload = text ? (JSON.parse(text) as ApiResponse<T> | ErrorResponse) : null

  if (!response.ok) {
    const err = payload as ErrorResponse | null
    throw new ApiError(err?.message ?? 'Request failed', response.status, err?.errors)
  }

  if (payload && 'data' in payload) {
    return (payload as ApiResponse<T>).data
  }
  return payload as T
}
