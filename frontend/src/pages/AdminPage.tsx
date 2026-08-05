import { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { adminApi } from '../api/services'
import { ApiError } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import type { Role, UserResponse } from '../types/api'

export function AdminPage() {
  const { user, accessToken } = useAuth()
  const [users, setUsers] = useState<UserResponse[]>([])
  const [error, setError] = useState<string | null>(null)
  const [ok, setOk] = useState<string | null>(null)

  useEffect(() => {
    if (!accessToken || user?.role !== 'ADMIN') return
    void adminApi
      .listUsers(accessToken)
      .then(setUsers)
      .catch((err) => setError(err instanceof ApiError ? err.message : 'Failed to load users'))
  }, [accessToken, user?.role])

  if (user?.role !== 'ADMIN') return <Navigate to="/" replace />

  async function changeRole(userId: string, role: Role) {
    if (!accessToken) return
    setError(null)
    try {
      const updated = await adminApi.updateRole(accessToken, userId, role)
      setUsers((prev) => prev.map((item) => (item.id === userId ? updated : item)))
      setOk(`Updated role for ${updated.email}`)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Role update failed')
    }
  }

  async function toggleStatus(target: UserResponse) {
    if (!accessToken) return
    setError(null)
    try {
      const updated = await adminApi.updateStatus(
        accessToken,
        target.id,
        !target.enabled,
        target.accountNonLocked,
      )
      setUsers((prev) => prev.map((item) => (item.id === target.id ? updated : item)))
      setOk(`Updated status for ${updated.email}`)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Status update failed')
    }
  }

  return (
    <div className="stack">
      <div className="page-head">
        <div>
          <h1>Admin</h1>
          <p>Manage roles and account status.</p>
        </div>
      </div>

      <div className="panel panel-pad">
        {error && <div className="alert alert-error">{error}</div>}
        {ok && <div className="alert alert-ok">{ok}</div>}

        <table className="table">
          <thead>
            <tr>
              <th>User</th>
              <th>Role</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((item) => (
              <tr key={item.id}>
                <td>
                  <div style={{ fontWeight: 700 }}>
                    {item.firstName} {item.lastName}
                  </div>
                  <div className="muted" style={{ fontSize: '0.82rem' }}>
                    {item.email}
                  </div>
                </td>
                <td>
                  <select
                    value={item.role}
                    onChange={(e) => void changeRole(item.id, e.target.value as Role)}
                  >
                    <option value="USER">USER</option>
                    <option value="ADMIN">ADMIN</option>
                  </select>
                </td>
                <td>
                  <span className={item.enabled ? 'badge badge-ok' : 'badge badge-bad'}>
                    {item.enabled ? 'enabled' : 'disabled'}
                  </span>
                </td>
                <td>
                  <button className="btn btn-ghost" type="button" onClick={() => void toggleStatus(item)}>
                    {item.enabled ? 'Disable' : 'Enable'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
