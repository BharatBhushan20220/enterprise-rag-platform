import { type DragEvent, type FormEvent, useCallback, useEffect, useState } from 'react'
import { documentApi } from '../api/services'
import { ApiError } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import type { DocumentResponse } from '../types/api'

function statusClass(status: DocumentResponse['status']) {
  if (status === 'INDEXED') return 'badge badge-ok'
  if (status === 'FAILED') return 'badge badge-bad'
  return 'badge badge-warn'
}

export function DocumentsPage() {
  const { accessToken } = useAuth()
  const [docs, setDocs] = useState<DocumentResponse[]>([])
  const [title, setTitle] = useState('')
  const [file, setFile] = useState<File | null>(null)
  const [dragging, setDragging] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [ok, setOk] = useState<string | null>(null)
  const [busy, setBusy] = useState(false)

  const load = useCallback(async () => {
    if (!accessToken) return
    const list = await documentApi.list(accessToken)
    setDocs(list)
  }, [accessToken])

  useEffect(() => {
    void load().catch((err) => {
      setError(err instanceof ApiError ? err.message : 'Failed to load documents')
    })
  }, [load])

  function onDrop(event: DragEvent) {
    event.preventDefault()
    setDragging(false)
    const next = event.dataTransfer.files?.[0]
    if (next) setFile(next)
  }

  async function onUpload(event: FormEvent) {
    event.preventDefault()
    if (!accessToken || !file) return
    setBusy(true)
    setError(null)
    setOk(null)
    try {
      const uploaded = await documentApi.upload(accessToken, file, title || undefined)
      setOk(`Indexed "${uploaded.title}" with ${uploaded.chunkCount} chunks`)
      setFile(null)
      setTitle('')
      await load()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Upload failed')
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="stack">
      <div className="page-head">
        <div>
          <h1>Documents</h1>
          <p>Upload PDF or text files to extract, chunk, embed, and index.</p>
        </div>
      </div>

      <div className="grid-2">
        <form className="panel panel-pad stack" onSubmit={onUpload}>
          <div className="field">
            <label htmlFor="title">Title (optional)</label>
            <input id="title" value={title} onChange={(e) => setTitle(e.target.value)} />
          </div>

          <div
            className={`dropzone ${dragging ? 'dragging' : ''}`}
            onDragOver={(e) => {
              e.preventDefault()
              setDragging(true)
            }}
            onDragLeave={() => setDragging(false)}
            onDrop={onDrop}
          >
            <div style={{ fontWeight: 700, marginBottom: '0.35rem' }}>
              {file ? file.name : 'Drop a PDF or .txt file'}
            </div>
            <div className="muted" style={{ marginBottom: '0.8rem' }}>
              or choose from disk
            </div>
            <input
              type="file"
              accept=".pdf,.txt,.md,text/plain,application/pdf"
              onChange={(e) => setFile(e.target.files?.[0] ?? null)}
            />
          </div>

          {error && <div className="alert alert-error">{error}</div>}
          {ok && <div className="alert alert-ok">{ok}</div>}

          <button className="btn btn-primary" type="submit" disabled={!file || busy}>
            {busy ? 'Processing…' : 'Upload & index'}
          </button>
        </form>

        <div className="panel panel-pad">
          <h2 style={{ marginTop: 0, fontFamily: 'var(--font-display)' }}>Library</h2>
          {docs.length === 0 ? (
            <div className="muted">No documents indexed yet.</div>
          ) : (
            <table className="table">
              <thead>
                <tr>
                  <th>Title</th>
                  <th>Status</th>
                  <th>Chunks</th>
                </tr>
              </thead>
              <tbody>
                {docs.map((doc) => (
                  <tr key={doc.id}>
                    <td>
                      <div style={{ fontWeight: 700 }}>{doc.title}</div>
                      <div className="muted" style={{ fontSize: '0.8rem' }}>
                        {doc.fileName}
                      </div>
                    </td>
                    <td>
                      <span className={statusClass(doc.status)}>{doc.status}</span>
                    </td>
                    <td>{doc.chunkCount}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  )
}
