import { useEffect, useMemo, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { registrarInversionista } from "../api/inversionistaService";
import { listarPaises, listarCiudades } from "../api/catalogosService";
import ErrorAlert from "../components/alerts/ErrorAlert";
import SuccessAlert from "../components/alerts/SuccessAlert";

function fmt(d) {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}-${m}-${day}`;
}

export default function RegistroInversionistaPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    nombre: "",
    apellido: "",
    tipoDocumento: "",
    numeroDocumento: "",
    correo: "",
    contrasena: "",
    fechaNacimiento: "",
    paisId: "",
    ciudadId: "",
  });
  const [paises, setPaises] = useState([]);
  const [ciudades, setCiudades] = useState([]);
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);
  const [ok, setOk] = useState("");
  const [error, setError] = useState("");

  const maxBirthDate = useMemo(() => {
    const today = new Date();
    return fmt(
      new Date(today.getFullYear() - 18, today.getMonth(), today.getDate())
    );
  }, []);
  const minBirthDate = "1900-01-01";

  const ciudadesFiltradas = useMemo(() => {
    if (!form.paisId) return [];
    return ciudades.filter(
      (c) => String(c.paisDTO?.id) === String(form.paisId)
    );
  }, [ciudades, form.paisId]);

  useEffect(() => {
    const load = async () => {
      try {
        const [ps, cs] = await Promise.all([listarPaises(), listarCiudades()]);
        setPaises(ps || []);
        setCiudades(cs || []);
      } catch {
        setError("No se pudieron cargar países y ciudades.");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const onChange = (e) => {
    const { name, value } = e.target;
    if (name === "paisId") {
      setForm((f) => ({ ...f, paisId: value, ciudadId: "" }));
      return;
    }
    if (name === "fechaNacimiento") {
      if (value && (value > maxBirthDate || value < minBirthDate)) {
        setError(`La fecha debe ser entre ${minBirthDate} y ${maxBirthDate}.`);
        return;
      }
    }
    setForm((f) => ({ ...f, [name]: value }));
  };

  const validate = () => {
    if (!form.nombre.trim()) return "El nombre es obligatorio.";
    if (!form.apellido.trim()) return "El apellido es obligatorio.";
    if (!form.tipoDocumento) return "Selecciona un tipo de documento.";
    if (!form.numeroDocumento.trim())
      return "El número de documento es obligatorio.";
    if (!form.correo.trim()) return "El correo es obligatorio.";
    if (form.contrasena.length < 8)
      return "La contraseña debe tener al menos 8 caracteres.";
    if (form.fechaNacimiento) {
      if (form.fechaNacimiento < minBirthDate) {
        return `La fecha de nacimiento no puede ser anterior a ${minBirthDate}.`;
      }
      if (form.fechaNacimiento > maxBirthDate) {
        return "Debes ser mayor de 18 años.";
      }
    }
    if (!form.paisId) return "Selecciona un país.";
    if (!form.ciudadId) return "Selecciona una ciudad.";
    return null;
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setOk("");

    const v = validate();
    if (v) return setError(v);

    setSending(true);
    try {
      const payload = {
        nombre: form.nombre.trim(),
        apellido: form.apellido.trim(),
        tipoDocumento: form.tipoDocumento,
        numeroDocumento: form.numeroDocumento.trim(),
        correo: form.correo.trim().toLowerCase(),
        contrasena: form.contrasena,
        fechaNacimiento: form.fechaNacimiento || null,
        paisId: Number(form.paisId),
        ciudadId: Number(form.ciudadId),
      };
      await registrarInversionista(payload);
      setOk("Registro exitoso. Redirigiendo al inicio de sesión...");
      setTimeout(() => navigate("/login", { replace: true }), 1200);
    } catch (e) {
      const code = e?.response?.data?.code;
      const alpacaMsg = e?.response?.data?.message;
      const msg =
        (code === "alpaca_error" && alpacaMsg) ||
        e?.response?.data?.message ||
        (e?.response?.status === 409 &&
          "Ya existe un inversionista con ese correo o documento.") ||
        "No se pudo registrar el inversionista.";
      setError(msg);
    } finally {
      setSending(false);
    }
  };

  if (loading)
    return (
      <div className="min-h-[60vh] grid place-content-center text-slate-300">
        Cargando…
      </div>
    );

  return (
    <div className="min-h-screen bg-gradient-to-b from-slate-950 via-slate-900 to-slate-950 text-slate-100 flex items-center justify-center px-4 py-10">
      <form onSubmit={onSubmit} className="w-full max-w-3xl relative">
        <div className="absolute -inset-0.5 rounded-2xl bg-gradient-to-r from-emerald-500/20 via-cyan-500/20 to-fuchsia-500/20 blur-lg" />
        <div className="relative rounded-2xl bg-slate-900/70 backdrop-blur-xl ring-1 ring-slate-800 shadow-2xl p-6 sm:p-10">
          <div className="mb-6 text-center">
            <div className="mx-auto mb-3 h-12 w-12 rounded-xl bg-slate-800/80 ring-1 ring-slate-700 grid place-content-center">
              <svg viewBox="0 0 24 24" className="h-6 w-6 text-slate-300">
                <path
                  fill="currentColor"
                  d="M12 12a5 5 0 1 0-5-5 5 5 0 0 0 5 5zm-9 9a9 9 0 0 1 18 0H3z"
                />
              </svg>
            </div>
            <h1 className="text-xl sm:text-2xl font-semibold tracking-tight">
              Registro de Inversionista
            </h1>
            <p className="text-sm text-slate-400 mt-1">
              Crea tu cuenta para acceder al panel de inversión
            </p>
          </div>

          <ErrorAlert message={error} onClose={() => setError("")} />
          <SuccessAlert message={ok} onClose={() => setOk("")} />

          <div className="grid gap-5 md:grid-cols-2">
            <input
              className="bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
              name="nombre"
              placeholder="Nombre"
              value={form.nombre}
              onChange={onChange}
              required
            />
            <input
              className="bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
              name="apellido"
              placeholder="Apellido"
              value={form.apellido}
              onChange={onChange}
              required
            />
          </div>

          <div className="grid gap-5 md:grid-cols-2 mt-5">
            <select
              className="bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
              name="tipoDocumento"
              value={form.tipoDocumento}
              onChange={onChange}
              required
            >
              <option value="">Tipo de documento</option>
              <option value="CC">Cédula de Ciudadanía</option>
              <option value="CE">Cédula de Extranjería</option>
              <option value="PASAPORTE">Pasaporte</option>
            </select>

            <input
              className="bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
              name="numeroDocumento"
              placeholder="Número de documento"
              value={form.numeroDocumento}
              onChange={onChange}
              required
            />
          </div>

          <div className="grid gap-5 md:grid-cols-2 mt-5">
            <input
              type="date"
              className="bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
              name="fechaNacimiento"
              value={form.fechaNacimiento}
              onChange={onChange}
              min={minBirthDate}
              max={maxBirthDate}
            />
            <input
              type="email"
              className="bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
              name="correo"
              placeholder="tucorreo@dominio.com"
              value={form.correo}
              onChange={onChange}
              required
            />
          </div>

          <div className="grid gap-5 md:grid-cols-2 mt-5">
            <div className="md:col-span-2">
              <input
                type="password"
                className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 
                 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                name="contrasena"
                placeholder="Contraseña (mínimo 8 caracteres)"
                value={form.contrasena}
                onChange={onChange}
                required
                minLength={8}
              />
            </div>
          </div>

          <div className="grid gap-5 md:grid-cols-2 mt-5">
            <select
              className="bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
              name="paisId"
              value={form.paisId}
              onChange={onChange}
              required
            >
              <option value="">Selecciona un país</option>
              {paises.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.nombre}
                </option>
              ))}
            </select>

            <select
              className="bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
              name="ciudadId"
              value={form.ciudadId}
              onChange={onChange}
              required
              disabled={!form.paisId}
            >
              <option value="">
                {form.paisId ? "Selecciona una ciudad" : "Primero el país"}
              </option>
              {ciudadesFiltradas.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.nombre}
                </option>
              ))}
            </select>
          </div>

          <button
            type="submit"
            disabled={sending}
            className="w-full mt-8 rounded-xl py-3 font-semibold text-slate-900
                       bg-gradient-to-r from-emerald-400 to-emerald-300
                       hover:from-emerald-300 hover:to-emerald-200
                       shadow-[0_0_0_1px_rgba(16,185,129,0.4),0_10px_20px_-10px_rgba(16,185,129,0.6)]
                       transition disabled:opacity-60"
          >
            {sending ? "Registrando..." : "Registrarme"}
          </button>

          <div className="mt-6 text-center">
            <Link
              to="/login"
              className="inline-flex items-center justify-center gap-2 px-4 py-2 rounded-lg
                         ring-1 ring-slate-700 text-slate-200 hover:bg-slate-800/60
                         transition"
            >
              Volver al inicio de sesión
            </Link>
            <div className="mt-4 h-1 w-full bg-gradient-to-r from-emerald-500/70 via-slate-700 to-rose-500/70 rounded-full" />
          </div>
        </div>
      </form>
    </div>
  );
}
