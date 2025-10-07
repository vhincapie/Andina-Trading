import { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { restablecerPassword } from "../api/authService";
import PasswordResetForm from "../components/PasswordResetForm";
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
    <div className="max-w-sm mx-auto mt-16">
      <ErrorAlert message={error} onClose={() => setError("")} />
      <SuccessAlert message={ok} onClose={() => setOk("")} />
      <PasswordResetForm
        nuevaContrasena={nuevaContrasena}
        setNuevaContrasena={setNuevaContrasena}
        confirmar={confirmar}
        setConfirmar={setConfirmar}
        loading={loading}
        onSubmit={onSubmit}
      />
    </div>
  );
}
