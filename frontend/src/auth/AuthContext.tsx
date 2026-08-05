import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import { authApi } from '../api/services'
import { ApiError } from '../api/client'
import type { UserResponse } from '../types/api'

type AuthState = {
  user: UserResponse | null
  accessToken: string | null
  refreshToken: string | null
  loading: boolean
  login: (email: string, password: string) => Promise<void>
  register: (payload: {
    firstName: string
    lastName: string
    email: string
    password: string
  }) => Promise<void>
  logout: () => Promise<void>
  refreshSession: () => Promise<boolean>
}

const STORAGE_KEY = 'aether.auth'

type StoredAuth = {
  accessToken: string
  refreshToken: string
}

const AuthContext = createContext<AuthState | null>(null)

function readStored(): StoredAuth | null {
  const raw = localStorage.getItem(STORAGE_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as StoredAuth
  } catch {
    return null
  }
}

function writeStored(value: StoredAuth | null) {
  if (!value) {
    localStorage.removeItem(STORAGE_KEY)
    return
  }
  localStorage.setItem(STORAGE_KEY, JSON.stringify(value))
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserResponse | null>(null)
  const [accessToken, setAccessToken] = useState<string | null>(null)
  const [refreshToken, setRefreshToken] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  const hydrate = useCallback(async (tokens: StoredAuth) => {
    const me = await authApi.me(tokens.accessToken)
    setAccessToken(tokens.accessToken)
    setRefreshToken(tokens.refreshToken)
    setUser(me)
    writeStored(tokens)
  }, [])

  const refreshSession = useCallback(async () => {
    const stored = readStored()
    if (!stored?.refreshToken) return false
    try {
      const tokens = await authApi.refresh(stored.refreshToken)
      await hydrate({
        accessToken: tokens.accessToken,
        refreshToken: tokens.refreshToken,
      })
      return true
    } catch {
      writeStored(null)
      setUser(null)
      setAccessToken(null)
      setRefreshToken(null)
      return false
    }
  }, [hydrate])

  useEffect(() => {
    const boot = async () => {
      const stored = readStored()
      if (!stored) {
        setLoading(false)
        return
      }
      try {
        await hydrate(stored)
      } catch (error) {
        if (error instanceof ApiError && error.status === 401) {
          await refreshSession()
        } else {
          writeStored(null)
        }
      } finally {
        setLoading(false)
      }
    }
    void boot()
  }, [hydrate, refreshSession])

  const login = useCallback(
    async (email: string, password: string) => {
      const tokens = await authApi.login({ email, password })
      await hydrate({
        accessToken: tokens.accessToken,
        refreshToken: tokens.refreshToken,
      })
    },
    [hydrate],
  )

  const register = useCallback(
    async (payload: {
      firstName: string
      lastName: string
      email: string
      password: string
    }) => {
      await authApi.register(payload)
      await login(payload.email, payload.password)
    },
    [login],
  )

  const logout = useCallback(async () => {
    try {
      if (accessToken) {
        await authApi.logout(accessToken, refreshToken ?? undefined)
      }
    } catch {
      // ignore logout network errors
    } finally {
      writeStored(null)
      setUser(null)
      setAccessToken(null)
      setRefreshToken(null)
    }
  }, [accessToken, refreshToken])

  const value = useMemo(
    () => ({
      user,
      accessToken,
      refreshToken,
      loading,
      login,
      register,
      logout,
      refreshSession,
    }),
    [user, accessToken, refreshToken, loading, login, register, logout, refreshSession],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
