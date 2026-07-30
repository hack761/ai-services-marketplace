(() => {
  const $ = (selector) => document.querySelector(selector);
  const state = { services: [], category: '', query: '', mode: 'login' };
  // Same-origin by default: when served by Spring Boot there is no CORS configuration to manage.
  const API = window.API_BASE_URL || window.location.origin;
  const grid = $('#serviceGrid'), empty = $('#emptyState'), notice = $('#connectionNotice');

  function money(value) { return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(value || 0); }
  function escapeHtml(value = '') { const node = document.createElement('span'); node.textContent = value; return node.innerHTML; }
  function setStatus(online) { const el = $('#apiStatus'); el.classList.toggle('online', online); el.innerHTML = `<i></i>${online ? 'Live API' : 'Offline'}`; }
  async function request(path, options = {}) {
    const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
    const token = localStorage.getItem('nexora_token'); if (token) headers.Authorization = `Bearer ${token}`;
    let response;
    try { response = await fetch(`${API}${path}`, { ...options, headers }); } catch { throw new Error('CONNECTION'); }
    const raw = await response.text(); let body; try { body = raw ? JSON.parse(raw) : null; } catch { body = raw; }
    if (!response.ok) { const message = typeof body === 'object' ? Object.values(body).join(' ') : body; throw new Error(message || 'Something went wrong.'); }
    return body;
  }
  function render() {
    const q = state.query.trim().toLowerCase();
    const list = state.services.filter(s => (!state.category || s.category === state.category) && (!q || `${s.title} ${s.description} ${s.category}`.toLowerCase().includes(q)));
    grid.hidden = list.length === 0; empty.hidden = list.length !== 0;
    grid.innerHTML = list.map((s, i) => `<article class="service-card"><div class="service-card-top"><span class="service-category">${escapeHtml(s.category)}</span><span class="service-number">0${i + 1}</span></div><h3>${escapeHtml(s.title)}</h3><p>${escapeHtml(s.description)}</p><div class="service-card-footer"><span class="seller-name">by ${escapeHtml(s.sellerName || 'Nexora specialist')}</span><strong class="price">${money(s.price)}</strong></div></article>`).join('');
  }
  async function loadServices() {
    grid.hidden = false; grid.innerHTML = '<div class="loading-card">Loading available services…</div>'; empty.hidden = true;
    try { state.services = await request('/api/buyer/services'); setStatus(true); notice.hidden = true; render(); }
    catch (error) { state.services = []; grid.innerHTML = ''; grid.hidden = true; empty.hidden = true; setStatus(false); notice.hidden = false; }
  }
  function toast(message) { const el = $('#toast'); el.textContent = message; el.hidden = false; clearTimeout(toast.timer); toast.timer = setTimeout(() => el.hidden = true, 3400); }
  function openModal(mode = 'login', role = '') { state.mode = mode; $('#authModal').hidden = false; const register = mode === 'register'; $('#modalKicker').textContent = register ? 'JOIN THE NETWORK' : 'WELCOME BACK'; $('#modalTitle').textContent = register ? 'Make your move.' : 'Log in to Nexora'; $('#modalLead').textContent = register ? 'A sharper way to find or offer exceptional AI work.' : 'Pick up where your best work left off.'; document.querySelector('.register-only').hidden = !register; $('#authSubmit').innerHTML = `${register ? 'Create account' : 'Log in'} <span>→</span>`; $('#modalSwitch').innerHTML = register ? 'Already a member? <button id="switchMode">Log in</button>' : 'New to Nexora? <button id="switchMode">Create an account</button>'; if (role) $('#role').value = role; $('#formError').hidden = true; setTimeout(() => $('#email').focus(), 30); }
  $('#loginButton').onclick = () => openModal('login'); $('#signupButton').onclick = () => openModal('register'); $('#openSeller').onclick = () => openModal('register', 'SELLER'); $('#sellerCta').onclick = () => openModal('register', 'SELLER'); $('#closeModal').onclick = () => $('#authModal').hidden = true;
  $('#authModal').onclick = e => { if (e.target === $('#authModal')) $('#authModal').hidden = true; };
  document.addEventListener('click', e => { if (e.target.id === 'switchMode') openModal(state.mode === 'login' ? 'register' : 'login'); });
  $('#authForm').onsubmit = async e => { e.preventDefault(); const data = Object.fromEntries(new FormData(e.currentTarget)); const error = $('#formError'), submit = $('#authSubmit'); error.hidden = true; submit.disabled = true; submit.textContent = 'Please wait…'; try { if (state.mode === 'register') { await request('/api/register', { method: 'POST', body: JSON.stringify(data) }); toast('Account created — you can log in now.'); openModal('login', data.role); } else { const result = await request('/api/login', { method: 'POST', body: JSON.stringify(data) }); localStorage.setItem('nexora_token', result.token); localStorage.setItem('nexora_role', result.role); $('#authModal').hidden = true; toast(`Welcome back. You are signed in as ${String(result.role).toLowerCase()}.`); } } catch (err) { error.textContent = err.message === 'CONNECTION' ? 'Unable to reach the API. Start the backend and try again.' : err.message; error.hidden = false; } finally { submit.disabled = false; submit.innerHTML = `${state.mode === 'register' ? 'Create account' : 'Log in'} <span>→</span>`; } };
  $('#searchInput').oninput = e => { state.query = e.target.value; render(); };
  $('#categoryPills').onclick = e => { const button = e.target.closest('.pill'); if (!button) return; state.category = button.dataset.category; document.querySelectorAll('.pill').forEach(p => p.classList.toggle('active', p === button)); render(); };
  $('#resetFilters').onclick = () => { state.query = ''; state.category = ''; $('#searchInput').value = ''; document.querySelectorAll('.pill').forEach((p, i) => p.classList.toggle('active', i === 0)); render(); };
  $('#retryButton').onclick = loadServices; $('#filterToggle').onclick = () => $('#searchInput').focus();
  loadServices();
})();
