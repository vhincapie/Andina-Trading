import { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { restablecerPassword } from "../api/authService";
import ErrorAlert from "../components/alerts/ErrorAlert";
import SuccessAlert from "../components/alerts/SuccessAlert";

export default function ResetPasswordPage() {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const token = params.get("token") || "";

  const [nuevaContrasena, setNuevaContrasena] = useState("");
  const [confirmar, setConfirmar] = useState("");
  const [loading, setLoading] = useState(false);
  const [ok, setOk] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (!error && !ok) return;
    const t = setTimeout(() => {
      setError("");
      setOk("");
    }, 7000);
    return () => clearTimeout(t);
  }, [error, ok]);

  useEffect(() => {
    if (!token) setError("Enlace inválido. Falta el token.");
  }, [token]);

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setOk("");
    if (!token) return;
    if (nuevaContrasena !== confirmar) {
      setError("Las contraseñas no coinciden.");
      return;
    }
    setLoading(true);
    try {
      await restablecerPassword(token, nuevaContrasena);
      setOk("Contraseña restablecida correctamente.");
      setTimeout(() => navigate("/login"), 1500);
    } catch (err) {
      console.error(err);
      const msg = err?.response?.data?.message;
      if (msg === "TOKEN_INVALIDO") setError("El enlace es inválido.");
      else if (msg === "TOKEN_EXPIRADO")
        setError("El enlace expiró. Solicita uno nuevo.");
      else setError("No se pudo restablecer la contraseña.");
    } finally {
      setLoading(false);
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
                  d="M12 1a5 5 0 0 0-5 5v3H6a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8a2 2 0 0 0-2-2h-1V6a5 5 0 0 0-5-5Zm-3 8V6a3 3 0 0 1 6 0v3H9Z"
                />
              </svg>
            </div>
            <h1 className="text-xl sm:text-2xl font-semibold tracking-tight">
              Restablecer contraseña
            </h1>
            <p className="text-sm text-slate-400 mt-1">
              Ingresa y confirma tu nueva contraseña
            </p>
          </div>

          <ErrorAlert message={error} onClose={() => setError("")} />
          <SuccessAlert message={ok} onClose={() => setOk("")} />

          <label className="block text-sm mb-1.5 text-slate-300">
            Nueva contraseña
          </label>
          <div className="mb-4 flex items-center gap-2 rounded-xl bg-slate-950/60 ring-1 ring-slate-800 focus-within:ring-emerald-400/70 focus-within:bg-slate-900/70 transition">
            <span className="pl-3">
              <svg viewBox="0 0 24 24" className="h-5 w-5 text-slate-400">
                <path
                  fill="currentColor"
                  d="M12 1a5 5 0 0 0-5 5v3H6a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8a2 2 0 0 0-2-2h-1V6a5 5 0 0 0-5-5Z"
                />
              </svg>
            </span>
            <input
              type="password"
              placeholder="••••••••"
              className="w-full bg-transparent outline-none placeholder:text-slate-500 text-slate-100 py-3 pr-3"
              value={nuevaContrasena}
              onChange={(e) => setNuevaContrasena(e.target.value)}
              required
              minLength={8}
            />
          </div>

          <label className="block text-sm mb-1.5 text-slate-300">
            Confirmar contraseña
          </label>
          <div className="mb-6 flex items-center gap-2 rounded-xl bg-slate-950/60 ring-1 ring-slate-800 focus-within:ring-emerald-400/70 focus-within:bg-slate-900/70 transition">
            <span className="pl-3">
              <svg viewBox="0 0 24 24" className="h-5 w-5 text-slate-400">
                <path
                  fill="currentColor"
                  d="M12 1a5 5 0 0 0-5 5v3H6a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8a2 2 0 0 0-2-2h-1V6a5 5 0 0 0-5-5Z"
                />
              </svg>
            </span>
            <input
              type="password"
              placeholder="••••••••"
              className="w-full bg-transparent outline-none placeholder:text-slate-500 text-slate-100 py-3 pr-3"
              value={confirmar}
              onChange={(e) => setConfirmar(e.target.value)}
              required
              minLength={8}
            />
          </div>

          <button
            type="submit"
            disabled={loading || !token}
            className="w-full rounded-xl py-3 font-semibold text-slate-900
                       bg-gradient-to-r from-emerald-400 to-emerald-300
                       hover:from-emerald-300 hover:to-emerald-200
                       shadow-[0_0_0_1px_rgba(16,185,129,0.4),0_10px_20px_-10px_rgba(16,185,129,0.6)]
                       transition disabled:opacity-60"
          >
            {loading ? "Guardando..." : "Restablecer"}
          </button>

          {!token && (
            <p className="text-xs text-amber-300 mt-3">
              Enlace inválido o incompleto. Vuelve a solicitar el correo de
              recuperación.
            </p>
          )}

          <div className="mt-4 h-1 w-full bg-gradient-to-r from-emerald-500/70 via-slate-700 to-rose-500/70 rounded-full" />
        </div>
      </form>
    </div>
  );
}
