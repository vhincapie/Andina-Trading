import { useState, useEffect } from "react";
import { solicitarReset } from "../api/authService";
import ErrorAlert from "../components/alerts/ErrorAlert";
import SuccessAlert from "../components/alerts/SuccessAlert";

export default function RecoverPasswordPage() {
  const [correo, setCorreo] = useState("");
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

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setOk("");
    setLoading(true);
    try {
      await solicitarReset(correo.trim().toLowerCase());
      setOk(
        "Si el correo existe, te enviamos un enlace para restablecer tu contraseña."
      );
    } catch (err) {
      console.error("RECUPERAR ERROR =>", err);
      setError("No se pudo enviar el enlace de recuperación.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={onSubmit} className="max-w-sm mx-auto mt-16 space-y-4">
      <h1 className="text-2xl font-semibold text-center">
        Recuperar contraseña
      </h1>
      <ErrorAlert message={error} onClose={() => setError("")} />
      <SuccessAlert message={ok} onClose={() => setOk("")} />
      <input
        type="email"
        placeholder="Correo"
        className="w-full border p-2 rounded"
        value={correo}
        onChange={(e) => setCorreo(e.target.value)}
        required
      />
      <button
        type="submit"
        disabled={loading}
        className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:opacity-60"
      >
        {loading ? "Enviando..." : "Enviar enlace"}
      </button>
    </form>
  );
}
