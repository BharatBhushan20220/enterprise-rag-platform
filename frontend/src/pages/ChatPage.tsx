import { type FormEvent, useEffect, useMemo, useState } from 'react'
import { chatApi } from '../api/services'
import { ApiError } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import type { ChatAskResponse } from '../types/api'

function newSessionId() {
  return crypto.randomUUID()
}

export function ChatPage() {
  const { accessToken } = useAuth()
  const [sessionId, setSessionId] = useState(() => localStorage.getItem('aether.session') ?? newSessionId())
  const [question, setQuestion] = useState('')
  const [messages, setMessages] = useState<ChatAskResponse[]>([])
  const [error, setError] = useState<string | null>(null)
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    localStorage.setItem('aether.session', sessionId)
  }, [sessionId])

  useEffect(() => {
    if (!accessToken) return
    const load = async () => {
      try {
        const history = await chatApi.history(accessToken, sessionId)
        setMessages(history)
      } catch {
        setMessages([])
      }
    }
    void load()
  }, [accessToken, sessionId])

  const canSend = useMemo(() => question.trim().length > 0 && !busy, [question, busy])

  async function onAsk(event: FormEvent) {
    event.preventDefault()
    if (!accessToken || !canSend) return
    setBusy(true)
    setError(null)
    try {
      const answer = await chatApi.ask(accessToken, sessionId, question.trim())
      setMessages((prev) => [...prev, answer])
      setQuestion('')
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Chat request failed')
    } finally {
      setBusy(false)
    }
  }

  function resetSession() {
    const id = newSessionId()
    setSessionId(id)
    setMessages([])
    setError(null)
  }

  return (
    <div className="stack">
      <div className="page-head">
        <div>
          <h1>Chat</h1>
          <p>Ask grounded questions against indexed knowledge.</p>
        </div>
        <button className="btn btn-ghost" type="button" onClick={resetSession}>
          New session
        </button>
      </div>

      <div className="panel panel-pad stack">
        <div className="muted" style={{ fontSize: '0.85rem' }}>
          Session: {sessionId}
        </div>

        <div className="chat-log">
          {messages.length === 0 && (
            <div className="muted">No messages yet. Upload documents, then ask a question.</div>
          )}
          {messages.map((msg) => (
            <div key={msg.messageId} className="stack" style={{ gap: '0.55rem' }}>
              <div className="bubble bubble-user">{msg.question}</div>
              <div className="bubble bubble-assistant">
                <div>{msg.answer}</div>
                {msg.sources?.length > 0 && (
                  <div className="sources">
                    <strong>Sources</strong>
                    <ul>
                      {msg.sources.map((source) => (
                        <li key={source.slice(0, 40)}>{source}</li>
                      ))}
                    </ul>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>

        {error && <div className="alert alert-error">{error}</div>}

        <form className="row" onSubmit={onAsk}>
          <div className="field" style={{ flex: 1, minWidth: '220px' }}>
            <label htmlFor="question">Question</label>
            <input
              id="question"
              value={question}
              onChange={(e) => setQuestion(e.target.value)}
              placeholder="What does our onboarding policy say about remote work?"
            />
          </div>
          <button className="btn btn-primary" type="submit" disabled={!canSend} style={{ marginTop: '1.45rem' }}>
            {busy ? 'Thinking…' : 'Ask'}
          </button>
        </form>
      </div>
    </div>
  )
}
