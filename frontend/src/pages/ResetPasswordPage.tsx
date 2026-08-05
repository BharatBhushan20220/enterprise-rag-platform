import { type FormEvent, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { authApi } from '../api/services'
import { ApiError } from '../api/client'

export function ResetPasswordPage() {
  const navigate = useNavigate()
  const [token, setToken] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [busy, setBusy] = useState(false)

  async function onSubmit(event: FormEvent) {
    event.preventDefault()
    setBusy(true)
    setError(null)
    try {
      await authApi.resetPassword(token, newPassword)
      navigate('/login')
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Reset failed')
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="auth-screen">
      <form className="panel panel-pad stack" style={{ width: 'min(420px, 100%)' }} onSubmit={onSubmit}>
        <h1 style={{ margin: 0, fontFamily: 'var(--font-display)' }}>Set new password</h1>
        <div className="field">
          <label htmlFor="token">Reset token</label>
          <input id="token" required value={token} onChange={(e) => setToken(e.target.value)} />
        </div>
        <div className="field">
          <label htmlFor="password">New password</label>
          <input
            id="password"
            type="password"
            minLength={8}
            required
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
          />
        </div>
        {error && <div className="alert alert-error">{error}</div>}
        <button className="btn btn-primary" type="submit" disabled={busy}>
          {busy ? 'Saving…' : 'Update password'}
        </button>
        <Link to="/login" className="muted">
          Back to sign in
        </Link>
      </form>
    </div>
  )
}
