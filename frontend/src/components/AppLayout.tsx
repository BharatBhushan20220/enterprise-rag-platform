import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

export function AppLayout() {
  const { user, logout } = useAuth()

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark">A</div>
          <div>
            <div className="brand-name">Aether</div>
            <div className="brand-sub">Knowledge Assistant</div>
          </div>
        </div>

        <nav className="nav">
          <NavLink to="/" end>
            Chat
          </NavLink>
          <NavLink to="/documents">Documents</NavLink>
          {user?.role === 'ADMIN' && <NavLink to="/admin">Admin</NavLink>}
        </nav>

        <div className="sidebar-foot">
          <div style={{ fontWeight: 700 }}>
            {user?.firstName} {user?.lastName}
          </div>
          <div className="muted" style={{ fontSize: '0.85rem', marginTop: '0.2rem' }}>
            {user?.email}
          </div>
          <div className="row" style={{ marginTop: '0.8rem' }}>
            <span className="badge">{user?.role}</span>
            <button className="btn btn-ghost" type="button" onClick={() => void logout()}>
              Logout
            </button>
          </div>
        </div>
      </aside>

      <main className="main">
        <Outlet />
      </main>
    </div>
  )
}
