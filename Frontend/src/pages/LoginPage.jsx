import { useState, useEffect } from "react";
import { login } from "../api/authService";
import ErrorAlert from "../components/alerts/ErrorAlert";
import SuccessAlert from "../components/alerts/SuccessAlert";

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
      setTimeout(() => (window.location.href = "/"), 1500);
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
    <form onSubmit={onSubmit} className="max-w-sm mx-auto mt-20 space-y-3">
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
      <input
        type="password"
        placeholder="Contraseña"
        className="w-full border p-2 rounded"
        value={contrasena}
        onChange={(e) => setContrasena(e.target.value)}
        required
      />
      <button
        type="submit"
        className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
      >
        Iniciar sesión
      </button>
      <p className="text-center mt-3">
        <a className="text-blue-600 hover:underline" href="/recuperar-password">
          ¿Olvidaste tu contraseña?
        </a>
      </p>
    </form>
  );
}
