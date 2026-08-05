import { type FormEvent, useState } from 'react'
import { Link } from 'react-router-dom'
import { authApi } from '../api/services'
import { ApiError } from '../api/client'

export function ForgotPasswordPage() {
  const [email, setEmail] = useState('')
  const [message, setMessage] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [busy, setBusy] = useState(false)

  async function onSubmit(event: FormEvent) {
    event.preventDefault()
    setBusy(true)
    setError(null)
    setMessage(null)
    try {
      await authApi.forgotPassword(email)
      setMessage('If that email exists, a reset token was issued. Check auth-service logs in local mode.')
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Request failed')
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="auth-screen">
      <form className="panel panel-pad stack" style={{ width: 'min(420px, 100%)' }} onSubmit={onSubmit}>
        <h1 style={{ margin: 0, fontFamily: 'var(--font-display)' }}>Reset access</h1>
        <p className="muted" style={{ margin: 0 }}>
          Enter your account email to start password reset.
        </p>
        <div className="field">
          <label htmlFor="email">Email</label>
          <input
            id="email"
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>
        {error && <div className="alert alert-error">{error}</div>}
        {message && <div className="alert alert-ok">{message}</div>}
        <button className="btn btn-primary" type="submit" disabled={busy}>
          {busy ? 'Sending…' : 'Send reset'}
        </button>
        <Link to="/login" className="muted">
          Back to sign in
        </Link>
        <Link to="/reset-password" className="muted">
          I already have a reset token
        </Link>
      </form>
    </div>
  )
}
