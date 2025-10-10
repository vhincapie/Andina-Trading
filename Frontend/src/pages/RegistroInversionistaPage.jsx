import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
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
      if (form.fechaNacimiento < minBirthDate)
        return `La fecha de nacimiento no puede ser anterior a ${minBirthDate}.`;
      if (form.fechaNacimiento > maxBirthDate)
        return "Debes ser mayor de 18 años.";
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
      const msg =
        e?.response?.data?.message ||
        (e?.response?.status === 409 &&
          "Ya existe un inversionista con ese correo o documento.") ||
        "No se pudo registrar el inversionista.";
      setError(msg);
    } finally {
      setSending(false);
    }
  };

  if (loading) return <p className="text-center mt-8">Cargando...</p>;

  return (
    <form
      onSubmit={onSubmit}
      className="max-w-md mx-auto mt-8 bg-white border rounded p-4 space-y-3"
    >
      <h1 className="text-xl font-semibold text-center">
        Registro de Inversionista
      </h1>
      <ErrorAlert message={error} onClose={() => setError("")} />
      <SuccessAlert message={ok} onClose={() => setOk("")} />

      <input
        className="w-full border p-2 rounded"
        name="nombre"
        placeholder="Nombre"
        value={form.nombre}
        onChange={onChange}
        required
      />
      <input
        className="w-full border p-2 rounded"
        name="apellido"
        placeholder="Apellido"
        value={form.apellido}
        onChange={onChange}
        required
      />

      <div className="grid gap-3 grid-cols-2">
        <select
          className="border p-2 rounded"
          name="tipoDocumento"
          value={form.tipoDocumento}
          onChange={onChange}
          required
        >
          <option value="">Tipo Documento</option>
          <option value="CC">Cédula de Ciudadanía</option>
          <option value="CE">Cédula de Extranjería</option>
          <option value="PAS">Pasaporte</option>
        </select>
        <input
          className="border p-2 rounded"
          name="numeroDocumento"
          placeholder="Número"
          value={form.numeroDocumento}
          onChange={onChange}
          required
        />
      </div>

      <input
        type="date"
        className="w-full border p-2 rounded"
        name="fechaNacimiento"
        value={form.fechaNacimiento}
        onChange={onChange}
        min={minBirthDate}
        max={maxBirthDate}
      />
      <input
        type="email"
        className="w-full border p-2 rounded"
        name="correo"
        placeholder="Correo electrónico"
        value={form.correo}
        onChange={onChange}
        required
      />
      <input
        type="password"
        className="w-full border p-2 rounded"
        name="contrasena"
        placeholder="Contraseña"
        value={form.contrasena}
        onChange={onChange}
        required
        minLength={8}
      />

      <select
        className="w-full border p-2 rounded"
        name="paisId"
        value={form.paisId}
        onChange={onChange}
        required
      >
        <option value="">País</option>
        {paises.map((p) => (
          <option key={p.id} value={p.id}>
            {p.nombre}
          </option>
        ))}
      </select>

      <select
        className="w-full border p-2 rounded"
        name="ciudadId"
        value={form.ciudadId}
        onChange={onChange}
        required
        disabled={!form.paisId}
      >
        <option value="">
          {form.paisId ? "Ciudad" : "Selecciona primero un país"}
        </option>
        {ciudadesFiltradas.map((c) => (
          <option key={c.id} value={c.id}>
            {c.nombre}
          </option>
        ))}
      </select>

      <button
        type="submit"
        disabled={sending}
        className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:opacity-60"
      >
        {sending ? "Registrando..." : "Registrarme"}
      </button>
    </form>
  );
}
