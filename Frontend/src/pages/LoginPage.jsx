import { useState, useEffect } from "react";
import { login } from "../api/authService";
import ErrorAlert from "../components/alerts/ErrorAlert";
import SuccessAlert from "../components/alerts/SuccessAlert";
import { Link } from "react-router-dom";

export default function LoginPage() {
  const [correo, setCorreo] = useState("");
  const [contrasena, setContrasena] = useState("");
  const [error, setError] = useState("");
  const [ok, setOk] = useState("");

  useEffect(() => {
    if (!error && !ok) return;
    const t = setTimeout(() => {
      setError("");
      setOk("");
    }, 7000);
    return () => clearTimeout(t);
  }, [error, ok]);

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setOk("");
    try {
      const data = await login(correo.trim().toLowerCase(), contrasena);
      localStorage.setItem("accessToken", data.accessToken);
      setOk("Inicio de sesión exitoso.");
      setTimeout(() => (window.location.href = "/"), 800);
    } catch (err) {
      console.error("LOGIN ERROR =>", {
        message: err?.message,
        status: err?.response?.status,
        data: err?.response?.data,
        url: err?.config?.baseURL + err?.config?.url,
      });
      const msg = err?.response?.data?.message;
      setError(
        msg === "CREDENCIALES_INVALIDAS"
          ? "Credenciales inválidas."
          : "Error al iniciar sesión."
      );
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-slate-950 via-slate-900 to-slate-950 text-slate-100 flex items-center justify-center px-4">
      <form onSubmit={onSubmit} className="w-full max-w-md relative">
        <div className="absolute -inset-0.5 rounded-2xl bg-gradient-to-r from-emerald-500/20 via-cyan-500/20 to-fuchsia-500/20 blur-lg" />
        <div className="relative rounded-2xl bg-slate-900/70 backdrop-blur-xl ring-1 ring-slate-800 shadow-2xl p-6 sm:p-8">
          <div className="mb-6 text-center">
            <div className="mx-auto mb-3 h-12 w-12 rounded-xl bg-slate-800/80 ring-1 ring-slate-700 grid place-content-center">
              <svg viewBox="0 0 24 24" className="h-6 w-6 text-slate-300">
                <path
                  fill="currentColor"
                  d="M12 12c2.761 0 5-2.686 5-6s-2.239-6-5-6-5 2.686-5 6 2.239 6 5 6zm0 2c-4.418 0-8 2.91-8 6.5V22h16v-1.5C20 16.91 16.418 14 12 14z"
                />
              </svg>
            </div>
            <h1 className="text-xl sm:text-2xl font-semibold tracking-tight">
              Iniciar sesión
            </h1>
            <p className="text-sm text-slate-400 mt-1">
              Accede al panel de inversión
            </p>
          </div>
          <ErrorAlert message={error} onClose={() => setError("")} />
          <SuccessAlert message={ok} onClose={() => setOk("")} />
          <label className="block text-sm mb-1.5 text-slate-300">Correo</label>
          <div className="mb-4 flex items-center gap-2 rounded-xl bg-slate-950/60 ring-1 ring-slate-800 focus-within:ring-emerald-400/70 focus-within:bg-slate-900/70 transition">
            <span className="pl-3">
              <svg viewBox="0 0 24 24" className="h-5 w-5 text-slate-400">
                <path
                  fill="currentColor"
                  d="M20 4H4a2 2 0 0 0-2 2v.4l10 6.25L22 6.4V6a2 2 0 0 0-2-2Zm0 4.5-8 5-8-5V18a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8.5Z"
                />
              </svg>
            </span>
            <input
              type="email"
              placeholder="tu@email.com"
              className="w-full bg-transparent outline-none placeholder:text-slate-500 text-slate-100 py-3 pr-3"
              value={correo}
              onChange={(e) => setCorreo(e.target.value)}
              required
            />
          </div>
          <label className="block text-sm mb-1.5 text-slate-300">
            Contraseña
          </label>
          <div className="mb-6 flex items-center gap-2 rounded-xl bg-slate-950/60 ring-1 ring-slate-800 focus-within:ring-emerald-400/70 focus-within:bg-slate-900/70 transition">
            <span className="pl-3">
              <svg viewBox="0 0 24 24" className="h-5 w-5 text-slate-400">
                <path
                  fill="currentColor"
                  d="M12 1a5 5 0 0 0-5 5v3H6a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8a2 2 0 0 0-2-2h-1V6a5 5 0 0 0-5-5Zm-3 8V6a3 3 0 0 1 6 0v3H9Z"
                />
              </svg>
            </span>
            <input
              type="password"
              placeholder="••••••••"
              className="w-full bg-transparent outline-none placeholder:text-slate-500 text-slate-100 py-3 pr-3"
              value={contrasena}
              onChange={(e) => setContrasena(e.target.value)}
              required
            />
          </div>
          <button
            type="submit"
            className="w-full rounded-xl py-3 font-semibold text-slate-900
                       bg-gradient-to-r from-emerald-400 to-emerald-300
                       hover:from-emerald-300 hover:to-emerald-200
                       shadow-[0_0_0_1px_rgba(16,185,129,0.4),0_10px_20px_-10px_rgba(16,185,129,0.6)]
                       transition"
          >
            Iniciar sesión
          </button>
          <div className="mt-5 text-center space-y-3">
            <a
              className="block text-sm text-cyan-300/90 hover:text-cyan-200 hover:underline"
              href="/recuperar-password"
            >
              ¿Olvidaste tu contraseña?
            </a>

            <Link
              to="/registro-inversionista"
              className="inline-flex items-center justify-center gap-2 px-4 py-2 rounded-lg
                         ring-1 ring-slate-700 text-slate-200 hover:bg-slate-800/60
                         transition"
            >
              Registrarme
            </Link>
            <div className="mt-4 h-1 w-full bg-gradient-to-r from-emerald-500/70 via-slate-700 to-rose-500/70 rounded-full" />
          </div>
        </div>
      </form>
    </div>
  );
}
