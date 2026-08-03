import { type FormEvent, useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { ApiError } from '../api/client'

export function LoginPage() {
  const { user, login, register, loading } = useAuth()
  const navigate = useNavigate()
  const [mode, setMode] = useState<'login' | 'register'>('login')
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')

  if (!loading && user) return <Navigate to="/" replace />

  async function onSubmit(event: FormEvent) {
    event.preventDefault()
    setError(null)
    setSubmitting(true)
    try {
      if (mode === 'login') {
        await login(email, password)
      } else {
        await register({ firstName, lastName, email, password })
      }
      navigate('/')
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Unable to authenticate')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="auth-screen">
      <div className="panel auth-card">
        <section className="auth-hero">
          <div>
            <div className="brand">
              <div className="brand-mark">A</div>
              <div className="brand-name">Aether</div>
            </div>
            <h1>Ask your enterprise knowledge with confidence.</h1>
            <p>
              Upload documents, retrieve grounded context, and chat with your private knowledge
              base.
            </p>
          </div>
          <div className="muted">Gateway · Auth · Documents · Search · Chat</div>
        </section>

        <section className="auth-form">
          <div className="tabs">
            <button
              type="button"
              className={mode === 'login' ? 'active' : ''}
              onClick={() => setMode('login')}
            >
              Sign in
            </button>
            <button
              type="button"
              className={mode === 'register' ? 'active' : ''}
              onClick={() => setMode('register')}
            >
              Create account
            </button>
          </div>

          <form className="stack" onSubmit={onSubmit}>
            {mode === 'register' && (
              <>
                <div className="field">
                  <label htmlFor="firstName">First name</label>
                  <input
                    id="firstName"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                    required
                  />
                </div>
                <div className="field">
                  <label htmlFor="lastName">Last name</label>
                  <input
                    id="lastName"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                    required
                  />
                </div>
              </>
            )}

            <div className="field">
              <label htmlFor="email">Email</label>
              <input
                id="email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="field">
              <label htmlFor="password">Password</label>
              <input
                id="password"
                type="password"
                minLength={8}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            {error && <div className="alert alert-error">{error}</div>}

            <button className="btn btn-primary" type="submit" disabled={submitting}>
              {submitting ? 'Please wait…' : mode === 'login' ? 'Enter workspace' : 'Create account'}
            </button>

            <div className="muted" style={{ fontSize: '0.9rem' }}>
              <Link to="/forgot-password">Forgot password?</Link>
            </div>
          </form>
        </section>
      </div>
    </div>
  )
}
